package com.thaiopensource.relaxng.output.xsd.basic;

import com.thaiopensource.relaxng.edit.SourceLocation;

public class AttributeGroupDefinition extends Definition
{
  private AttributeUse attributeUses;

  public AttributeGroupDefinition (final SourceLocation location,
                                   final Annotation annotation,
                                   final Schema parentSchema,
                                   final String name,
                                   final AttributeUse attributeUses)
  {
    super (location, annotation, parentSchema, name);
    this.attributeUses = attributeUses;
  }

  public AttributeUse getAttributeUses ()
  {
    return attributeUses;
  }

  public void setAttributeUses (final AttributeUse attributeUses)
  {
    this.attributeUses = attributeUses;
  }

  public void accept (final SchemaVisitor visitor)
  {
    visitor.visitAttributeGroup (this);
  }
}
