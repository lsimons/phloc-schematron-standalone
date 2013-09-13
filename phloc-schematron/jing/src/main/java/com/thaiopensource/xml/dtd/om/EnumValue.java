package com.thaiopensource.xml.dtd.om;

public class EnumValue extends EnumGroupMember
{
  private final String value;

  public EnumValue (final String value)
  {
    this.value = value;
  }

  public String getValue ()
  {
    return value;
  }

  @Override
  public void accept (final EnumGroupVisitor visitor) throws Exception
  {
    visitor.enumValue (value);
  }

  @Override
  public int getType ()
  {
    return ENUM_VALUE;
  }

}
