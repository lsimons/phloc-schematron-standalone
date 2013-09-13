package com.thaiopensource.xml.dtd.om;

public class Attribute extends AttributeGroupMember
{
  private final NameSpec nameSpec;
  private final Datatype datatype;
  private final AttributeDefault attributeDefault;

  public Attribute (final NameSpec nameSpec, final Datatype datatype, final AttributeDefault attributeDefault)
  {
    this.nameSpec = nameSpec;
    this.datatype = datatype;
    this.attributeDefault = attributeDefault;
  }

  public NameSpec getNameSpec ()
  {
    return nameSpec;
  }

  public Datatype getDatatype ()
  {
    return datatype;
  }

  public AttributeDefault getAttributeDefault ()
  {
    return attributeDefault;
  }

  @Override
  public void accept (final AttributeGroupVisitor visitor) throws Exception
  {
    visitor.attribute (nameSpec, datatype, attributeDefault);
  }

  @Override
  public int getType ()
  {
    return ATTRIBUTE;
  }

}
