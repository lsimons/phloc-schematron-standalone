package com.thaiopensource.xml.dtd.om;

public class AttlistDecl extends TopLevel
{

  private final NameSpec elementNameSpec;
  private final AttributeGroup attributeGroup;

  public AttlistDecl (final NameSpec elementNameSpec, final AttributeGroup attributeGroup)
  {
    this.elementNameSpec = elementNameSpec;
    this.attributeGroup = attributeGroup;
  }

  @Override
  public int getType ()
  {
    return ATTLIST_DECL;
  }

  public NameSpec getElementNameSpec ()
  {
    return elementNameSpec;
  }

  public AttributeGroup getAttributeGroup ()
  {
    return attributeGroup;
  }

  @Override
  public void accept (final TopLevelVisitor visitor) throws Exception
  {
    visitor.attlistDecl (elementNameSpec, attributeGroup);
  }

}
