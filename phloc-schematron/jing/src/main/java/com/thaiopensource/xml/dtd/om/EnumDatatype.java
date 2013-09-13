package com.thaiopensource.xml.dtd.om;

public class EnumDatatype extends Datatype
{
  private final EnumGroup enumGroup;

  public EnumDatatype (final EnumGroup enumGroup)
  {
    this.enumGroup = enumGroup;
  }

  @Override
  public int getType ()
  {
    return ENUM;
  }

  public EnumGroup getEnumGroup ()
  {
    return enumGroup;
  }

  @Override
  public void accept (final DatatypeVisitor visitor) throws Exception
  {
    visitor.enumDatatype (enumGroup);
  }
}
