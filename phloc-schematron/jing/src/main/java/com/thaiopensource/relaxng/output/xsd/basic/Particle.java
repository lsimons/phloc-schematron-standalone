package com.thaiopensource.relaxng.output.xsd.basic;

import com.thaiopensource.relaxng.edit.SourceLocation;

public abstract class Particle extends Annotated
{
  public Particle (final SourceLocation location, final Annotation annotation)
  {
    super (location, annotation);
  }

  public abstract <T> T accept (ParticleVisitor <T> visitor);
}
