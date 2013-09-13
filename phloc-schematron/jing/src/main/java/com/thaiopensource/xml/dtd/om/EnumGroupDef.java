package com.thaiopensource.xml.dtd.om;

public class EnumGroupDef extends Def
{

  private final EnumGroup enumGroup;

  public EnumGroupDef (final String name, final EnumGroup enumGroup)
  {
    super (name);
    this.enumGroup = enumGroup;
  }

  @Override
  public int getType ()
  {
    return ENUM_GROUP_DEF;
  }

  public EnumGroup getEnumGroup ()
  {
    return enumGroup;
  }

  @Override
  public void accept (final TopLevelVisitor visitor) throws Exception
  {
    visitor.enumGroupDef (getName (), enumGroup);
  }
}
