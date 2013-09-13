package com.thaiopensource.xml.infer;

import com.thaiopensource.xml.util.Name;

public class ElementParticle extends Particle
{
  private final Name name;

  public ElementParticle (final Name name)
  {
    this.name = name;
  }

  public Name getName ()
  {
    return name;
  }

  @Override
  public Object accept (final ParticleVisitor visitor)
  {
    return visitor.visitElement (this);
  }

}
