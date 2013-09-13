package com.thaiopensource.xml.infer;

import com.thaiopensource.xml.util.Name;

public class ParticleDumper implements ParticleVisitor
{

  final private String defaultNamespace;

  private ParticleDumper (final String defaultNamespace)
  {
    this.defaultNamespace = defaultNamespace;
  }

  public static String toString (final Particle p, final String defaultNamespace)
  {
    return new ParticleDumper (defaultNamespace).convert (p);
  }

  private String convert (final Particle p)
  {
    return (String) p.accept (this);
  }

  public Object visitElement (final ElementParticle p)
  {
    final Name name = p.getName ();
    final String ns = name.getNamespaceUri ();
    if (ns.equals (defaultNamespace))
      return name.getLocalName ();
    return "{" + name.getNamespaceUri () + "}" + name.getLocalName ();

  }

  public Object visitChoice (final ChoiceParticle p)
  {
    final StringBuffer buf = new StringBuffer ();
    buf.append ("(");
    convertForChoice (p, buf);
    buf.append (")");
    return buf.toString ();
  }

  private void convertForChoice (final Particle p, final StringBuffer buf)
  {
    if (p instanceof ChoiceParticle)
      convertForChoice ((ChoiceParticle) p, buf);
    else
      buf.append (convert (p));
  }

  private void convertForChoice (final ChoiceParticle cp, final StringBuffer buf)
  {
    convertForChoice (cp.getChild1 (), buf);
    buf.append ('|');
    convertForChoice (cp.getChild2 (), buf);
  }

  public Object visitSequence (final SequenceParticle p)
  {
    final StringBuffer buf = new StringBuffer ();
    buf.append ("(");
    convertForSequence (p, buf);
    buf.append (")");
    return buf.toString ();
  }

  private void convertForSequence (final Particle p, final StringBuffer buf)
  {
    if (p instanceof SequenceParticle)
      convertForSequence ((SequenceParticle) p, buf);
    else
      buf.append (convert (p));
  }

  private void convertForSequence (final SequenceParticle sp, final StringBuffer buf)
  {
    convertForSequence (sp.getChild1 (), buf);
    buf.append (',');
    convertForSequence (sp.getChild2 (), buf);
  }

  public Object visitEmpty (final EmptyParticle p)
  {
    return "#empty";
  }

  public Object visitText (final TextParticle p)
  {
    return "#text";
  }

  public Object visitOneOrMore (final OneOrMoreParticle p)
  {
    return convert (p.getChild ()) + "+";
  }
}
