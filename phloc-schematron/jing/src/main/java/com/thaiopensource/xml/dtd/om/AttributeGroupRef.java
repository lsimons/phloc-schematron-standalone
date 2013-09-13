package com.thaiopensource.xml.dtd.om;

public class AttributeGroupRef extends AttributeGroupMember
{

  private final String name;
  private final AttributeGroup attributeGroup;

  public AttributeGroupRef (final String name, final AttributeGroup attributeGroup)
  {
    this.name = name;
    this.attributeGroup = attributeGroup;
  }

  @Override
  public int getType ()
  {
    return ATTRIBUTE_GROUP_REF;
  }

  public AttributeGroup getAttributeGroup ()
  {
    return attributeGroup;
  }

  public String getName ()
  {
    return name;
  }

  @Override
  public void accept (final AttributeGroupVisitor visitor) throws Exception
  {
    visitor.attributeGroupRef (name, attributeGroup);
  }
}
