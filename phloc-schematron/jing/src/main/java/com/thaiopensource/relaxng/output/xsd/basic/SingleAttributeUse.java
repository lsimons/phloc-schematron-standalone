package com.thaiopensource.relaxng.output.xsd.basic;

import com.thaiopensource.relaxng.edit.SourceLocation;
import com.thaiopensource.xml.util.Name;

public abstract class SingleAttributeUse extends AttributeUse
{
  public SingleAttributeUse (final SourceLocation location, final Annotation annotation)
  {
    super (location, annotation);
  }

  public abstract Name getName ();

  public abstract SimpleType getType ();

  public abstract boolean isOptional ();

  public String getDefaultValue ()
  {
    return null;
  }
}
