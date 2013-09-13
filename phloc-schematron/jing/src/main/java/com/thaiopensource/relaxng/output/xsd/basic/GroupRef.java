package com.thaiopensource.relaxng.output.xsd.basic;

import com.thaiopensource.relaxng.edit.SourceLocation;

public class GroupRef extends Particle
{
  private final String name;

  public GroupRef (final SourceLocation location, final Annotation annotation, final String name)
  {
    super (location, annotation);
    this.name = name;
  }

  public String getName ()
  {
    return name;
  }

  @Override
  public <T> T accept (final ParticleVisitor <T> visitor)
  {
    return visitor.visitGroupRef (this);
  }

  @Override
  public boolean equals (final Object obj)
  {
    return super.equals (obj) && name.equals (((GroupRef) obj).name);
  }

  @Override
  public int hashCode ()
  {
    return super.hashCode () ^ name.hashCode ();
  }
}
