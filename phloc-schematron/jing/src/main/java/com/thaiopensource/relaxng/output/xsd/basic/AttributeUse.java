package com.thaiopensource.relaxng.output.xsd.basic;

import com.thaiopensource.relaxng.edit.SourceLocation;

public abstract class AttributeUse extends Annotated
{
  public AttributeUse (final SourceLocation location, final Annotation annotation)
  {
    super (location, annotation);
  }

  public abstract <T> T accept (AttributeUseVisitor <T> visitor);
}
