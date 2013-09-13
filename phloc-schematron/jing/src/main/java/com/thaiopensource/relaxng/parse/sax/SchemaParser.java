package com.thaiopensource.relaxng.parse.sax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.thaiopensource.relaxng.parse.Annotations;
import com.thaiopensource.relaxng.parse.CommentList;
import com.thaiopensource.relaxng.parse.Context;
import com.thaiopensource.relaxng.parse.DataPatternBuilder;
import com.thaiopensource.relaxng.parse.Div;
import com.thaiopensource.relaxng.parse.ElementAnnotationBuilder;
import com.thaiopensource.relaxng.parse.Grammar;
import com.thaiopensource.relaxng.parse.GrammarSection;
import com.thaiopensource.relaxng.parse.IllegalSchemaException;
import com.thaiopensource.relaxng.parse.Include;
import com.thaiopensource.relaxng.parse.IncludedGrammar;
import com.thaiopensource.relaxng.parse.ParsedPatternFuture;
import com.thaiopensource.relaxng.parse.SchemaBuilder;
import com.thaiopensource.relaxng.parse.Scope;
import com.thaiopensource.util.Localizer;
import com.thaiopensource.util.Uri;
import com.thaiopensource.xml.sax.AbstractLexicalHandler;
import com.thaiopensource.xml.sax.XmlBaseHandler;
import com.thaiopensource.xml.util.Naming;
import com.thaiopensource.xml.util.WellKnownNamespaces;

class SchemaParser <Pattern, NameClass, Location, ElementAnnotation, CommentListImpl extends CommentList <Location>, AnnotationsImpl extends Annotations <Location, ElementAnnotation, CommentListImpl>> implements
                                                                                                                                                                                                         ParsedPatternFuture <Pattern>
{
  private static final String relaxngURIPrefix = WellKnownNamespaces.RELAX_NG.substring (0,
                                                                                         WellKnownNamespaces.RELAX_NG.lastIndexOf ('/') + 1);
  static final String relaxng10URI = WellKnownNamespaces.RELAX_NG;
  private static final Localizer localizer = new Localizer (SchemaParser.class);

  private String relaxngURI;
  private final XMLReader xr;
  private final ErrorHandler eh;
  private final SchemaBuilder <Pattern, NameClass, Location, ElementAnnotation, CommentListImpl, AnnotationsImpl> schemaBuilder;
  private Pattern startPattern;
  private Locator locator;
  private final XmlBaseHandler xmlBaseHandler = new XmlBaseHandler ();
  private final ContextImpl context = new ContextImpl ();

  private boolean hadError = false;

  private Map <String, State> patternMap;
  private Map <String, State> nameClassMap;

  static class PrefixMapping
  {
    final String prefix;
    final String uri;
    final PrefixMapping next;

    PrefixMapping (final String prefix, final String uri, final PrefixMapping next)
    {
      this.prefix = prefix;
      this.uri = uri;
      this.next = next;
    }
  }

  static abstract class AbstractContext extends DtdContext implements Context
  {
    PrefixMapping prefixMapping;

    AbstractContext ()
    {
      prefixMapping = new PrefixMapping ("xml", WellKnownNamespaces.XML, null);
    }

    AbstractContext (final AbstractContext context)
    {
      super (context);
      prefixMapping = context.prefixMapping;
    }

    public String resolveNamespacePrefix (final String prefix)
    {
      for (PrefixMapping p = prefixMapping; p != null; p = p.next)
        if (p.prefix.equals (prefix))
          return p.uri;
      return null;
    }

    public Set <String> prefixes ()
    {
      final Set <String> set = new HashSet <String> ();
      for (PrefixMapping p = prefixMapping; p != null; p = p.next)
        set.add (p.prefix);
      return set;
    }

    public Context copy ()
    {
      return new SavedContext (this);
    }
  }

  static class SavedContext extends AbstractContext
  {
    private final String baseUri;

    SavedContext (final AbstractContext context)
    {
      super (context);
      this.baseUri = context.getBaseUri ();
    }

    public String getBaseUri ()
    {
      return baseUri;
    }
  }

  class ContextImpl extends AbstractContext
  {
    public String getBaseUri ()
    {
      return xmlBaseHandler.getBaseUri ();
    }
  }

  static interface CommentHandler
  {
    void comment (String value);
  }

  abstract class Handler implements ContentHandler, CommentHandler
  {
    CommentListImpl comments;

    CommentListImpl getComments ()
    {
      final CommentListImpl tem = comments;
      comments = null;
      return tem;
    }

    public void comment (final String value)
    {
      if (comments == null)
        comments = schemaBuilder.makeCommentList ();
      comments.addComment (value, makeLocation ());
    }

    public void processingInstruction (final String target, final String date)
    {}

    public void skippedEntity (final String name)
    {}

    public void ignorableWhitespace (final char [] ch, final int start, final int len)
    {}

    public void startDocument ()
    {}

    public void endDocument ()
    {}

    public void startPrefixMapping (final String prefix, final String uri)
    {
      context.prefixMapping = new PrefixMapping (prefix, uri, context.prefixMapping);
    }

    public void endPrefixMapping (final String prefix)
    {
      context.prefixMapping = context.prefixMapping.next;
    }

    public void setDocumentLocator (final Locator loc)
    {
      locator = loc;
      xmlBaseHandler.setLocator (loc);
    }
  }

  abstract class State extends Handler
  {
    State parent;
    String nsInherit;
    String ns;
    String datatypeLibrary;
    Scope <Pattern, Location, ElementAnnotation, CommentListImpl, AnnotationsImpl> scope;
    Location startLocation;
    AnnotationsImpl annotations;

    void set ()
    {
      xr.setContentHandler (this);
    }

    abstract State create ();

    abstract State createChildState (String localName) throws SAXException;

    RootState toRootState ()
    {
      return null;
    }

    NameClassChoiceState toNameClassChoiceState ()
    {
      return null;
    }

    void setParent (final State parent)
    {
      this.parent = parent;
      this.nsInherit = parent.getNs ();
      this.datatypeLibrary = parent.datatypeLibrary;
      this.scope = parent.scope;
      this.startLocation = makeLocation ();
      if (parent.comments != null)
      {
        annotations = schemaBuilder.makeAnnotations (parent.comments, getContext ());
        parent.comments = null;
      }
      else
        if (parent.toRootState () != null)
          annotations = schemaBuilder.makeAnnotations (null, getContext ());
    }

    String getNs ()
    {
      return ns == null ? nsInherit : ns;
    }

    boolean isRelaxNGElement (final String uri) throws SAXException
    {
      return uri.equals (relaxngURI);
    }

    public void startElement (final String namespaceURI,
                              final String localName,
                              final String qName,
                              final Attributes atts) throws SAXException
    {
      xmlBaseHandler.startElement ();
      if (isRelaxNGElement (namespaceURI))
      {
        final State state = createChildState (localName);
        if (state == null)
        {
          xr.setContentHandler (new Skipper (this));
          return;
        }
        state.setParent (this);
        state.set ();
        state.attributes (atts);
      }
      else
      {
        checkForeignElement ();
        final ForeignElementHandler feh = new ForeignElementHandler (this, getComments ());
        feh.startElement (namespaceURI, localName, qName, atts);
        xr.setContentHandler (feh);
      }
    }

    public void endElement (final String namespaceURI, final String localName, final String qName) throws SAXException
    {
      xmlBaseHandler.endElement ();
      parent.set ();
      end ();
    }

    void setName (final String name) throws SAXException
    {
      error ("illegal_name_attribute");
    }

    void setOtherAttribute (final String name, final String value) throws SAXException
    {
      error ("illegal_attribute_ignored", name);
    }

    void endAttributes () throws SAXException
    {}

    void checkForeignElement () throws SAXException
    {}

    void attributes (final Attributes atts) throws SAXException
    {
      final int len = atts.getLength ();
      for (int i = 0; i < len; i++)
      {
        final String uri = atts.getURI (i);
        if (uri.length () == 0)
        {
          final String name = atts.getLocalName (i);
          if (name.equals ("name"))
            setName (atts.getValue (i).trim ());
          else
            if (name.equals ("ns"))
              ns = atts.getValue (i);
            else
              if (name.equals ("datatypeLibrary"))
              {
                datatypeLibrary = atts.getValue (i);
                checkUri (datatypeLibrary);
                if (!datatypeLibrary.equals ("") && !Uri.isAbsolute (datatypeLibrary))
                  error ("relative_datatype_library");
                if (Uri.hasFragmentId (datatypeLibrary))
                  error ("fragment_identifier_datatype_library");
                datatypeLibrary = Uri.escapeDisallowedChars (datatypeLibrary);
              }
              else
                setOtherAttribute (name, atts.getValue (i));
        }
        else
          if (uri.equals (relaxngURI))
            error ("qualified_attribute", atts.getLocalName (i));
          else
            if (uri.equals (WellKnownNamespaces.XML) && atts.getLocalName (i).equals ("base"))
              xmlBaseHandler.xmlBaseAttribute (atts.getValue (i));
            else
            {
              if (annotations == null)
                annotations = schemaBuilder.makeAnnotations (null, getContext ());
              annotations.addAttribute (uri,
                                        atts.getLocalName (i),
                                        findPrefix (atts.getQName (i), uri),
                                        atts.getValue (i),
                                        startLocation);
            }
      }
      endAttributes ();
    }

    abstract void end () throws SAXException;

    void endPatternChild (final Pattern pattern)
    {
      // XXX cannot happen; throw exception
    }

    void endNameClassChild (final NameClass nc)
    {
      // XXX cannot happen; throw exception
    }

    @Override
    public void startDocument ()
    {}

    @Override
    public void endDocument ()
    {
      if (comments != null && startPattern != null)
      {
        startPattern = schemaBuilder.commentAfterPattern (startPattern, comments);
        comments = null;
      }
    }

    public void characters (final char [] ch, final int start, final int len) throws SAXException
    {
      for (int i = 0; i < len; i++)
      {
        switch (ch[start + i])
        {
          case ' ':
          case '\r':
          case '\n':
          case '\t':
            break;
          default:
            error ("illegal_characters_ignored");
            break;
        }
      }
    }

    boolean isPatternNamespaceURI (final String s)
    {
      return s.equals (relaxngURI);
    }

    void endForeignChild (final ElementAnnotation ea)
    {
      if (annotations == null)
        annotations = schemaBuilder.makeAnnotations (null, getContext ());
      annotations.addElement (ea);
    }

    void mergeLeadingComments ()
    {
      if (comments != null)
      {
        if (annotations == null)
          annotations = schemaBuilder.makeAnnotations (comments, getContext ());
        else
          annotations.addLeadingComment (comments);
        comments = null;
      }
    }
  }

  class ForeignElementHandler extends Handler
  {
    final State nextState;
    ElementAnnotationBuilder <Location, ElementAnnotation, CommentListImpl> builder;
    final Stack <ElementAnnotationBuilder <Location, ElementAnnotation, CommentListImpl>> builderStack = new Stack <ElementAnnotationBuilder <Location, ElementAnnotation, CommentListImpl>> ();
    StringBuffer textBuf;
    Location textLoc;

    ForeignElementHandler (final State nextState, final CommentListImpl comments)
    {
      this.nextState = nextState;
      this.comments = comments;
    }

    public void startElement (final String namespaceURI,
                              final String localName,
                              final String qName,
                              final Attributes atts)
    {
      flushText ();
      if (builder != null)
        builderStack.push (builder);
      final Location loc = makeLocation ();
      builder = schemaBuilder.makeElementAnnotationBuilder (namespaceURI,
                                                            localName,
                                                            findPrefix (qName, namespaceURI),
                                                            loc,
                                                            getComments (),
                                                            getContext ());
      final int len = atts.getLength ();
      for (int i = 0; i < len; i++)
      {
        final String uri = atts.getURI (i);
        builder.addAttribute (uri, atts.getLocalName (i), findPrefix (atts.getQName (i), uri), atts.getValue (i), loc);
      }
    }

    public void endElement (final String namespaceURI, final String localName, final String qName)
    {
      flushText ();
      if (comments != null)
        builder.addComment (getComments ());
      final ElementAnnotation ea = builder.makeElementAnnotation ();
      if (builderStack.empty ())
      {
        nextState.endForeignChild (ea);
        nextState.set ();
      }
      else
      {
        builder = builderStack.pop ();
        builder.addElement (ea);
      }
    }

    public void characters (final char ch[], final int start, final int length)
    {
      if (textBuf == null)
        textBuf = new StringBuffer ();
      textBuf.append (ch, start, length);
      if (textLoc == null)
        textLoc = makeLocation ();
    }

    @Override
    public void comment (final String value)
    {
      flushText ();
      super.comment (value);
    }

    void flushText ()
    {
      if (textBuf != null && textBuf.length () != 0)
      {
        builder.addText (textBuf.toString (), textLoc, getComments ());
        textBuf.setLength (0);
      }
      textLoc = null;
    }
  }

  class Skipper extends DefaultHandler implements CommentHandler
  {
    int level = 1;
    final State nextState;

    Skipper (final State nextState)
    {
      this.nextState = nextState;
    }

    @Override
    public void startElement (final String namespaceURI,
                              final String localName,
                              final String qName,
                              final Attributes atts) throws SAXException
    {
      ++level;
    }

    @Override
    public void endElement (final String namespaceURI, final String localName, final String qName) throws SAXException
    {
      if (--level == 0)
        nextState.set ();
    }

    public void comment (final String value)
    {}
  }

  abstract class EmptyContentState extends State
  {

    @Override
    State createChildState (final String localName) throws SAXException
    {
      error ("expected_empty", localName);
      return null;
    }

    abstract Pattern makePattern () throws SAXException;

    @Override
    void end () throws SAXException
    {
      if (comments != null)
      {
        if (annotations == null)
          annotations = schemaBuilder.makeAnnotations (null, getContext ());
        annotations.addComment (comments);
        comments = null;
      }
      parent.endPatternChild (makePattern ());
    }
  }

  abstract class PatternContainerState extends State
  {
    List <Pattern> childPatterns = new ArrayList <Pattern> ();

    @Override
    State createChildState (final String localName) throws SAXException
    {
      final State state = patternMap.get (localName);
      if (state == null)
      {
        error ("expected_pattern", localName);
        return null;
      }
      return state.create ();
    }

    Pattern buildPattern (final List <Pattern> patterns, final Location loc, final AnnotationsImpl anno) throws SAXException
    {
      if (patterns.size () == 1 && anno == null)
        return patterns.get (0);
      return schemaBuilder.makeGroup (patterns, loc, anno);
    }

    @Override
    void endPatternChild (final Pattern pattern)
    {
      childPatterns.add (pattern);
    }

    @Override
    void endForeignChild (final ElementAnnotation ea)
    {
      final int nChildPatterns = childPatterns.size ();
      if (nChildPatterns == 0)
        super.endForeignChild (ea);
      else
        childPatterns.set (nChildPatterns - 1,
                           schemaBuilder.annotateAfterPattern (childPatterns.get (nChildPatterns - 1), ea));
    }

    @Override
    void end () throws SAXException
    {
      if (childPatterns.size () == 0)
      {
        error ("missing_children");
        endPatternChild (schemaBuilder.makeErrorPattern ());
      }
      if (comments != null)
      {
        final int nChildPatterns = childPatterns.size ();
        childPatterns.set (nChildPatterns - 1,
                           schemaBuilder.commentAfterPattern (childPatterns.get (nChildPatterns - 1), comments));
        comments = null;
      }
      sendPatternToParent (buildPattern (childPatterns, startLocation, annotations));
    }

    void sendPatternToParent (final Pattern p)
    {
      parent.endPatternChild (p);
    }
  }

  class GroupState extends PatternContainerState
  {
    @Override
    State create ()
    {
      return new GroupState ();
    }
  }

  class ZeroOrMoreState extends PatternContainerState
  {
    @Override
    State create ()
    {
      return new ZeroOrMoreState ();
    }

    @Override
    Pattern buildPattern (final List <Pattern> patterns, final Location loc, final AnnotationsImpl anno) throws SAXException
    {
      return schemaBuilder.makeZeroOrMore (super.buildPattern (patterns, loc, null), loc, anno);
    }
  }

  class OneOrMoreState extends PatternContainerState
  {
    @Override
    State create ()
    {
      return new OneOrMoreState ();
    }

    @Override
    Pattern buildPattern (final List <Pattern> patterns, final Location loc, final AnnotationsImpl anno) throws SAXException
    {
      return schemaBuilder.makeOneOrMore (super.buildPattern (patterns, loc, null), loc, anno);
    }
  }

  class OptionalState extends PatternContainerState
  {
    @Override
    State create ()
    {
      return new OptionalState ();
    }

    @Override
    Pattern buildPattern (final List <Pattern> patterns, final Location loc, final AnnotationsImpl anno) throws SAXException
    {
      return schemaBuilder.makeOptional (super.buildPattern (patterns, loc, null), loc, anno);
    }
  }

  class ListState extends PatternContainerState
  {
    @Override
    State create ()
    {
      return new ListState ();
    }

    @Override
    Pattern buildPattern (final List <Pattern> patterns, final Location loc, final AnnotationsImpl anno) throws SAXException
    {
      return schemaBuilder.makeList (super.buildPattern (patterns, loc, null), loc, anno);
    }
  }

  class ChoiceState extends PatternContainerState
  {
    @Override
    State create ()
    {
      return new ChoiceState ();
    }

    @Override
    Pattern buildPattern (final List <Pattern> patterns, final Location loc, final AnnotationsImpl anno) throws SAXException
    {
      return schemaBuilder.makeChoice (patterns, loc, anno);
    }
  }

  class InterleaveState extends PatternContainerState
  {
    @Override
    State create ()
    {
      return new InterleaveState ();
    }

    @Override
    Pattern buildPattern (final List <Pattern> patterns, final Location loc, final AnnotationsImpl anno)
    {
      return schemaBuilder.makeInterleave (patterns, loc, anno);
    }
  }

  class MixedState extends PatternContainerState
  {
    @Override
    State create ()
    {
      return new MixedState ();
    }

    @Override
    Pattern buildPattern (final List <Pattern> patterns, final Location loc, final AnnotationsImpl anno) throws SAXException
    {
      return schemaBuilder.makeMixed (super.buildPattern (patterns, loc, null), loc, anno);
    }
  }

  static interface NameClassRef <NC>
  {
    void setNameClass (NC nc);
  }

  class ElementState extends PatternContainerState implements NameClassRef <NameClass>
  {
    NameClass nameClass;
    boolean nameClassWasAttribute;
    String name;

    @Override
    void setName (final String name)
    {
      this.name = name;
    }

    public void setNameClass (final NameClass nc)
    {
      nameClass = nc;
    }

    @Override
    void endAttributes () throws SAXException
    {
      if (name != null)
      {
        nameClass = expandName (name, getNs (), null);
        nameClassWasAttribute = true;
      }
      else
        new NameClassChildState (this, this).set ();
    }

    @Override
    State create ()
    {
      return new ElementState ();
    }

    @Override
    Pattern buildPattern (final List <Pattern> patterns, final Location loc, final AnnotationsImpl anno) throws SAXException
    {
      return schemaBuilder.makeElement (nameClass, super.buildPattern (patterns, loc, null), loc, anno);
    }

    @Override
    void endForeignChild (final ElementAnnotation ea)
    {
      if (nameClassWasAttribute || childPatterns.size () > 0 || nameClass == null)
        super.endForeignChild (ea);
      else
        nameClass = schemaBuilder.annotateAfterNameClass (nameClass, ea);
    }
  }

  class RootState extends PatternContainerState
  {
    IncludedGrammar <Pattern, Location, ElementAnnotation, CommentListImpl, AnnotationsImpl> grammar;

    RootState ()
    {}

    RootState (final IncludedGrammar <Pattern, Location, ElementAnnotation, CommentListImpl, AnnotationsImpl> grammar,
               final Scope <Pattern, Location, ElementAnnotation, CommentListImpl, AnnotationsImpl> scope,
               final String ns)
    {
      this.grammar = grammar;
      this.scope = scope;
      this.nsInherit = ns;
      this.datatypeLibrary = "";
    }

    @Override
    RootState toRootState ()
    {
      return this;
    }

    @Override
    State create ()
    {
      return new RootState ();
    }

    @Override
    State createChildState (final String localName) throws SAXException
    {
      if (grammar == null)
        return super.createChildState (localName);
      if (localName.equals ("grammar"))
        return new MergeGrammarState (grammar);
      error ("expected_grammar", localName);
      return null;
    }

    @Override
    void checkForeignElement () throws SAXException
    {
      error ("root_bad_namespace_uri", WellKnownNamespaces.RELAX_NG);
    }

    @Override
    void endPatternChild (final Pattern pattern)
    {
      startPattern = pattern;
    }

    @Override
    boolean isRelaxNGElement (final String uri) throws SAXException
    {
      if (!uri.startsWith (relaxngURIPrefix))
        return false;
      if (!uri.equals (WellKnownNamespaces.RELAX_NG))
        warning ("wrong_uri_version",
                 WellKnownNamespaces.RELAX_NG.substring (relaxngURIPrefix.length ()),
                 uri.substring (relaxngURIPrefix.length ()));
      relaxngURI = uri;
      return true;
    }

  }

  class NotAllowedState extends EmptyContentState
  {
    @Override
    State create ()
    {
      return new NotAllowedState ();
    }

    @Override
    Pattern makePattern ()
    {
      return schemaBuilder.makeNotAllowed (startLocation, annotations);
    }
  }

  class EmptyState extends EmptyContentState
  {
    @Override
    State create ()
    {
      return new EmptyState ();
    }

    @Override
    Pattern makePattern ()
    {
      return schemaBuilder.makeEmpty (startLocation, annotations);
    }
  }

  class TextState extends EmptyContentState
  {
    @Override
    State create ()
    {
      return new TextState ();
    }

    @Override
    Pattern makePattern ()
    {
      return schemaBuilder.makeText (startLocation, annotations);
    }
  }

  class ValueState extends EmptyContentState
  {
    final StringBuffer buf = new StringBuffer ();
    String type;

    @Override
    State create ()
    {
      return new ValueState ();
    }

    @Override
    void setOtherAttribute (final String name, final String value) throws SAXException
    {
      if (name.equals ("type"))
        type = checkNCName (value.trim ());
      else
        super.setOtherAttribute (name, value);
    }

    @Override
    public void characters (final char [] ch, final int start, final int len)
    {
      buf.append (ch, start, len);
    }

    @Override
    void checkForeignElement () throws SAXException
    {
      error ("value_contains_foreign_element");
    }

    @Override
    Pattern makePattern () throws SAXException
    {
      if (type == null)
        return makePattern ("", "token");
      else
        return makePattern (datatypeLibrary, type);
    }

    @Override
    void end () throws SAXException
    {
      mergeLeadingComments ();
      super.end ();
    }

    Pattern makePattern (final String datatypeLibrary, final String type)
    {
      return schemaBuilder.makeValue (datatypeLibrary,
                                      type,
                                      buf.toString (),
                                      getContext (),
                                      getNs (),
                                      startLocation,
                                      annotations);
    }

  }

  class DataState extends State
  {
    String type;
    Pattern except = null;
    DataPatternBuilder <Pattern, Location, ElementAnnotation, CommentListImpl, AnnotationsImpl> dpb = null;

    @Override
    State create ()
    {
      return new DataState ();
    }

    @Override
    State createChildState (final String localName) throws SAXException
    {
      if (localName.equals ("param"))
      {
        if (except != null)
          error ("param_after_except");
        return new ParamState (dpb);
      }
      if (localName.equals ("except"))
      {
        if (except != null)
          error ("multiple_except");
        return new ChoiceState ();
      }
      error ("expected_param_except", localName);
      return null;
    }

    @Override
    void setOtherAttribute (final String name, final String value) throws SAXException
    {
      if (name.equals ("type"))
        type = checkNCName (value.trim ());
      else
        super.setOtherAttribute (name, value);
    }

    @Override
    void endAttributes () throws SAXException
    {
      if (type == null)
        error ("missing_type_attribute");
      else
        dpb = schemaBuilder.makeDataPatternBuilder (datatypeLibrary, type, startLocation);
    }

    @Override
    void endForeignChild (final ElementAnnotation ea)
    {
      dpb.annotation (ea);
    }

    @Override
    void end () throws SAXException
    {
      Pattern p;
      if (dpb != null)
      {
        if (except != null)
          p = dpb.makePattern (except, startLocation, annotations);
        else
          p = dpb.makePattern (startLocation, annotations);
      }
      else
        p = schemaBuilder.makeErrorPattern ();
      // XXX need to capture comments
      parent.endPatternChild (p);
    }

    @Override
    void endPatternChild (final Pattern pattern)
    {
      except = pattern;
    }

  }

  class ParamState extends State
  {
    private final StringBuffer buf = new StringBuffer ();
    private final DataPatternBuilder <Pattern, Location, ElementAnnotation, CommentListImpl, AnnotationsImpl> dpb;
    private String name;

    ParamState (final DataPatternBuilder <Pattern, Location, ElementAnnotation, CommentListImpl, AnnotationsImpl> dpb)
    {
      this.dpb = dpb;
    }

    @Override
    State create ()
    {
      return new ParamState (null);
    }

    @Override
    void setName (final String name) throws SAXException
    {
      this.name = checkNCName (name);
    }

    @Override
    void endAttributes () throws SAXException
    {
      if (name == null)
        error ("missing_name_attribute");
    }

    @Override
    State createChildState (final String localName) throws SAXException
    {
      error ("expected_empty", localName);
      return null;
    }

    @Override
    public void characters (final char [] ch, final int start, final int len)
    {
      buf.append (ch, start, len);
    }

    @Override
    void checkForeignElement () throws SAXException
    {
      error ("param_contains_foreign_element");
    }

    @Override
    void end () throws SAXException
    {
      if (name == null)
        return;
      if (dpb == null)
        return;
      mergeLeadingComments ();
      dpb.addParam (name, buf.toString (), getContext (), getNs (), startLocation, annotations);
    }
  }

  class AttributeState extends PatternContainerState implements NameClassRef <NameClass>
  {
    NameClass nameClass;
    boolean nameClassWasAttribute;
    String name;

    @Override
    State create ()
    {
      return new AttributeState ();
    }

    @Override
    void setName (final String name)
    {
      this.name = name;
    }

    public void setNameClass (final NameClass nc)
    {
      nameClass = nc;
    }

    @Override
    void endAttributes () throws SAXException
    {
      if (name != null)
      {
        String nsUse;
        if (ns != null)
          nsUse = ns;
        else
          nsUse = "";
        nameClass = expandName (name, nsUse, null);
        nameClassWasAttribute = true;
      }
      else
        new NameClassChildState (this, this).set ();
    }

    @Override
    void endForeignChild (final ElementAnnotation ea)
    {
      if (nameClassWasAttribute || childPatterns.size () > 0 || nameClass == null)
        super.endForeignChild (ea);
      else
        nameClass = schemaBuilder.annotateAfterNameClass (nameClass, ea);
    }

    @Override
    void end () throws SAXException
    {
      if (childPatterns.size () == 0)
        endPatternChild (schemaBuilder.makeText (startLocation, null));
      super.end ();
    }

    @Override
    Pattern buildPattern (final List <Pattern> patterns, final Location loc, final AnnotationsImpl anno) throws SAXException
    {
      return schemaBuilder.makeAttribute (nameClass, super.buildPattern (patterns, loc, null), loc, anno);
    }

    @Override
    State createChildState (final String localName) throws SAXException
    {
      final State tem = super.createChildState (localName);
      if (tem != null && childPatterns.size () != 0)
        error ("attribute_multi_pattern");
      return tem;
    }

  }

  abstract class SinglePatternContainerState extends PatternContainerState
  {
    @Override
    State createChildState (final String localName) throws SAXException
    {
      if (childPatterns.size () == 0)
        return super.createChildState (localName);
      error ("too_many_children");
      return null;
    }
  }

  class GrammarSectionState extends State
  {
    GrammarSection <Pattern, Location, ElementAnnotation, CommentListImpl, AnnotationsImpl> section;

    GrammarSectionState ()
    {}

    GrammarSectionState (final GrammarSection <Pattern, Location, ElementAnnotation, CommentListImpl, AnnotationsImpl> section)
    {
      this.section = section;
    }

    @Override
    State create ()
    {
      return new GrammarSectionState (null);
    }

    @Override
    State createChildState (final String localName) throws SAXException
    {
      if (localName.equals ("define"))
        return new DefineState (section);
      if (localName.equals ("start"))
        return new StartState (section);
      if (localName.equals ("include"))
      {
        final Include <Pattern, Location, ElementAnnotation, CommentListImpl, AnnotationsImpl> include = section.makeInclude ();
        if (include != null)
          return new IncludeState (include);
      }
      if (localName.equals ("div"))
        return new DivState (section.makeDiv ());
      error ("expected_define", localName);
      // XXX better errors
      return null;
    }

    @Override
    void end () throws SAXException
    {
      if (comments != null)
      {
        section.topLevelComment (comments);
        comments = null;
      }
    }

    @Override
    void endForeignChild (final ElementAnnotation ea)
    {
      section.topLevelAnnotation (ea);
    }
  }

  class DivState extends GrammarSectionState
  {
    final Div <Pattern, Location, ElementAnnotation, CommentListImpl, AnnotationsImpl> div;

    DivState (final Div <Pattern, Location, ElementAnnotation, CommentListImpl, AnnotationsImpl> div)
    {
      super (div);
      this.div = div;
    }

    @Override
    void end () throws SAXException
    {
      super.end ();
      div.endDiv (startLocation, annotations);
    }
  }

  class IncludeState extends GrammarSectionState
  {
    String href;
    String base;
    final Include <Pattern, Location, ElementAnnotation, CommentListImpl, AnnotationsImpl> include;

    IncludeState (final Include <Pattern, Location, ElementAnnotation, CommentListImpl, AnnotationsImpl> include)
    {
      super (include);
      this.include = include;
    }

    @Override
    void setOtherAttribute (final String name, final String value) throws SAXException
    {
      if (name.equals ("href"))
      {
        href = value;
        checkUriNoFragmentId (href);
      }
      else
        super.setOtherAttribute (name, value);
    }

    @Override
    void endAttributes () throws SAXException
    {
      if (href == null)
        error ("missing_href_attribute");
      else
        base = xmlBaseHandler.getBaseUri ();
    }

    @Override
    void end () throws SAXException
    {
      super.end ();
      if (href != null)
      {
        try
        {
          include.endInclude (href, base, getNs (), startLocation, annotations);
        }
        catch (final IllegalSchemaException e)
        {}
      }
    }
  }

  class MergeGrammarState extends GrammarSectionState
  {
    final IncludedGrammar <Pattern, Location, ElementAnnotation, CommentListImpl, AnnotationsImpl> grammar;

    MergeGrammarState (final IncludedGrammar <Pattern, Location, ElementAnnotation, CommentListImpl, AnnotationsImpl> grammar)
    {
      super (grammar);
      this.grammar = grammar;
    }

    @Override
    void end () throws SAXException
    {
      super.end ();
      parent.endPatternChild (grammar.endIncludedGrammar (startLocation, annotations));
    }
  }

  class GrammarState extends GrammarSectionState
  {
    Grammar <Pattern, Location, ElementAnnotation, CommentListImpl, AnnotationsImpl> grammar;

    @Override
    void setParent (final State parent)
    {
      super.setParent (parent);
      grammar = schemaBuilder.makeGrammar (scope);
      section = grammar;
      scope = grammar;
    }

    @Override
    State create ()
    {
      return new GrammarState ();
    }

    @Override
    void end () throws SAXException
    {
      super.end ();
      parent.endPatternChild (grammar.endGrammar (startLocation, annotations));
    }
  }

  class RefState extends EmptyContentState
  {
    String name;

    @Override
    State create ()
    {
      return new RefState ();
    }

    @Override
    void endAttributes () throws SAXException
    {
      if (name == null)
        error ("missing_name_attribute");
    }

    @Override
    void setName (final String name) throws SAXException
    {
      this.name = checkNCName (name);
    }

    @Override
    Pattern makePattern ()
    {
      if (name == null)
        return schemaBuilder.makeErrorPattern ();
      return scope.makeRef (name, startLocation, annotations);
    }
  }

  class ParentRefState extends RefState
  {
    @Override
    State create ()
    {
      return new ParentRefState ();
    }

    @Override
    Pattern makePattern ()
    {
      if (name == null)
        return schemaBuilder.makeErrorPattern ();
      return scope.makeParentRef (name, startLocation, annotations);
    }
  }

  class ExternalRefState extends EmptyContentState
  {
    String href;
    String base;
    Pattern includedPattern;

    @Override
    State create ()
    {
      return new ExternalRefState ();
    }

    @Override
    void setOtherAttribute (final String name, final String value) throws SAXException
    {
      if (name.equals ("href"))
      {
        href = value;
        checkUriNoFragmentId (href);
      }
      else
        super.setOtherAttribute (name, value);
    }

    @Override
    void endAttributes () throws SAXException
    {
      if (href == null)
        error ("missing_href_attribute");
      else
        base = xmlBaseHandler.getBaseUri ();
    }

    @Override
    Pattern makePattern ()
    {
      if (href != null)
      {
        try
        {
          return schemaBuilder.makeExternalRef (href, base, getNs (), scope, startLocation, annotations);
        }
        catch (final IllegalSchemaException e)
        {}
      }
      return schemaBuilder.makeErrorPattern ();
    }
  }

  abstract class DefinitionState extends PatternContainerState
  {
    GrammarSection.Combine combine = null;
    final GrammarSection <Pattern, Location, ElementAnnotation, CommentListImpl, AnnotationsImpl> section;

    DefinitionState (final GrammarSection <Pattern, Location, ElementAnnotation, CommentListImpl, AnnotationsImpl> section)
    {
      this.section = section;
    }

    @Override
    void setOtherAttribute (final String name, String value) throws SAXException
    {
      if (name.equals ("combine"))
      {
        value = value.trim ();
        if (value.equals ("choice"))
          combine = GrammarSection.COMBINE_CHOICE;
        else
          if (value.equals ("interleave"))
            combine = GrammarSection.COMBINE_INTERLEAVE;
          else
            error ("combine_attribute_bad_value", value);
      }
      else
        super.setOtherAttribute (name, value);
    }

    @Override
    Pattern buildPattern (final List <Pattern> patterns, final Location loc, final AnnotationsImpl anno) throws SAXException
    {
      return super.buildPattern (patterns, loc, null);
    }
  }

  class DefineState extends DefinitionState
  {
    String name;

    DefineState (final GrammarSection <Pattern, Location, ElementAnnotation, CommentListImpl, AnnotationsImpl> section)
    {
      super (section);
    }

    @Override
    State create ()
    {
      return new DefineState (null);
    }

    @Override
    void setName (final String name) throws SAXException
    {
      this.name = checkNCName (name);
    }

    @Override
    void endAttributes () throws SAXException
    {
      if (name == null)
        error ("missing_name_attribute");
    }

    @Override
    void sendPatternToParent (final Pattern p)
    {
      if (name != null)
        section.define (name, combine, p, startLocation, annotations);
    }

  }

  class StartState extends DefinitionState
  {

    StartState (final GrammarSection <Pattern, Location, ElementAnnotation, CommentListImpl, AnnotationsImpl> section)
    {
      super (section);
    }

    @Override
    State create ()
    {
      return new StartState (null);
    }

    @Override
    void sendPatternToParent (final Pattern p)
    {
      section.define (GrammarSection.START, combine, p, startLocation, annotations);
    }

    @Override
    State createChildState (final String localName) throws SAXException
    {
      final State tem = super.createChildState (localName);
      if (tem != null && childPatterns.size () != 0)
        error ("start_multi_pattern");
      return tem;
    }

  }

  abstract class NameClassContainerState extends State
  {
    @Override
    State createChildState (final String localName) throws SAXException
    {
      final State state = nameClassMap.get (localName);
      if (state == null)
      {
        error ("expected_name_class", localName);
        return null;
      }
      return state.create ();
    }
  }

  class NameClassChildState extends NameClassContainerState
  {
    final State prevState;
    final NameClassRef <NameClass> nameClassRef;

    @Override
    State create ()
    {
      return null;
    }

    NameClassChildState (final State prevState, final NameClassRef <NameClass> nameClassRef)
    {
      this.prevState = prevState;
      this.nameClassRef = nameClassRef;
      setParent (prevState.parent);
      this.ns = prevState.ns;
    }

    @Override
    void endNameClassChild (final NameClass nameClass)
    {
      nameClassRef.setNameClass (nameClass);
      prevState.set ();
    }

    @Override
    void endForeignChild (final ElementAnnotation ea)
    {
      prevState.endForeignChild (ea);
    }

    @Override
    void end () throws SAXException
    {
      nameClassRef.setNameClass (schemaBuilder.makeErrorNameClass ());
      error ("missing_name_class");
      prevState.set ();
      prevState.end ();
    }
  }

  abstract class NameClassBaseState extends State
  {

    abstract NameClass makeNameClass () throws SAXException;

    @Override
    void end () throws SAXException
    {
      parent.endNameClassChild (makeNameClass ());
    }
  }

  class NameState extends NameClassBaseState
  {
    final StringBuffer buf = new StringBuffer ();

    @Override
    State createChildState (final String localName) throws SAXException
    {
      error ("expected_name", localName);
      return null;
    }

    @Override
    State create ()
    {
      return new NameState ();
    }

    @Override
    public void characters (final char [] ch, final int start, final int len)
    {
      buf.append (ch, start, len);
    }

    @Override
    void checkForeignElement () throws SAXException
    {
      error ("name_contains_foreign_element");
    }

    @Override
    NameClass makeNameClass () throws SAXException
    {
      mergeLeadingComments ();
      return expandName (buf.toString ().trim (), getNs (), annotations);
    }

  }

  private static final int PATTERN_CONTEXT = 0;
  private static final int ANY_NAME_CONTEXT = 1;
  private static final int NS_NAME_CONTEXT = 2;

  class AnyNameState extends NameClassBaseState
  {
    NameClass except = null;

    @Override
    State create ()
    {
      return new AnyNameState ();
    }

    @Override
    State createChildState (final String localName) throws SAXException
    {
      if (localName.equals ("except"))
      {
        if (except != null)
          error ("multiple_except");
        return new NameClassChoiceState (getContext ());
      }
      error ("expected_except", localName);
      return null;
    }

    int getContext ()
    {
      return ANY_NAME_CONTEXT;
    }

    @Override
    NameClass makeNameClass ()
    {
      if (except == null)
        return makeNameClassNoExcept ();
      else
        return makeNameClassExcept (except);
    }

    NameClass makeNameClassNoExcept ()
    {
      return schemaBuilder.makeAnyName (startLocation, annotations);
    }

    NameClass makeNameClassExcept (final NameClass except)
    {
      return schemaBuilder.makeAnyName (except, startLocation, annotations);
    }

    @Override
    void endNameClassChild (final NameClass nameClass)
    {
      except = nameClass;
    }

  }

  class NsNameState extends AnyNameState
  {
    @Override
    State create ()
    {
      return new NsNameState ();
    }

    @Override
    NameClass makeNameClassNoExcept ()
    {
      return schemaBuilder.makeNsName (getNs (), null, null);
    }

    @Override
    NameClass makeNameClassExcept (final NameClass except)
    {
      return schemaBuilder.makeNsName (getNs (), except, null, null);
    }

    @Override
    int getContext ()
    {
      return NS_NAME_CONTEXT;
    }

  }

  class NameClassChoiceState extends NameClassContainerState
  {
    private final List <NameClass> nameClasses = new ArrayList <NameClass> ();
    private int context;

    NameClassChoiceState ()
    {
      this.context = PATTERN_CONTEXT;
    }

    NameClassChoiceState (final int context)
    {
      this.context = context;
    }

    @Override
    NameClassChoiceState toNameClassChoiceState ()
    {
      return this;
    }

    @Override
    void setParent (final State parent)
    {
      super.setParent (parent);
      final NameClassChoiceState parentChoice = parent.toNameClassChoiceState ();
      if (parentChoice != null)
        this.context = parentChoice.context;
    }

    @Override
    State create ()
    {
      return new NameClassChoiceState ();
    }

    @Override
    State createChildState (final String localName) throws SAXException
    {
      if (localName.equals ("anyName"))
      {
        if (context >= ANY_NAME_CONTEXT)
        {
          error (context == ANY_NAME_CONTEXT ? "any_name_except_contains_any_name" : "ns_name_except_contains_any_name");
          return null;
        }
      }
      else
        if (localName.equals ("nsName"))
        {
          if (context == NS_NAME_CONTEXT)
          {
            error ("ns_name_except_contains_ns_name");
            return null;
          }
        }
      return super.createChildState (localName);
    }

    @Override
    void endNameClassChild (final NameClass nc)
    {
      nameClasses.add (nc);
    }

    @Override
    void endForeignChild (final ElementAnnotation ea)
    {
      final int nNameClasses = nameClasses.size ();
      if (nNameClasses == 0)
        super.endForeignChild (ea);
      else
        nameClasses.set (nNameClasses - 1,
                         schemaBuilder.annotateAfterNameClass (nameClasses.get (nNameClasses - 1), ea));
    }

    @Override
    void end () throws SAXException
    {
      if (nameClasses.size () == 0)
      {
        error ("missing_name_class");
        parent.endNameClassChild (schemaBuilder.makeErrorNameClass ());
        return;
      }
      if (comments != null)
      {
        final int nNameClasses = nameClasses.size ();
        nameClasses.set (nNameClasses - 1,
                         schemaBuilder.commentAfterNameClass (nameClasses.get (nNameClasses - 1), comments));
        comments = null;
      }
      parent.endNameClassChild (schemaBuilder.makeNameClassChoice (nameClasses, startLocation, annotations));
    }
  }

  private void initPatternTable ()
  {
    patternMap = new HashMap <String, State> ();
    patternMap.put ("zeroOrMore", new ZeroOrMoreState ());
    patternMap.put ("oneOrMore", new OneOrMoreState ());
    patternMap.put ("optional", new OptionalState ());
    patternMap.put ("list", new ListState ());
    patternMap.put ("choice", new ChoiceState ());
    patternMap.put ("interleave", new InterleaveState ());
    patternMap.put ("group", new GroupState ());
    patternMap.put ("mixed", new MixedState ());
    patternMap.put ("element", new ElementState ());
    patternMap.put ("attribute", new AttributeState ());
    patternMap.put ("empty", new EmptyState ());
    patternMap.put ("text", new TextState ());
    patternMap.put ("value", new ValueState ());
    patternMap.put ("data", new DataState ());
    patternMap.put ("notAllowed", new NotAllowedState ());
    patternMap.put ("grammar", new GrammarState ());
    patternMap.put ("ref", new RefState ());
    patternMap.put ("parentRef", new ParentRefState ());
    patternMap.put ("externalRef", new ExternalRefState ());
  }

  private void initNameClassTable ()
  {
    nameClassMap = new HashMap <String, State> ();
    nameClassMap.put ("name", new NameState ());
    nameClassMap.put ("anyName", new AnyNameState ());
    nameClassMap.put ("nsName", new NsNameState ());
    nameClassMap.put ("choice", new NameClassChoiceState ());
  }

  public Pattern getParsedPattern () throws IllegalSchemaException
  {
    if (hadError)
      throw new IllegalSchemaException ();
    return startPattern;
  }

  private void error (final String key) throws SAXException
  {
    error (key, locator);
  }

  private void error (final String key, final String arg) throws SAXException
  {
    error (key, arg, locator);
  }

  void error (final String key, final String arg1, final String arg2) throws SAXException
  {
    error (key, arg1, arg2, locator);
  }

  private void error (final String key, final Locator loc) throws SAXException
  {
    error (new SAXParseException (localizer.message (key), loc));
  }

  private void error (final String key, final String arg, final Locator loc) throws SAXException
  {
    error (new SAXParseException (localizer.message (key, arg), loc));
  }

  private void error (final String key, final String arg1, final String arg2, final Locator loc) throws SAXException
  {
    error (new SAXParseException (localizer.message (key, arg1, arg2), loc));
  }

  private void error (final SAXParseException e) throws SAXException
  {
    hadError = true;
    if (eh != null)
      eh.error (e);
  }

  void warning (final String key) throws SAXException
  {
    warning (key, locator);
  }

  private void warning (final String key, final String arg) throws SAXException
  {
    warning (key, arg, locator);
  }

  private void warning (final String key, final String arg1, final String arg2) throws SAXException
  {
    warning (key, arg1, arg2, locator);
  }

  private void warning (final String key, final Locator loc) throws SAXException
  {
    warning (new SAXParseException (localizer.message (key), loc));
  }

  private void warning (final String key, final String arg, final Locator loc) throws SAXException
  {
    warning (new SAXParseException (localizer.message (key, arg), loc));
  }

  private void warning (final String key, final String arg1, final String arg2, final Locator loc) throws SAXException
  {
    warning (new SAXParseException (localizer.message (key, arg1, arg2), loc));
  }

  private void warning (final SAXParseException e) throws SAXException
  {
    if (eh != null)
      eh.warning (e);
  }

  SchemaParser (final XMLReader xr,
                final ErrorHandler eh,
                final SchemaBuilder <Pattern, NameClass, Location, ElementAnnotation, CommentListImpl, AnnotationsImpl> schemaBuilder,
                final IncludedGrammar <Pattern, Location, ElementAnnotation, CommentListImpl, AnnotationsImpl> grammar,
                final Scope <Pattern, Location, ElementAnnotation, CommentListImpl, AnnotationsImpl> scope) throws SAXException
  {
    this.xr = xr;
    this.eh = eh;
    this.schemaBuilder = schemaBuilder;
    if (eh != null)
      xr.setErrorHandler (eh);
    xr.setDTDHandler (context);
    if (schemaBuilder.usesComments ())
    {
      try
      {
        xr.setProperty ("http://xml.org/sax/properties/lexical-handler", new LexicalHandlerImpl ());
      }
      catch (final SAXNotRecognizedException e)
      {
        warning ("no_comment_support", xr.getClass ().getName ());
      }
      catch (final SAXNotSupportedException e)
      {
        warning ("no_comment_support", xr.getClass ().getName ());
      }
    }
    initPatternTable ();
    initNameClassTable ();
    new RootState (grammar, scope, SchemaBuilder.INHERIT_NS).set ();
  }

  private Context getContext ()
  {
    return context;
  }

  class LexicalHandlerImpl extends AbstractLexicalHandler
  {
    private boolean inDtd = false;

    @Override
    public void startDTD (final String s, final String s1, final String s2) throws SAXException
    {
      inDtd = true;
    }

    @Override
    public void endDTD () throws SAXException
    {
      inDtd = false;
    }

    @Override
    public void comment (final char [] chars, final int start, final int length) throws SAXException
    {
      if (!inDtd)
        ((CommentHandler) xr.getContentHandler ()).comment (new String (chars, start, length));
    }
  }

  private NameClass expandName (final String name, final String ns, final AnnotationsImpl anno) throws SAXException
  {
    final int ic = name.indexOf (':');
    if (ic == -1)
      return schemaBuilder.makeName (ns, checkNCName (name), null, null, anno);
    final String prefix = checkNCName (name.substring (0, ic));
    final String localName = checkNCName (name.substring (ic + 1));
    for (PrefixMapping tem = context.prefixMapping; tem != null; tem = tem.next)
      if (tem.prefix.equals (prefix))
        return schemaBuilder.makeName (tem.uri, localName, prefix, null, anno);
    error ("undefined_prefix", prefix);
    return schemaBuilder.makeName ("", localName, null, null, anno);
  }

  private String findPrefix (final String qName, final String uri)
  {
    String prefix = null;
    if (qName == null || qName.equals (""))
    {
      for (PrefixMapping p = context.prefixMapping; p != null; p = p.next)
        if (p.uri.equals (uri))
        {
          prefix = p.prefix;
          break;
        }
    }
    else
    {
      final int off = qName.indexOf (':');
      if (off > 0)
        prefix = qName.substring (0, off);
    }
    return prefix;
  }

  private String checkNCName (final String str) throws SAXException
  {
    if (!Naming.isNcname (str))
      error ("invalid_ncname", str);
    return str;
  }

  private Location makeLocation ()
  {
    if (locator == null)
      return null;
    return schemaBuilder.makeLocation (locator.getSystemId (), locator.getLineNumber (), locator.getColumnNumber ());
  }

  private void checkUriNoFragmentId (final String s) throws SAXException
  {
    checkUri (s);
    if (Uri.hasFragmentId (s))
      error ("href_fragment_id");
  }

  private void checkUri (final String s) throws SAXException
  {
    if (!Uri.isValid (s))
      error ("invalid_uri", s);
  }
}
