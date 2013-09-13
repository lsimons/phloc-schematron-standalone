package com.thaiopensource.xml.dtd.om;

public class AttributeGroupDef extends Def
{

  private final AttributeGroup attributeGroup;

  public AttributeGroupDef (final String name, final AttributeGroup attributeGroup)
  {
    super (name);
    this.attributeGroup = attributeGroup;
  }

  @Override
  public int getType ()
  {
    return ATTRIBUTE_GROUP_DEF;
  }

  public AttributeGroup getAttributeGroup ()
  {
    return attributeGroup;
  }

  @Override
  public void accept (final TopLevelVisitor visitor) throws Exception
  {
    visitor.attributeGroupDef (getName (), attributeGroup);
  }
}
