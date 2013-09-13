package com.thaiopensource.relaxng.output.xsd.basic;

import java.util.Collections;
import java.util.List;

import com.thaiopensource.relaxng.edit.SourceLocation;

public class SimpleTypeUnion extends SimpleType
{
  private final List <SimpleType> children;

  public SimpleTypeUnion (final SourceLocation location, final Annotation annotation, final List <SimpleType> children)
  {
    super (location, annotation);
    this.children = Collections.unmodifiableList (children);
  }

  public List <SimpleType> getChildren ()
  {
    return children;
  }

  @Override
  public <T> T accept (final SimpleTypeVisitor <T> visitor)
  {
    return visitor.visitUnion (this);
  }

  @Override
  public boolean equals (final Object obj)
  {
    return super.equals (obj) && children.equals (((SimpleTypeUnion) obj).children);
  }

  @Override
  public int hashCode ()
  {
    return super.hashCode () ^ children.hashCode ();
  }
}
