package com.thaiopensource.relaxng.output.xsd.basic;

import com.thaiopensource.relaxng.edit.SourceLocation;

public class SimpleTypeRef extends SimpleType
{
  private final String name;

  public SimpleTypeRef (final SourceLocation location, final Annotation annotation, final String name)
  {
    super (location, annotation);
    this.name = name;
  }

  public String getName ()
  {
    return name;
  }

  @Override
  public <T> T accept (final SimpleTypeVisitor <T> visitor)
  {
    return visitor.visitRef (this);
  }

  @Override
  public boolean equals (final Object obj)
  {
    return super.equals (obj) && ((SimpleTypeRef) obj).name.equals (name);
  }

  @Override
  public int hashCode ()
  {
    return super.hashCode () ^ name.hashCode ();
  }
}
