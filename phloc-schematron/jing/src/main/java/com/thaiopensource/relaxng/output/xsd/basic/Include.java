package com.thaiopensource.relaxng.output.xsd.basic;

import com.thaiopensource.relaxng.edit.SourceLocation;

public class Include extends Annotated implements TopLevel
{
  private final Schema includedSchema;

  public Include (final SourceLocation location, final Annotation annotation, final Schema includedSchema)
  {
    super (location, annotation);
    this.includedSchema = includedSchema;
  }

  public Schema getIncludedSchema ()
  {
    return includedSchema;
  }

  public void accept (final SchemaVisitor visitor)
  {
    visitor.visitInclude (this);
  }
}
