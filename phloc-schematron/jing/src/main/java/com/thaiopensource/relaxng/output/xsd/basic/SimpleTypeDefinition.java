package com.thaiopensource.relaxng.output.xsd.basic;

import com.thaiopensource.relaxng.edit.SourceLocation;

public class SimpleTypeDefinition extends Definition
{
  private SimpleType simpleType;

  public SimpleTypeDefinition (final SourceLocation location,
                               final Annotation annotation,
                               final Schema parentSchema,
                               final String name,
                               final SimpleType simpleType)
  {
    super (location, annotation, parentSchema, name);
    this.simpleType = simpleType;
  }

  public SimpleType getSimpleType ()
  {
    return simpleType;
  }

  public void setSimpleType (final SimpleType simpleType)
  {
    this.simpleType = simpleType;
  }

  public void accept (final SchemaVisitor visitor)
  {
    visitor.visitSimpleType (this);
  }
}
