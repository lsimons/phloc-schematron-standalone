package com.thaiopensource.relaxng.output.xsd.basic;

import java.util.Collections;
import java.util.List;

import com.thaiopensource.relaxng.edit.SourceLocation;

public class AttributeGroup extends AttributeUse
{
  private final List <AttributeUse> children;
  private static final List <AttributeUse> EMPTY_LIST = Collections.emptyList ();

  public static final AttributeGroup EMPTY = new AttributeGroup (null, null, EMPTY_LIST);

  public AttributeGroup (final SourceLocation location, final Annotation annotation, final List <AttributeUse> children)
  {
    super (location, annotation);
    this.children = Collections.unmodifiableList (children);
  }

  public List <AttributeUse> getChildren ()
  {
    return children;
  }

  @Override
  public boolean equals (final Object obj)
  {
    return super.equals (obj) && ((AttributeGroup) obj).children.equals (children);
  }

  @Override
  public int hashCode ()
  {
    return super.hashCode () ^ children.hashCode ();
  }

  @Override
  public <T> T accept (final AttributeUseVisitor <T> visitor)
  {
    return visitor.visitAttributeGroup (this);
  }
}
