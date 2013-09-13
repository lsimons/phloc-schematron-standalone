package com.thaiopensource.xml.infer;

public class OneOrMoreParticle extends Particle
{
  private final Particle child;

  public OneOrMoreParticle (final Particle child)
  {
    this.child = child;
  }

  public Particle getChild ()
  {
    return child;
  }

  @Override
  public Object accept (final ParticleVisitor visitor)
  {
    return visitor.visitOneOrMore (this);
  }
}
