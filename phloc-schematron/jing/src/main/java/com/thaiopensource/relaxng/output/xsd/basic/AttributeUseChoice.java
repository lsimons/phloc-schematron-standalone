package com.thaiopensource.relaxng.output.xsd.basic;

import java.util.List;

import com.thaiopensource.relaxng.edit.SourceLocation;

public class AttributeUseChoice extends AttributeGroup
{
  public AttributeUseChoice (final SourceLocation location,
                             final Annotation annotation,
                             final List <AttributeUse> children)
  {
    super (location, annotation, children);
  }

  @Override
  public <T> T accept (final AttributeUseVisitor <T> visitor)
  {
    return visitor.visitAttributeUseChoice (this);
  }
}
