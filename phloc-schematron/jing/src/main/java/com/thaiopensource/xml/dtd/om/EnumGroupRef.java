package com.thaiopensource.xml.dtd.om;

public class EnumGroupRef extends EnumGroupMember
{

  private final String name;
  private final EnumGroup enumGroup;

  public EnumGroupRef (final String name, final EnumGroup enumGroup)
  {
    this.name = name;
    this.enumGroup = enumGroup;
  }

  @Override
  public int getType ()
  {
    return ENUM_GROUP_REF;
  }

  public EnumGroup getEnumGroup ()
  {
    return enumGroup;
  }

  public String getName ()
  {
    return name;
  }

  @Override
  public void accept (final EnumGroupVisitor visitor) throws Exception
  {
    visitor.enumGroupRef (name, enumGroup);
  }
}
