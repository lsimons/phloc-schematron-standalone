package com.thaiopensource.relaxng.output.xsd.basic;

import com.thaiopensource.relaxng.edit.SourceLocation;

public class WildcardElement extends Particle
{
  private final Wildcard wildcard;

  public WildcardElement (final SourceLocation location, final Annotation annotation, final Wildcard wildcard)
  {
    super (location, annotation);
    this.wildcard = wildcard;
  }

  public Wildcard getWildcard ()
  {
    return wildcard;
  }

  @Override
  public boolean equals (final Object obj)
  {
    return super.equals (obj) && ((WildcardElement) obj).wildcard.equals (wildcard);
  }

  @Override
  public int hashCode ()
  {
    return super.hashCode () ^ wildcard.hashCode ();
  }

  @Override
  public <T> T accept (final ParticleVisitor <T> visitor)
  {
    return visitor.visitWildcardElement (this);
  }
}
