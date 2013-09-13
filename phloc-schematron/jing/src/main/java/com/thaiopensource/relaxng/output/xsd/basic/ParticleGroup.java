package com.thaiopensource.relaxng.output.xsd.basic;

import java.util.Collections;
import java.util.List;

import com.thaiopensource.relaxng.edit.SourceLocation;

public abstract class ParticleGroup extends Particle
{
  private final List <Particle> children;

  public ParticleGroup (final SourceLocation location, final Annotation annotation, final List <Particle> children)
  {
    super (location, annotation);
    this.children = Collections.unmodifiableList (children);
  }

  public List <Particle> getChildren ()
  {
    return children;
  }

  @Override
  public boolean equals (final Object obj)
  {
    return super.equals (obj) && ((ParticleGroup) obj).children.equals (children);
  }

  @Override
  public int hashCode ()
  {
    return super.hashCode () ^ getChildren ().hashCode ();
  }
}
