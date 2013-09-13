package com.thaiopensource.relaxng.output.xsd.basic;

import com.thaiopensource.relaxng.edit.SourceLocation;

public abstract class Definition extends Annotated implements TopLevel
{
  private final Schema parentSchema;
  private final String name;

  public Definition (final SourceLocation location,
                     final Annotation annotation,
                     final Schema parentSchema,
                     final String name)
  {
    super (location, annotation);
    this.parentSchema = parentSchema;
    this.name = name;
  }

  public Schema getParentSchema ()
  {
    return parentSchema;
  }

  public String getName ()
  {
    return name;
  }
}
