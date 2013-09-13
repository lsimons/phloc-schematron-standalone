package com.thaiopensource.xml.dtd.om;

public class ElementRef extends ModelGroup
{

  private final NameSpec nameSpec;

  public ElementRef (final NameSpec nameSpec)
  {
    this.nameSpec = nameSpec;
  }

  @Override
  public int getType ()
  {
    return ELEMENT_REF;
  }

  public NameSpec getNameSpec ()
  {
    return nameSpec;
  }

  @Override
  public void accept (final ModelGroupVisitor visitor) throws Exception
  {
    visitor.elementRef (nameSpec);
  }

}
