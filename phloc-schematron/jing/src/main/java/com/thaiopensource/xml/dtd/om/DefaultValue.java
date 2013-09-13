package com.thaiopensource.xml.dtd.om;

public class DefaultValue extends AttributeDefault
{
  private final String value;

  public DefaultValue (final String value)
  {
    this.value = value;
  }

  @Override
  public int getType ()
  {
    return DEFAULT_VALUE;
  }

  public String getValue ()
  {
    return value;
  }

  @Override
  public void accept (final AttributeDefaultVisitor visitor) throws Exception
  {
    visitor.defaultValue (value);
  }

  @Override
  public String getDefaultValue ()
  {
    return value;
  }
}
