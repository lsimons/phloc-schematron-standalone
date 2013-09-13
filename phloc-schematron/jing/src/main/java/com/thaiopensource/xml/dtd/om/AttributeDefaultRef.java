package com.thaiopensource.xml.dtd.om;

public class AttributeDefaultRef extends AttributeDefault
{

  private final String name;
  private final AttributeDefault attributeDefault;

  public AttributeDefaultRef (final String name, final AttributeDefault attributeDefault)
  {
    this.name = name;
    this.attributeDefault = attributeDefault;
  }

  @Override
  public int getType ()
  {
    return ATTRIBUTE_DEFAULT_REF;
  }

  public AttributeDefault getAttributeDefault ()
  {
    return attributeDefault;
  }

  public String getName ()
  {
    return name;
  }

  @Override
  public void accept (final AttributeDefaultVisitor visitor) throws Exception
  {
    visitor.attributeDefaultRef (name, attributeDefault);
  }

  @Override
  public boolean isRequired ()
  {
    return attributeDefault.isRequired ();
  }

  @Override
  public String getDefaultValue ()
  {
    return attributeDefault.getDefaultValue ();
  }

  @Override
  public String getFixedValue ()
  {
    return attributeDefault.getFixedValue ();
  }
}
