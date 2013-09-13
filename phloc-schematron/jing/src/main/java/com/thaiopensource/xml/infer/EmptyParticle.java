package com.thaiopensource.xml.infer;

public class EmptyParticle extends Particle
{
  @Override
  public Object accept (final ParticleVisitor visitor)
  {
    return visitor.visitEmpty (this);
  }
}
