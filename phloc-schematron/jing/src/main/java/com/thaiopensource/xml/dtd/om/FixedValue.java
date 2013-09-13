package com.thaiopensource.xml.dtd.om;

public class FixedValue extends AttributeDefault
{
  private final String value;

  public FixedValue (final String value)
  {
    this.value = value;
  }

  @Override
  public int getType ()
  {
    return FIXED_VALUE;
  }

  public String getValue ()
  {
    return value;
  }

  @Override
  public void accept (final AttributeDefaultVisitor visitor) throws Exception
  {
    visitor.fixedValue (value);
  }

  @Override
  public String getDefaultValue ()
  {
    return value;
  }

  @Override
  public String getFixedValue ()
  {
    return value;
  }
}
