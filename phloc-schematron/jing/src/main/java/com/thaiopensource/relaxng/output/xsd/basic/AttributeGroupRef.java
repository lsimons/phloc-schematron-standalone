package com.thaiopensource.relaxng.output.xsd.basic;

import com.thaiopensource.relaxng.edit.SourceLocation;

public class AttributeGroupRef extends AttributeUse
{
  private final String name;

  public AttributeGroupRef (final SourceLocation location, final Annotation annotation, final String name)
  {
    super (location, annotation);
    this.name = name;
  }

  public String getName ()
  {
    return name;
  }

  @Override
  public <T> T accept (final AttributeUseVisitor <T> visitor)
  {
    return visitor.visitAttributeGroupRef (this);
  }

  @Override
  public boolean equals (final Object obj)
  {
    return super.equals (obj) && ((AttributeGroupRef) obj).name.equals (name);
  }

  @Override
  public int hashCode ()
  {
    return super.hashCode () ^ name.hashCode ();
  }
}
