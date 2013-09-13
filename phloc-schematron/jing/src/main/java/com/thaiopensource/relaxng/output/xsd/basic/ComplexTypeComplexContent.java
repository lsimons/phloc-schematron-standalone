package com.thaiopensource.relaxng.output.xsd.basic;

public class ComplexTypeComplexContent extends ComplexTypeAllowedContent
{
  private final Particle particle;
  private final boolean mixed;

  /**
   * particle may be null
   */
  public ComplexTypeComplexContent (final AttributeUse attributeUses, final Particle particle, final boolean mixed)
  {
    super (attributeUses);
    this.particle = particle;
    this.mixed = mixed;
  }

  public Particle getParticle ()
  {
    return particle;
  }

  @Override
  public boolean isMixed ()
  {
    return mixed;
  }

  @Override
  public <T> T accept (final ComplexTypeVisitor <T> visitor)
  {
    return visitor.visitComplexContent (this);
  }

  @Override
  public boolean equals (final Object obj)
  {
    if (!(obj instanceof ComplexTypeComplexContent))
      return false;
    final ComplexTypeComplexContent other = (ComplexTypeComplexContent) obj;
    if (particle == null)
    {
      if (other.particle != null)
        return false;
    }
    else
      if (!particle.equals (other.particle))
        return false;
    return getAttributeUses ().equals (other.getAttributeUses ()) && mixed == other.mixed;
  }

  @Override
  public int hashCode ()
  {
    int hc = getAttributeUses ().hashCode ();
    if (particle != null)
      hc ^= particle.hashCode ();
    return hc;
  }
}
