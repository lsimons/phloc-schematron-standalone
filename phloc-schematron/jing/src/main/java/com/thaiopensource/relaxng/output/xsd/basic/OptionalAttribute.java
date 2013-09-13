package com.thaiopensource.relaxng.output.xsd.basic;

import com.thaiopensource.relaxng.edit.SourceLocation;
import com.thaiopensource.util.Equal;
import com.thaiopensource.xml.util.Name;

public class OptionalAttribute extends SingleAttributeUse
{
  private final Attribute attribute;
  private final String defaultValue;

  public OptionalAttribute (final SourceLocation location,
                            final Annotation annotation,
                            final Attribute attribute,
                            final String defaultValue)
  {
    super (location, annotation);
    this.attribute = attribute;
    this.defaultValue = defaultValue;
  }

  public Attribute getAttribute ()
  {
    return attribute;
  }

  @Override
  public <T> T accept (final AttributeUseVisitor <T> visitor)
  {
    return visitor.visitOptionalAttribute (this);
  }

  @Override
  public Name getName ()
  {
    return attribute.getName ();
  }

  @Override
  public SimpleType getType ()
  {
    return attribute.getType ();
  }

  @Override
  public String getDefaultValue ()
  {
    return defaultValue;
  }

  @Override
  public boolean isOptional ()
  {
    return true;
  }

  @Override
  public boolean equals (final Object obj)
  {
    return (super.equals (obj) && ((OptionalAttribute) obj).attribute.equals (attribute) && Equal.equal (defaultValue,
                                                                                                         ((OptionalAttribute) obj).defaultValue));
  }

  @Override
  public int hashCode ()
  {
    int hc = super.hashCode () ^ attribute.hashCode ();
    if (defaultValue != null)
      hc ^= defaultValue.hashCode ();
    return hc;
  }
}
