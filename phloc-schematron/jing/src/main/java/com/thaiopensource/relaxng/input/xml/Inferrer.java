package com.thaiopensource.relaxng.input.xml;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.thaiopensource.datatype.DatatypeLibraryLoader;
import com.thaiopensource.relaxng.edit.AttributePattern;
import com.thaiopensource.relaxng.edit.ChoicePattern;
import com.thaiopensource.relaxng.edit.CompositePattern;
import com.thaiopensource.relaxng.edit.DataPattern;
import com.thaiopensource.relaxng.edit.DefineComponent;
import com.thaiopensource.relaxng.edit.ElementPattern;
import com.thaiopensource.relaxng.edit.EmptyPattern;
import com.thaiopensource.relaxng.edit.GrammarPattern;
import com.thaiopensource.relaxng.edit.GroupPattern;
import com.thaiopensource.relaxng.edit.NameNameClass;
import com.thaiopensource.relaxng.edit.OneOrMorePattern;
import com.thaiopensource.relaxng.edit.OptionalPattern;
import com.thaiopensource.relaxng.edit.Pattern;
import com.thaiopensource.relaxng.edit.RefPattern;
import com.thaiopensource.relaxng.edit.SchemaCollection;
import com.thaiopensource.relaxng.edit.SchemaDocument;
import com.thaiopensource.relaxng.edit.TextPattern;
import com.thaiopensource.relaxng.edit.ZeroOrMorePattern;
import com.thaiopensource.resolver.Resolver;
import com.thaiopensource.resolver.xml.sax.SAXResolver;
import com.thaiopensource.xml.infer.AttributeDecl;
import com.thaiopensource.xml.infer.ChoiceParticle;
import com.thaiopensource.xml.infer.ElementDecl;
import com.thaiopensource.xml.infer.ElementParticle;
import com.thaiopensource.xml.infer.EmptyParticle;
import com.thaiopensource.xml.infer.InferHandler;
import com.thaiopensource.xml.infer.OneOrMoreParticle;
import com.thaiopensource.xml.infer.Particle;
import com.thaiopensource.xml.infer.ParticleVisitor;
import com.thaiopensource.xml.infer.Schema;
import com.thaiopensource.xml.infer.SequenceParticle;
import com.thaiopensource.xml.infer.TextParticle;
import com.thaiopensource.xml.util.Name;

class Inferrer
{
  private final Schema schema;
  private final Set <Name> multiplyReferencedElementNames = new HashSet <Name> ();
  private final GrammarPattern grammar;
  private final ParticleConverter particleConverter = new ParticleConverter ();
  private final List <Name> outputQueue = new Vector <Name> ();
  private final Set <Name> queued = new HashSet <Name> ();
  private String prefixSeparator;

  private static final String SEPARATORS = ".-_";

  static class Options
  {
    String encoding;
    Resolver resolver;
  }

  private static class PatternComparator implements Comparator <Pattern>
  {
    private static final Class <?> [] classOrder = { TextPattern.class, RefPattern.class, ElementPattern.class };

    public int compare (final Pattern p1, final Pattern p2)
    {
      if (p1.getClass () != p2.getClass ())
        return classIndex (p1.getClass ()) - classIndex (p2.getClass ());
      if (p1 instanceof RefPattern)
        return ((RefPattern) p1).getName ().compareTo (((RefPattern) p2).getName ());
      if (p1 instanceof ElementPattern)
        return Name.compare (extractElementName (p1), extractElementName (p2));
      return 0;
    }

    private static Name extractElementName (final Object o)
    {
      final NameNameClass nnc = (NameNameClass) ((ElementPattern) o).getNameClass ();
      return new Name (nnc.getNamespaceUri (), nnc.getLocalName ());
    }

    private static int classIndex (final Class <? extends Pattern> aClass)
    {
      for (int i = 0; i < classOrder.length; i++)
        if (aClass == classOrder[i])
          return i;
      return classOrder.length;
    }
  }

  private class ParticleConverter extends PatternComparator implements ParticleVisitor
  {
    public Object visitElement (final ElementParticle p)
    {
      final Name name = p.getName ();
      if (multiplyReferencedElementNames.contains (name))
      {
        if (!queued.contains (name))
        {
          queued.add (name);
          outputQueue.add (name);
        }
        return new RefPattern (getDefineName (name));
      }
      return createElementPattern (name);
    }

    public Object visitChoice (final ChoiceParticle p)
    {
      final ChoicePattern cp = new ChoicePattern ();
      final List <Pattern> children = cp.getChildren ();
      addChoices (children, p.getChild1 ());
      addChoices (children, p.getChild2 ());
      Collections.sort (children, this);
      for (final Iterator <Pattern> iter = children.iterator (); iter.hasNext ();)
        if (iter.next () instanceof EmptyPattern)
        {
          iter.remove ();
          return makeOptional (cp);
        }
      return cp;
    }

    private Object makeOptional (final ChoicePattern cp)
    {
      final List <Pattern> children = cp.getChildren ();
      boolean done = false;
      for (int i = 0, len = children.size (); i < len; i++)
      {
        final Pattern child = children.get (i);
        if (child instanceof OneOrMorePattern)
        {
          children.set (i, new ZeroOrMorePattern (((OneOrMorePattern) child).getChild ()));
          done = true;
        }
      }
      if (done)
        return normalize (cp);
      return new OptionalPattern (normalize (cp));
    }

    private void addChoices (final List <Pattern> children, final Particle child)
    {
      final Pattern pattern = convert (child);
      if (pattern instanceof ChoicePattern)
        children.addAll (((ChoicePattern) pattern).getChildren ());
      else
        children.add (pattern);
    }

    public Object visitSequence (final SequenceParticle p)
    {
      final GroupPattern gp = new GroupPattern ();
      addGroup (gp.getChildren (), p.getChild1 ());
      addGroup (gp.getChildren (), p.getChild2 ());
      return gp;
    }

    private void addGroup (final List <Pattern> children, final Particle child)
    {
      final Pattern pattern = convert (child);
      if (pattern instanceof GroupPattern)
        children.addAll (((GroupPattern) pattern).getChildren ());
      else
        children.add (pattern);
    }

    public Object visitEmpty (final EmptyParticle p)
    {
      return new EmptyPattern ();
    }

    public Object visitText (final TextParticle p)
    {
      return new TextPattern ();
    }

    public Object visitOneOrMore (final OneOrMoreParticle p)
    {
      return new OneOrMorePattern (convert (p.getChild ()));
    }

    public Pattern convert (final Particle particle)
    {
      return (Pattern) particle.accept (this);
    }
  }

  private class ReferenceFinder implements ParticleVisitor
  {
    private final Set <Name> referencedElementNames = new HashSet <Name> ();

    public Object visitElement (final ElementParticle p)
    {
      final Name name = p.getName ();
      if (referencedElementNames.contains (name))
        multiplyReferencedElementNames.add (name);
      else
        referencedElementNames.add (name);
      return null;
    }

    public Object visitChoice (final ChoiceParticle p)
    {
      p.getChild1 ().accept (this);
      p.getChild2 ().accept (this);
      return null;
    }

    public Object visitSequence (final SequenceParticle p)
    {
      p.getChild1 ().accept (this);
      p.getChild2 ().accept (this);
      return null;
    }

    public Object visitEmpty (final EmptyParticle p)
    {
      return null;
    }

    public Object visitText (final TextParticle p)
    {
      return null;
    }

    public Object visitOneOrMore (final OneOrMoreParticle p)
    {
      return p.getChild ().accept (this);
    }
  }

  static SchemaCollection infer (final String [] args, final Options options, final ErrorHandler eh) throws SAXException,
                                                                                                    IOException
  {
    final InferHandler handler = new InferHandler (new DatatypeLibraryLoader ());
    final XMLReader xr = new SAXResolver (options.resolver).createXMLReader ();
    xr.setErrorHandler (eh);
    xr.setContentHandler (handler);
    for (final String arg : args)
    {
      final InputSource in = new InputSource (arg);
      if (options.encoding != null)
        in.setEncoding (options.encoding);
      xr.parse (in);
    }
    final SchemaCollection sc = new SchemaCollection ();
    sc.setMainUri (args[0]);
    final SchemaDocument sd = new SchemaDocument (new Inferrer (handler.getSchema ()).grammar);
    sc.getSchemaDocumentMap ().put (sc.getMainUri (), sd);
    return sc;
  }

  private Inferrer (final Schema schema)
  {
    this.schema = schema;
    this.grammar = new GrammarPattern ();
    findMultiplyReferencedElements ();
    choosePrefixSeparator ();
    grammar.getComponents ().add (new DefineComponent (DefineComponent.START,
                                                       particleConverter.convert (schema.getStart ())));
    // Names can get added to outputQueue during this loop,
    // so don't change this to a for each.
    for (int i = 0; i < outputQueue.size (); i++)
    {
      final Name elementName = outputQueue.get (i);
      grammar.getComponents ().add (new DefineComponent (getDefineName (elementName),
                                                         createElementPattern (elementName)));
    }
  }

  private void findMultiplyReferencedElements ()
  {
    final ReferenceFinder finder = new ReferenceFinder ();
    schema.getStart ().accept (finder);
    for (final ElementDecl decl : schema.getElementDecls ().values ())
    {
      final Particle particle = decl.getContentModel ();
      if (particle != null)
        particle.accept (finder);
    }
  }

  private void choosePrefixSeparator ()
  {
    final Map <String, String> prefixMap = schema.getPrefixMap ();
    final Set <String> namespacesInDefines = new HashSet <String> ();
    for (final Name name : multiplyReferencedElementNames)
      namespacesInDefines.add (name.getNamespaceUri ());
    if (namespacesInDefines.size () <= 1)
      return; // don't need to use prefixes in defines
    // define additional prefixes if necessary
    namespacesInDefines.removeAll (prefixMap.keySet ());
    if (namespacesInDefines.size () > 1)
    {
      namespacesInDefines.remove ("");
      int n = 1;
      for (final String ns : namespacesInDefines)
      {
        for (;;)
        {
          final String prefix = "ns" + Integer.toString (n++);
          if (!prefixMap.containsKey (prefix))
          {
            prefixMap.put (ns, prefix);
            break;
          }
        }
      }
    }
    // choose a prefixSeparator that avoids all collisions
    final StringBuffer buf = new StringBuffer ();
    for (int len = 1;; len++)
      for (int i = 0; i < SEPARATORS.length (); i++)
      {
        final char c = SEPARATORS.charAt (i);
        for (int j = 0; j < len; j++)
          buf.append (c);
        prefixSeparator = buf.toString ();
        if (prefixSeparatorOk ())
          return;
        buf.setLength (0);
      }
  }

  private boolean prefixSeparatorOk ()
  {
    final Set <String> names = new HashSet <String> ();
    for (final Name elementName : multiplyReferencedElementNames)
    {
      final String name = getDefineName (elementName);
      if (names.contains (name))
        return false;
      names.add (name);
    }
    return true;
  }

  private Pattern createElementPattern (final Name elementName)
  {
    final ElementDecl elementDecl = schema.getElementDecl (elementName);
    Pattern contentPattern;
    final Particle particle = elementDecl.getContentModel ();
    if (particle != null)
      contentPattern = particleConverter.convert (particle);
    else
      contentPattern = makeDatatype (elementDecl.getDatatype ());
    final Map <Name, AttributeDecl> attributeDecls = elementDecl.getAttributeDecls ();
    if (attributeDecls.size () > 0)
    {
      final GroupPattern group = new GroupPattern ();
      final List <Name> attributeNames = new Vector <Name> ();
      attributeNames.addAll (attributeDecls.keySet ());
      Collections.sort (attributeNames, new Comparator <Name> ()
      {
        public int compare (final Name n1, final Name n2)
        {
          return Name.compare (n1, n2);
        }
      });
      for (final Name attName : attributeNames)
      {
        final AttributeDecl att = attributeDecls.get (attName);
        Pattern tem;
        if (att.getDatatype () == null)
          tem = new TextPattern ();
        else
          tem = makeDatatype (att.getDatatype ());
        tem = new AttributePattern (makeNameClass (attName), tem);
        if (att.isOptional ())
          tem = new OptionalPattern (tem);
        group.getChildren ().add (tem);
      }
      if (contentPattern instanceof GroupPattern)
        group.getChildren ().addAll (((GroupPattern) contentPattern).getChildren ());
      else
        if (!(contentPattern instanceof EmptyPattern))
          group.getChildren ().add (contentPattern);
      contentPattern = group;
    }
    return new ElementPattern (makeNameClass (elementName), contentPattern);
  }

  private NameNameClass makeNameClass (final Name name)
  {
    final String ns = name.getNamespaceUri ();
    final NameNameClass nnc = new NameNameClass (ns, name.getLocalName ());
    if (!ns.equals (""))
    {
      final String prefix = schema.getPrefixMap ().get (ns);
      if (prefix != null)
        nnc.setPrefix (prefix);
    }
    return nnc;
  }

  private static DataPattern makeDatatype (final Name datatypeName)
  {
    return new DataPattern (datatypeName.getNamespaceUri (), datatypeName.getLocalName ());
  }

  private String getDefineName (final Name elementName)
  {
    if (prefixSeparator != null)
    {
      final String prefix = schema.getPrefixMap ().get (elementName.getNamespaceUri ());
      if (prefix != null)
        return prefix + prefixSeparator + elementName.getLocalName ();
    }
    return elementName.getLocalName ();
  }

  private static Pattern normalize (final CompositePattern cp)
  {
    if (cp.getChildren ().size () == 1)
      return cp.getChildren ().get (0);
    return cp;
  }

}
