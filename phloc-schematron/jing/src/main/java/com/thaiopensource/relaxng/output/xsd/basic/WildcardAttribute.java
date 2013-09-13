package com.thaiopensource.relaxng.output.xsd.basic;

import com.thaiopensource.relaxng.edit.SourceLocation;

public class WildcardAttribute extends AttributeUse
{
  private final Wildcard wildcard;

  public WildcardAttribute (final SourceLocation location, final Annotation annotation, final Wildcard wildcard)
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
    return super.equals (obj) && ((WildcardAttribute) obj).wildcard.equals (wildcard);
  }

  @Override
  public int hashCode ()
  {
    return super.hashCode () ^ wildcard.hashCode ();
  }

  @Override
  public <T> T accept (final AttributeUseVisitor <T> visitor)
  {
    return visitor.visitWildcardAttribute (this);
  }
}
