package com.thaiopensource.xml.dtd.om;

public class ElementDecl extends TopLevel
{

  private final NameSpec nameSpec;
  private final ModelGroup modelGroup;

  public ElementDecl (final NameSpec nameSpec, final ModelGroup modelGroup)
  {
    this.nameSpec = nameSpec;
    this.modelGroup = modelGroup;
  }

  @Override
  public int getType ()
  {
    return ELEMENT_DECL;
  }

  public NameSpec getNameSpec ()
  {
    return nameSpec;
  }

  public ModelGroup getModelGroup ()
  {
    return modelGroup;
  }

  @Override
  public void accept (final TopLevelVisitor visitor) throws Exception
  {
    visitor.elementDecl (nameSpec, modelGroup);
  }

}
