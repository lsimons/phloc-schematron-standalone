package com.thaiopensource.xml.infer;

public class TextParticle extends Particle
{
  @Override
  public Object accept (final ParticleVisitor visitor)
  {
    return visitor.visitText (this);
  }
}
