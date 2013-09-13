package com.thaiopensource.xml.dtd.parse;

import java.io.IOException;
import java.io.Reader;
import java.util.Hashtable;
import java.util.Vector;

import com.thaiopensource.util.Localizer;
import com.thaiopensource.xml.em.EntityManager;
import com.thaiopensource.xml.em.ExternalId;
import com.thaiopensource.xml.em.OpenEntity;
import com.thaiopensource.xml.tok.EmptyTokenException;
import com.thaiopensource.xml.tok.EndOfPrologException;
import com.thaiopensource.xml.tok.ExtensibleTokenException;
import com.thaiopensource.xml.tok.InvalidTokenException;
import com.thaiopensource.xml.tok.PartialTokenException;
import com.thaiopensource.xml.tok.Position;
import com.thaiopensource.xml.tok.TextDecl;
import com.thaiopensource.xml.tok.Token;
import com.thaiopensource.xml.tok.Tokenizer;

class Parser extends Token
{
  static final Localizer localizer = new Localizer (Parser.class);
  private Parser parent;
  private Reader in;
  private char [] buf;
  private int bufStart = 0;
  private int bufEnd;
  private int currentTokenStart = 0;
  // The offset in buffer corresponding to pos.
  private int posOff = 0;
  private long bufEndStreamOffset = 0;
  private final Position pos = new Position ();

  private static final int READSIZE = 1024 * 8;
  // Some temporary buffers
  private final ReplacementTextBuffer valueBuf;
  private final DtdBuilder db;
  private final Vector <Atom> atoms = new Vector <Atom> ();
  private final boolean isInternal;
  private final String baseUri;
  private final EntityManager entityManager;
  // for error messages
  private String location;

  private final Hashtable <String, Atom> atomTable;
  private final Hashtable <String, String> elementTable;

  static class DeclState
  {
    Entity entity;
    Notation notation;
  }

  Parser (final OpenEntity entity, final EntityManager entityManager)
  {
    this.in = entity.getReader ();
    this.baseUri = entity.getBaseUri ();
    this.location = entity.getLocation ();
    this.entityManager = entityManager;
    this.buf = new char [READSIZE * 2];
    this.valueBuf = new ReplacementTextBuffer ();
    this.bufEnd = 0;
    this.db = new DtdBuilder (atoms);
    this.isInternal = false;
    this.elementTable = new Hashtable <String, String> ();
    this.atomTable = new Hashtable <String, Atom> ();
  }

  private Parser (final OpenEntity entity, final Parser parent)
  {
    this.in = entity.getReader ();
    this.baseUri = entity.getBaseUri ();
    this.location = entity.getLocation ();
    this.entityManager = parent.entityManager;
    this.parent = parent;
    this.buf = new char [READSIZE * 2];
    this.valueBuf = new ReplacementTextBuffer ();
    this.bufEnd = 0;
    this.db = parent.db;
    this.isInternal = false;
    this.elementTable = parent.elementTable;
    this.atomTable = parent.atomTable;
  }

  private Parser (final char [] buf, final String entityName, final Parser parent)
  {
    // this.internalEntityName = entityName;
    this.buf = buf;
    this.parent = parent;
    this.baseUri = parent.baseUri;
    this.entityManager = parent.entityManager;
    this.bufEnd = buf.length;
    this.bufEndStreamOffset = buf.length;
    this.valueBuf = parent.valueBuf;
    this.db = parent.db;
    this.isInternal = true;
    this.elementTable = parent.elementTable;
    this.atomTable = parent.atomTable;
  }

  DtdBuilder parse () throws IOException
  {
    skipTextDecl ();
    parseDecls (false);
    return db;
  }

  private void parseDecls (final boolean isInternal) throws IOException
  {
    final PrologParser pp = new PrologParser (isInternal ? PrologParser.INTERNAL_ENTITY : PrologParser.EXTERNAL_ENTITY);
    final DeclState declState = new DeclState ();
    try
    {
      for (;;)
      {
        int tok;
        try
        {
          tok = tokenizeProlog ();
        }
        catch (final EndOfPrologException e)
        {
          fatal ("SYNTAX_ERROR");
          break;
        }
        catch (final EmptyTokenException e)
        {
          pp.end ();
          break;
        }
        prologAction (tok, pp, declState);
      }
    }
    catch (final PrologSyntaxException e)
    {
      fatal ("SYNTAX_ERROR");
    }
    finally
    {
      if (!isInternal && in != null)
      {
        in.close ();
        in = null;
      }
    }
  }

  private void prologAction (final int tok, final PrologParser pp, final DeclState declState) throws IOException,
                                                                                             PrologSyntaxException
  {
    final Atom a = makeAtom (tok, currentTokenStart, bufStart);
    addAtom (a);
    final String token = a.getToken ();
    final int action = pp.action (tok, token);
    switch (action)
    {
      case PrologParser.ACTION_IGNORE_SECT:
        skipIgnoreSect ();
        break;
      case PrologParser.ACTION_GENERAL_ENTITY_NAME:
        declState.entity = db.createGeneralEntity (token);
        break;
      case PrologParser.ACTION_PARAM_ENTITY_NAME:
        declState.entity = db.createParamEntity (token);
        break;
      case PrologParser.ACTION_ENTITY_PUBLIC_ID:
        try
        {
          declState.entity.publicId = Tokenizer.getPublicId (buf, currentTokenStart, bufStart);
        }
        catch (final InvalidTokenException e)
        {
          currentTokenStart = e.getOffset ();
          fatal ("INVALID_PUBLIC_ID");
        }
        break;
      case PrologParser.ACTION_ENTITY_SYSTEM_ID:
        declState.entity.systemId = token.substring (1, token.length () - 1);
        declState.entity.baseUri = baseUri;
        break;
      case PrologParser.ACTION_ENTITY_NOTATION_NAME:
        declState.entity.notationName = token;
        break;
      case PrologParser.ACTION_ENTITY_VALUE_WITH_PEREFS:
        makeReplacementText ();
        declState.entity.text = valueBuf.getChars ();
        declState.entity.entityValue = token.substring (1, token.length () - 1);
        declState.entity.mustReparse = valueBuf.getMustReparse ();
        declState.entity.references = valueBuf.getReferences ();
        if (declState.entity.mustReparse)
          declState.entity.problem = Entity.REPARSE_PROBLEM;
        else
          if (declState.entity.overridden && declState.entity.isParameter)
            declState.entity.atoms = tokenizeOverriddenEntity (declState.entity.text);
        break;
      case PrologParser.ACTION_INNER_PARAM_ENTITY_REF:
      case PrologParser.ACTION_OUTER_PARAM_ENTITY_REF:
      {
        final int nameStart = currentTokenStart + 1;
        final String name = new String (buf, nameStart, getNameEnd () - nameStart);
        final Entity entity = db.lookupParamEntity (name);
        if (entity == null)
        {
          fatal ("UNDEF_PEREF", name);
          break;
        }
        final Parser parser = makeParserForEntity (entity, name);
        if (parser == null)
        {
          // XXX
          break;
        }
        entity.open = true;
        if (action == PrologParser.ACTION_OUTER_PARAM_ENTITY_REF)
          parser.parseDecls (entity.text != null);
        else
          parser.parseInnerParamEntity (pp, declState);
        entity.atoms = parser.atoms;
        setLastAtomEntity (entity);
        entity.open = false;
        break;
      }
      case PrologParser.ACTION_ELEMENT_NAME:
        if (elementTable.get (token) != null)
          fatal ("DUPLICATE_ELEMENT", token);
        elementTable.put (token, token);
        break;
      case PrologParser.ACTION_NOTATION_NAME:
        declState.notation = db.createNotation (token);
        if (declState.notation == null)
          fatal ("DUPLICATE_NOTATION", token);
        break;
      case PrologParser.ACTION_NOTATION_PUBLIC_ID:
        try
        {
          declState.notation.publicId = Tokenizer.getPublicId (buf, currentTokenStart, bufStart);
        }
        catch (final InvalidTokenException e)
        {
          currentTokenStart = e.getOffset ();
          fatal ("INVALID_PUBLIC_ID");
        }
        break;
      case PrologParser.ACTION_NOTATION_SYSTEM_ID:
        declState.notation.systemId = token.substring (1, token.length () - 1);
        declState.notation.baseUri = baseUri;
        break;
      case PrologParser.ACTION_DEFAULT_ATTRIBUTE_VALUE:
      {
        final String origValue = token.substring (1, token.length () - 1);
        if (db.getNormalized (origValue) != null)
          break;
        final StringBuffer tem = new StringBuffer ();
        try
        {
          normalizeAttributeValue (buf, currentTokenStart + 1, bufStart - 1, tem);
        }
        catch (final AttributeValueException e)
        {
          currentTokenStart = e.offset;
          if (e.arg != null)
            fatal (e.key, e.arg);
          else
            fatal (e.key);
        }
        db.setNormalized (origValue, tem.toString ());
        break;
      }
    }
  }

  void parseInnerParamEntity (final PrologParser pp, final DeclState declState) throws IOException
  {
    final int groupLevel = pp.getGroupLevel ();
    try
    {
      for (;;)
      {
        final int tok = tokenizeProlog ();
        prologAction (tok, pp, declState);
        switch (tok)
        {
          case Tokenizer.TOK_DECL_CLOSE:
          case Tokenizer.TOK_OPEN_BRACKET:
            fatal ("PE_DECL_NESTING");
        }
      }
    }
    catch (final EndOfPrologException e)
    {
      fatal ("SYNTAX_ERROR");
    }
    catch (final PrologSyntaxException e)
    {
      fatal ("SYNTAX_ERROR");
    }
    catch (final EmptyTokenException e)
    {}
    if (pp.getGroupLevel () != groupLevel)
      fatal ("PE_GROUP_NESTING");
  }

  private Parser makeParserForEntity (final Entity entity, final String name) throws IOException
  {
    entity.noteReferenced ();
    if (entity.open)
      fatal ("RECURSION");
    if (entity.notationName != null)
      fatal ("UNPARSED_REF");
    if (entity.text != null)
      return new Parser (entity.text, name, this);

    final OpenEntity openEntity = entityManager.open (new ExternalId (entity.systemId, entity.publicId, entity.baseUri),
                                                      entity.isParameter,
                                                      entity.name);
    if (openEntity == null)
      return null;
    entity.encoding = openEntity.getEncoding ();
    entity.uri = openEntity.getBaseUri ();
    final Parser p = new Parser (openEntity, this);
    p.skipTextDecl ();
    return p;
  }

  /*
   * Make the replacement text for an entity out of the literal in the current
   * token.
   */
  private void makeReplacementText () throws IOException
  {
    valueBuf.clear ();
    final Token t = new Token ();
    int start = currentTokenStart + 1;
    final int end = bufStart - 1;
    try
    {
      for (;;)
      {
        int tok;
        int nextStart;
        try
        {
          tok = Tokenizer.tokenizeEntityValue (buf, start, end, t);
          nextStart = t.getTokenEnd ();
        }
        catch (final ExtensibleTokenException e)
        {
          tok = e.getTokenType ();
          nextStart = end;
        }
        handleEntityValueToken (valueBuf, tok, start, nextStart, t);
        start = nextStart;
      }
    }
    catch (final PartialTokenException e)
    {
      currentTokenStart = end;
      fatal ("NOT_WELL_FORMED");
    }
    catch (final InvalidTokenException e)
    {
      currentTokenStart = e.getOffset ();
      reportInvalidToken (e);
    }
    catch (final EmptyTokenException e)
    {}
  }

  private void parseEntityValue (final ReplacementTextBuffer value) throws IOException
  {
    final Token t = new Token ();
    for (;;)
    {
      int tok;
      for (;;)
      {
        try
        {
          tok = Tokenizer.tokenizeEntityValue (buf, bufStart, bufEnd, t);
          currentTokenStart = bufStart;
          bufStart = t.getTokenEnd ();
          break;
        }
        catch (final EmptyTokenException e)
        {
          if (!fill ())
            return;
        }
        catch (final PartialTokenException e)
        {
          if (!fill ())
          {
            currentTokenStart = bufStart;
            bufStart = bufEnd;
            fatal ("UNCLOSED_TOKEN");
          }
        }
        catch (final ExtensibleTokenException e)
        {
          if (!fill ())
          {
            currentTokenStart = bufStart;
            bufStart = bufEnd;
            tok = e.getTokenType ();
            break;
          }
        }
        catch (final InvalidTokenException e)
        {
          currentTokenStart = e.getOffset ();
          reportInvalidToken (e);
        }
      }
      handleEntityValueToken (value, tok, currentTokenStart, bufStart, t);
    }
  }

  private void handleEntityValueToken (final ReplacementTextBuffer value,
                                       final int tok,
                                       final int start,
                                       final int end,
                                       final Token t) throws IOException
  {
    switch (tok)
    {
      case Tokenizer.TOK_DATA_NEWLINE:
        if (!isInternal)
        {
          value.append ('\n');
          break;
        }
        // fall through
      case Tokenizer.TOK_DATA_CHARS:
      case Tokenizer.TOK_ENTITY_REF:
      case Tokenizer.TOK_MAGIC_ENTITY_REF:
        value.append (buf, start, end);
        break;
      case Tokenizer.TOK_CHAR_REF:
      {
        final char c = t.getRefChar ();
        if (c == '&' || c == '%')
          value.setMustReparse ();
        value.append (t.getRefChar ());
      }
        break;
      case Tokenizer.TOK_CHAR_PAIR_REF:
        value.appendRefCharPair (t);
        break;
      case Tokenizer.TOK_PARAM_ENTITY_REF:
        final String name = new String (buf, start + 1, end - start - 2);
        final Entity entity = db.lookupParamEntity (name);
        if (entity == null)
        {
          fatal ("UNDEF_PEREF", name);
          break;
        }
        if (entity.text != null && !entity.mustReparse)
        {
          entity.noteReferenced ();
          value.appendReplacementText (entity);
        }
        else
        {
          final Parser parser = makeParserForEntity (entity, name);
          if (parser != null)
          {
            entity.open = true;
            parser.parseEntityValue (value);
            entity.open = false;
          }
        }
        break;
      default:
        throw new Error ("replacement text botch");
    }
  }

  private void skipTextDecl () throws IOException
  {
    try
    {
      if (tokenizeProlog () != Tokenizer.TOK_XML_DECL)
      {
        currentTokenStart = bufStart = 0;
        return;
      }
      try
      {
        new TextDecl (buf, currentTokenStart, bufStart);
      }
      catch (final InvalidTokenException e)
      {
        currentTokenStart = e.getOffset ();
        fatal ("INVALID_TEXT_DECL");
      }
    }
    catch (final EmptyTokenException e)
    {}
    catch (final EndOfPrologException e)
    {}
  }

  private final int tokenizeProlog () throws IOException, EmptyTokenException, EndOfPrologException
  {
    for (;;)
    {
      try
      {
        final int tok = Tokenizer.tokenizeProlog (buf, bufStart, bufEnd, this);
        currentTokenStart = bufStart;
        bufStart = getTokenEnd ();
        return tok;
      }
      catch (final EmptyTokenException e)
      {
        if (!fill ())
          throw e;
      }
      catch (final PartialTokenException e)
      {
        if (!fill ())
        {
          currentTokenStart = bufStart;
          bufStart = bufEnd;
          fatal ("UNCLOSED_TOKEN");
        }
      }
      catch (final ExtensibleTokenException e)
      {
        if (!fill ())
        {
          currentTokenStart = bufStart;
          bufStart = bufEnd;
          return e.getTokenType ();
        }
      }
      catch (final InvalidTokenException e)
      {
        bufStart = currentTokenStart = e.getOffset ();
        reportInvalidToken (e);
      }
    }
  }

  private final void skipIgnoreSect () throws IOException
  {
    for (;;)
    {
      try
      {
        final int sectStart = bufStart;
        bufStart = Tokenizer.skipIgnoreSect (buf, bufStart, bufEnd);
        addAtom (new Atom (Tokenizer.TOK_COND_SECT_CLOSE, bufferString (sectStart, bufStart)));
        return;
      }
      catch (final PartialTokenException e)
      {
        if (!fill ())
        {
          currentTokenStart = bufStart;
          fatal ("UNCLOSED_CONDITIONAL_SECTION");
        }
      }
      catch (final InvalidTokenException e)
      {
        currentTokenStart = e.getOffset ();
        fatal ("IGNORE_SECT_CHAR");
      }
    }
  }

  private Vector <Atom> tokenizeOverriddenEntity (final char [] text)
  {
    final Vector <Atom> v = new Vector <Atom> ();
    int level = 0;
    try
    {
      final Token t = new Token ();
      int start = 0;
      for (;;)
      {
        int tok;
        int tokenEnd;
        try
        {
          tok = Tokenizer.tokenizeProlog (text, start, text.length, t);
          tokenEnd = t.getTokenEnd ();
        }
        catch (final ExtensibleTokenException e)
        {
          tok = e.getTokenType ();
          tokenEnd = text.length;
        }
        switch (tok)
        {
          case Tokenizer.TOK_COND_SECT_OPEN:
          case Tokenizer.TOK_OPEN_PAREN:
          case Tokenizer.TOK_OPEN_BRACKET:
          case Tokenizer.TOK_DECL_OPEN:
            level++;
            break;
          case Tokenizer.TOK_CLOSE_PAREN:
          case Tokenizer.TOK_CLOSE_PAREN_ASTERISK:
          case Tokenizer.TOK_CLOSE_PAREN_QUESTION:
          case Tokenizer.TOK_CLOSE_PAREN_PLUS:
          case Tokenizer.TOK_CLOSE_BRACKET:
          case Tokenizer.TOK_DECL_CLOSE:
            if (--level < 0)
              return null;
            break;
          case Tokenizer.TOK_COND_SECT_CLOSE:
            if ((level -= 2) < 0)
              return null;
            break;
        }
        v.addElement (new Atom (tok, new String (text, start, tokenEnd - start)));

        start = tokenEnd;
      }
    }
    catch (final EmptyTokenException e)
    {
      if (level != 0)
        return null;
      return v;
    }
    catch (final EndOfPrologException e)
    {}
    catch (final PartialTokenException e)
    {}
    catch (final InvalidTokenException e)
    {}
    return null;
  }

  /*
   * The size of the buffer is always a multiple of READSIZE. We do reads so
   * that a complete read would end at the end of the buffer. Unless there has
   * been an incomplete read, we always read in multiples of READSIZE.
   */
  private boolean fill () throws IOException
  {
    if (in == null)
      return false;
    if (bufEnd == buf.length)
    {
      Tokenizer.movePosition (buf, posOff, bufStart, pos);
      /* The last read was complete. */
      final int keep = bufEnd - bufStart;
      if (keep == 0)
        bufEnd = 0;
      else
        if (keep + READSIZE <= buf.length)
        {
          /*
           * There is space in the buffer for at least READSIZE bytes. Choose
           * bufEnd so that it is the least non-negative integer greater than or
           * equal to <code>keep</code>, such <code>bufLength - keep</code> is a
           * multiple of READSIZE.
           */
          bufEnd = buf.length - (((buf.length - keep) / READSIZE) * READSIZE);
          for (int i = 0; i < keep; i++)
            buf[bufEnd - keep + i] = buf[bufStart + i];
        }
        else
        {
          final char newBuf[] = new char [buf.length << 1];
          bufEnd = buf.length;
          System.arraycopy (buf, bufStart, newBuf, bufEnd - keep, keep);
          buf = newBuf;
        }
      bufStart = bufEnd - keep;
      posOff = bufStart;
    }
    final int nChars = in.read (buf, bufEnd, buf.length - bufEnd);
    if (nChars < 0)
    {
      in.close ();
      in = null;
      return false;
    }
    bufEnd += nChars;
    bufEndStreamOffset += nChars;
    return true;
  }

  private void fatal (final String key, final String arg) throws ParseException
  {
    doFatal (localizer.message (key, arg));
  }

  private void fatal (final String key) throws ParseException
  {
    doFatal (localizer.message (key));
  }

  private void doFatal (final String message) throws ParseException
  {
    if (isInternal)
      parent.doFatal (message);
    if (posOff > currentTokenStart)
      throw new Error ("positioning botch");
    Tokenizer.movePosition (buf, posOff, currentTokenStart, pos);
    posOff = currentTokenStart;
    throw new ParseException (localizer, message, location, pos.getLineNumber (), pos.getColumnNumber ());
  }

  private void reportInvalidToken (final InvalidTokenException e) throws ParseException
  {
    if (e.getType () == InvalidTokenException.XML_TARGET)
      fatal ("XML_TARGET");
    else
      fatal ("ILLEGAL_CHAR");
  }

  private void addAtom (final Atom a)
  {
    atoms.addElement (a);
  }

  private void setLastAtomEntity (final Entity e)
  {
    final Atom a = atoms.elementAt (atoms.size () - 1);
    atoms.setElementAt (new Atom (a.getTokenType (), a.getToken (), e), atoms.size () - 1);
  }

  private final String bufferString (final int start, final int end)
  {
    return normalizeNewlines (new String (buf, start, end - start));
  }

  private final String normalizeNewlines (final String str)
  {
    if (isInternal)
      return str;
    int i = str.indexOf ('\r');
    if (i < 0)
      return str;
    final StringBuffer buf = new StringBuffer ();
    for (i = 0; i < str.length (); i++)
    {
      final char c = str.charAt (i);
      if (c == '\r')
      {
        buf.append ('\n');
        if (i + 1 < str.length () && str.charAt (i + 1) == '\n')
          i++;
      }
      else
        buf.append (c);
    }
    return buf.toString ();
  }

  static class AttributeValueException extends Exception
  {
    final int offset;
    final String key;
    final String arg;

    AttributeValueException (final String key, final int offset)
    {
      this.key = key;
      this.arg = null;
      this.offset = offset;
    }

    AttributeValueException (final String key, final String arg, final int offset)
    {
      this.key = key;
      this.arg = arg;
      this.offset = offset;
    }
  }

  private void normalizeAttributeValue (final char [] b, int start, final int end, final StringBuffer result) throws AttributeValueException
  {
    final Token t = new Token ();
    for (;;)
    {
      int tok;
      int nextStart;
      try
      {
        tok = Tokenizer.tokenizeAttributeValue (b, start, end, t);
        nextStart = t.getTokenEnd ();
      }
      catch (final PartialTokenException e)
      {
        throw new AttributeValueException ("NOT_WELL_FORMED", end);
      }
      catch (final InvalidTokenException e)
      {
        throw new AttributeValueException ("ILLEGAL_CHAR", e.getOffset ());
      }
      catch (final EmptyTokenException e)
      {
        return;
      }
      catch (final ExtensibleTokenException e)
      {
        tok = e.getTokenType ();
        nextStart = end;
      }
      switch (tok)
      {
        case Tokenizer.TOK_DATA_NEWLINE:
          if (b == buf && !isInternal)
            result.append (' ');
          else
          {
            for (int i = start; i < nextStart; i++)
              result.append (' ');
          }
          break;
        case Tokenizer.TOK_DATA_CHARS:
          result.append (b, start, nextStart - start);
          break;
        case Tokenizer.TOK_MAGIC_ENTITY_REF:
        case Tokenizer.TOK_CHAR_REF:
          result.append (t.getRefChar ());
          break;
        case Tokenizer.TOK_CHAR_PAIR_REF:
        {
          final char [] pair = new char [2];
          t.getRefCharPair (pair, 0);
          result.append (pair);
        }
          break;
        case Tokenizer.TOK_ATTRIBUTE_VALUE_S:
          result.append (' ');
          break;
        case Tokenizer.TOK_ENTITY_REF:
          final String name = new String (b, start + 1, nextStart - start - 2);
          final Entity entity = db.lookupGeneralEntity (name);
          if (entity == null)
            throw new AttributeValueException ("UNDEF_REF", name, start);
          if (entity.systemId != null)
            throw new AttributeValueException ("EXTERN_REF_ATTVAL", name, start);
          try
          {
            if (entity.open)
              throw new AttributeValueException ("RECURSION", start);
            entity.open = true;
            normalizeAttributeValue (entity.text, 0, entity.text.length, result);
            entity.open = false;
          }
          catch (final AttributeValueException e)
          {
            throw new AttributeValueException (e.key, e.arg, start);
          }
          break;
        default:
          throw new Error ("attribute value botch");
      }
      start = nextStart;
    }
  }

  private Atom makeAtom (final int tok, final int start, final int end)
  {
    String token = null;
    if (end - start == 1)
    {
      switch (buf[start])
      {
        case ' ':
          token = " ";
          break;
        case '\t':
          token = "\t";
          break;
        case '\n':
          token = "\n";
          break;
        case ',':
          token = ",";
          break;
        case '|':
          token = "|";
          break;
        case '(':
          token = "(";
          break;
        case ')':
          token = ")";
          break;
      }
    }
    else
      if (end - start == 2 && buf[start] == '\r' && buf[start + 1] == '\n')
        token = "\n";
    if (token == null)
      token = bufferString (start, end);
    Atom a = atomTable.get (token);
    if (a == null)
    {
      a = new Atom (tok, token);
      atomTable.put (token, a);
    }
    return a;
  }

}
