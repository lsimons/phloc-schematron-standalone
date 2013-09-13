package com.thaiopensource.xml.infer;

public class ChoiceParticle extends BinaryParticle
{

  public ChoiceParticle (final Particle p1, final Particle p2)
  {
    super (p1, p2);
  }

  @Override
  public Object accept (final ParticleVisitor visitor)
  {
    return visitor.visitChoice (this);
  }
}
