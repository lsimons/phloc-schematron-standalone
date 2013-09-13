package com.thaiopensource.relaxng.output.xsd.basic;

import java.util.List;

import com.thaiopensource.relaxng.edit.SourceLocation;

public class ParticleSequence extends ParticleGroup
{
  public ParticleSequence (final SourceLocation location, final Annotation annotation, final List <Particle> children)
  {
    super (location, annotation, children);
  }

  @Override
  public <T> T accept (final ParticleVisitor <T> visitor)
  {
    return visitor.visitSequence (this);
  }
}
