package com.thaiopensource.xml.infer;

public class SequenceParticle extends BinaryParticle
{
  public SequenceParticle (final Particle p1, final Particle p2)
  {
    super (p1, p2);
  }

  @Override
  public Object accept (final ParticleVisitor visitor)
  {
    return visitor.visitSequence (this);
  }
}
