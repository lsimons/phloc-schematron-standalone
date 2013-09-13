package com.thaiopensource.relaxng.pattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.thaiopensource.util.VoidValue;
import com.thaiopensource.xml.util.Name;
import com.thaiopensource.xml.util.WellKnownNamespaces;

public class PatternDumper
{
  private static final String INTERNAL_NAMESPACE = "http://www.thaiopensource.com/relaxng/internal";
  private boolean startTagOpen = false;
  private final ArrayList <String> tagStack = new ArrayList <String> ();
  private final StringBuilder buf;
  private int level = 0;
  private boolean suppressIndent = false;
  private final List <ElementPattern> patternList = new ArrayList <ElementPattern> ();
  private final Map <String, Integer> localNamePatternCount = new HashMap <String, Integer> ();
  private int otherPatternCount;
  private final Map <ElementPattern, String> patternNameMap = new HashMap <ElementPattern, String> ();

  private final PatternFunction <VoidValue> dumper = new Dumper ();
  private final PatternFunction <VoidValue> elementDumper = new ElementDumper ();
  private final PatternFunction <VoidValue> optionalDumper = new OptionalDumper ();
  private final PatternFunction <VoidValue> groupDumper = new GroupDumper ();
  private final PatternFunction <VoidValue> choiceDumper = new ChoiceDumper ();
  private final PatternFunction <VoidValue> interleaveDumper = new InterleaveDumper ();
  private final NameClassVisitor nameClassDumper = new NameClassDumper ();
  private final NameClassVisitor choiceNameClassDumper = new ChoiceNameClassDumper ();

  static public String toString (final Pattern p)
  {
    return new PatternDumper ().dump (p).getSchema ();
  }

  private PatternDumper ()
  {
    buf = new StringBuilder ();
  }

  private String getSchema ()
  {
    return buf.toString ();
  }

  private PatternDumper dump (final Pattern p)
  {
    write ("<?xml version=\"1.0\"?>");
    startElement ("grammar");
    attribute ("xmlns", WellKnownNamespaces.RELAX_NG);
    startElement ("start");
    p.apply (dumper);
    endElement ();
    for (int i = 0; i < patternList.size (); i++)
    {
      startElement ("define");
      final ElementPattern tem = patternList.get (i);
      attribute ("name", getName (tem));
      tem.apply (elementDumper);
      endElement ();
    }
    endElement ();
    write ('\n');
    return this;
  }

  private String getName (final ElementPattern p)
  {
    String name = patternNameMap.get (p);
    // patterns for element patterns with local name X are named: X, X_2, X_3
    // however if X is of the form Y_N (N > 0), then the patterns are named:
    // X_1, X_2, X_3
    // for element patterns with complex name classes, the patterns are named:
    // _1, _2, _3
    if (name == null)
    {
      final NameClass nc = p.getNameClass ();
      if (nc instanceof SimpleNameClass)
      {
        final String localName = ((SimpleNameClass) nc).getName ().getLocalName ();
        Integer i = localNamePatternCount.get (localName);
        if (i == null)
        {
          i = Integer.valueOf (1);
          name = localName;
          // see if the name can be the same as one of our generated names
          final int u = name.lastIndexOf ('_');
          if (u >= 0)
          {
            try
            {
              if (Integer.parseInt (name.substring (u + 1, name.length ())) > 0)
                // it can, so transform it so that it cannot
                name += "_1";
            }
            catch (final NumberFormatException e)
            {
              // not a number, so cannot be the same as one of our generated
              // names
            }
          }
        }
        else
        {
          i = Integer.valueOf (i.intValue () + 1);
          name = localName + "_" + i.intValue ();
        }
        localNamePatternCount.put (localName, i);
      }
      else
        name = "_" + ++otherPatternCount;
      patternList.add (p);
      patternNameMap.put (p, name);
    }
    return name;
  }

  private void startElement (final String name)
  {
    closeStartTag ();
    indent (level);
    write ('<');
    write (name);
    push (name);
    startTagOpen = true;
    level++;
  }

  private void closeStartTag ()
  {
    if (startTagOpen)
    {
      startTagOpen = false;
      write ('>');
    }
  }

  private void attribute (final String name, final String value)
  {
    write (' ');
    write (name);
    write ('=');
    write ('"');
    chars (value, true);
    write ('"');
  }

  private void data (final String str)
  {
    if (str.length () > 0)
    {
      closeStartTag ();
      chars (str, false);
      suppressIndent = true;
    }
  }

  private void chars (final String str, final boolean isAttribute)
  {
    final int len = str.length ();
    for (int i = 0; i < len; i++)
    {
      final char c = str.charAt (i);
      switch (c)
      {
        case '&':
          write ("&amp;");
          break;
        case '<':
          write ("&lt;");
          break;
        case '>':
          write ("&gt;");
          break;
        case 0xD:
          write ("&#xD;");
          break;
        case 0xA:
          if (isAttribute)
            write ("&#xA;");
          else
            write (c);
          break;
        case 0x9:
          if (isAttribute)
            write ("&#x9;");
          else
            write (c);
          break;
        case '"':
          if (isAttribute)
            write ("&quot;");
          else
            write (c);
          break;
        default:
          write (c);
          break;
      }
    }
  }

  private void endElement ()
  {
    --level;
    if (startTagOpen)
    {
      startTagOpen = false;
      write ("/>");
      pop ();
    }
    else
    {
      if (!suppressIndent)
        indent (level);
      write ("</");
      write (pop ());
      write (">");
    }
    suppressIndent = false;
  }

  private void indent (final int level)
  {
    write ('\n');
    for (int i = 0; i < level; i++)
      write ("  ");
  }

  private void write (final String str)
  {
    buf.append (str);
  }

  private void write (final char c)
  {
    buf.append (c);
  }

  private void push (final String s)
  {
    tagStack.add (s);
  }

  private String pop ()
  {
    return tagStack.remove (tagStack.size () - 1);
  }

  class Dumper implements PatternFunction <VoidValue>
  {
    public VoidValue caseEmpty (final EmptyPattern p)
    {
      startElement ("empty");
      endElement ();
      return VoidValue.VOID;
    }

    public VoidValue caseNotAllowed (final NotAllowedPattern p)
    {
      startElement ("notAllowed");
      endElement ();
      return VoidValue.VOID;
    }

    public VoidValue caseGroup (final GroupPattern p)
    {
      startElement ("group");
      p.getOperand1 ().apply (groupDumper);
      p.getOperand2 ().apply (groupDumper);
      endElement ();
      return VoidValue.VOID;
    }

    public VoidValue caseInterleave (final InterleavePattern p)
    {
      startElement ("interleave");
      p.getOperand1 ().apply (interleaveDumper);
      p.getOperand2 ().apply (interleaveDumper);
      endElement ();
      return VoidValue.VOID;
    }

    public VoidValue caseChoice (final ChoicePattern p)
    {
      final Pattern p1 = p.getOperand1 ();
      final Pattern p2 = p.getOperand2 ();
      if (p1 instanceof EmptyPattern)
        p2.apply (optionalDumper);
      else
        if (p2 instanceof EmptyPattern)
          p1.apply (optionalDumper);
        else
          choice (p1, p2);
      return VoidValue.VOID;
    }

    protected void choice (final Pattern p1, final Pattern p2)
    {
      startElement ("choice");
      p1.apply (choiceDumper);
      p2.apply (choiceDumper);
      endElement ();
    }

    public VoidValue caseOneOrMore (final OneOrMorePattern p)
    {
      startElement ("oneOrMore");
      p.getOperand ().apply (dumper);
      endElement ();
      return VoidValue.VOID;
    }

    public VoidValue caseElement (final ElementPattern p)
    {
      startElement ("ref");
      attribute ("name", getName (p));
      endElement ();
      return VoidValue.VOID;
    }

    public VoidValue caseAttribute (final AttributePattern p)
    {
      startElement ("attribute");
      outputName (p.getNameClass ());
      p.getContent ().apply (dumper);
      endElement ();
      return VoidValue.VOID;
    }

    protected void outputName (final NameClass nc)
    {
      if (nc instanceof SimpleNameClass)
      {
        final Name name = ((SimpleNameClass) nc).getName ();
        attribute ("name", name.getLocalName ());
        attribute ("ns", name.getNamespaceUri ());
      }
      else
        nc.accept (nameClassDumper);
    }

    public VoidValue caseData (final DataPattern p)
    {
      startData (p);
      endElement ();
      return VoidValue.VOID;
    }

    private void startData (final DataPattern p)
    {
      startElement ("data");
      final Name dtName = p.getDatatypeName ();
      attribute ("type", dtName.getLocalName ());
      attribute ("datatypeLibrary", dtName.getNamespaceUri ());
      for (final Iterator <String> iter = p.getParams ().iterator (); iter.hasNext ();)
      {
        startElement ("param");
        attribute ("name", iter.next ());
        data (iter.next ());
        endElement ();
      }
    }

    public VoidValue caseDataExcept (final DataExceptPattern p)
    {
      startData (p);
      startElement ("except");
      p.getExcept ().apply (dumper);
      endElement ();
      endElement ();
      return VoidValue.VOID;
    }

    public VoidValue caseValue (final ValuePattern p)
    {
      startElement ("value");
      final Name dtName = p.getDatatypeName ();
      attribute ("type", dtName.getLocalName ());
      attribute ("datatypeLibrary", dtName.getNamespaceUri ());
      String stringValue = p.getStringValue ();
      final Object value = p.getValue ();
      String ns = "";
      // XXX won't work with a datatypeLibrary that doesn't use Name to
      // implement QName's
      if (value instanceof Name)
      {
        ns = ((Name) value).getNamespaceUri ();
        final int colonIndex = stringValue.indexOf (':');
        if (colonIndex < 0)
          stringValue = stringValue.substring (colonIndex + 1, stringValue.length ());
      }
      attribute ("ns", ns);
      data (stringValue);
      endElement ();
      return VoidValue.VOID;
    }

    public VoidValue caseText (final TextPattern p)
    {
      startElement ("text");
      endElement ();
      return VoidValue.VOID;
    }

    public VoidValue caseList (final ListPattern p)
    {
      startElement ("list");
      p.getOperand ().apply (dumper);
      endElement ();
      return VoidValue.VOID;
    }

    public VoidValue caseRef (final RefPattern p)
    {
      return p.getPattern ().apply (this);
    }

    public VoidValue caseAfter (final AfterPattern p)
    {
      startElement ("i:after");
      attribute ("xmlns:i", INTERNAL_NAMESPACE);
      p.getOperand1 ().apply (this);
      p.getOperand2 ().apply (this);
      endElement ();
      return VoidValue.VOID;
    }

    public VoidValue caseError (final ErrorPattern p)
    {
      startElement ("i:error");
      attribute ("xmlns:i", INTERNAL_NAMESPACE);
      endElement ();
      return VoidValue.VOID;
    }
  }

  class ElementDumper extends Dumper
  {
    @Override
    public VoidValue caseElement (final ElementPattern p)
    {
      startElement ("element");
      outputName (p.getNameClass ());
      p.getContent ().apply (dumper);
      endElement ();
      return VoidValue.VOID;
    }
  }

  class OptionalDumper extends AbstractPatternFunction <VoidValue>
  {
    @Override
    public VoidValue caseOther (final Pattern p)
    {
      startElement ("optional");
      p.apply (dumper);
      endElement ();
      return VoidValue.VOID;
    }

    @Override
    public VoidValue caseOneOrMore (final OneOrMorePattern p)
    {
      startElement ("zeroOrMore");
      p.getOperand ().apply (dumper);
      endElement ();
      return VoidValue.VOID;
    }
  }

  class GroupDumper extends Dumper
  {
    @Override
    public VoidValue caseGroup (final GroupPattern p)
    {
      p.getOperand1 ().apply (this);
      p.getOperand2 ().apply (this);
      return VoidValue.VOID;
    }
  }

  class ChoiceDumper extends Dumper
  {
    @Override
    protected void choice (final Pattern p1, final Pattern p2)
    {
      p1.apply (this);
      p2.apply (this);
    }
  }

  class InterleaveDumper extends Dumper
  {
    @Override
    public VoidValue caseInterleave (final InterleavePattern p)
    {
      p.getOperand1 ().apply (this);
      p.getOperand2 ().apply (this);
      return VoidValue.VOID;
    }
  }

  class NameClassDumper implements NameClassVisitor
  {
    public void visitChoice (final NameClass nc1, final NameClass nc2)
    {
      startElement ("choice");
      nc1.accept (choiceNameClassDumper);
      nc2.accept (choiceNameClassDumper);
      endElement ();
    }

    public void visitNsName (final String ns)
    {
      startElement ("nsName");
      attribute ("ns", ns);
      endElement ();
    }

    public void visitNsNameExcept (final String ns, final NameClass nc)
    {
      startElement ("nsName");
      attribute ("ns", ns);
      startElement ("except");
      nc.accept (nameClassDumper);
      endElement ();
      endElement ();
    }

    public void visitAnyName ()
    {
      startElement ("anyName");
      endElement ();
    }

    public void visitAnyNameExcept (final NameClass nc)
    {
      startElement ("anyName");
      startElement ("except");
      nc.accept (nameClassDumper);
      endElement ();
      endElement ();
    }

    public void visitName (final Name name)
    {
      startElement ("name");
      attribute ("ns", name.getNamespaceUri ());
      data (name.getLocalName ());
      endElement ();
    }

    public void visitError ()
    {
      startElement ("error");
      endElement ();
    }

    public void visitNull ()
    {
      visitAnyName ();
    }
  }

  class ChoiceNameClassDumper extends NameClassDumper
  {
    @Override
    public void visitChoice (final NameClass nc1, final NameClass nc2)
    {
      nc1.accept (this);
      nc2.accept (this);
    }
  }
}
