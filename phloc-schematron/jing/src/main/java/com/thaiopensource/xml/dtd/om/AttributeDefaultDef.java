package com.thaiopensource.xml.dtd.om;

public class AttributeDefaultDef extends Def
{

  private final AttributeDefault attributeDefault;

  public AttributeDefaultDef (final String name, final AttributeDefault attributeDefault)
  {
    super (name);
    this.attributeDefault = attributeDefault;
  }

  @Override
  public int getType ()
  {
    return ATTRIBUTE_DEFAULT_DEF;
  }

  public AttributeDefault getAttributeDefault ()
  {
    return attributeDefault;
  }

  @Override
  public void accept (final TopLevelVisitor visitor) throws Exception
  {
    visitor.attributeDefaultDef (getName (), attributeDefault);
  }
}
