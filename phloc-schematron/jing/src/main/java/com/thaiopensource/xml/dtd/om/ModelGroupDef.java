package com.thaiopensource.xml.dtd.om;

public class ModelGroupDef extends Def
{

  private final ModelGroup modelGroup;

  public ModelGroupDef (final String name, final ModelGroup modelGroup)
  {
    super (name);
    this.modelGroup = modelGroup;
  }

  @Override
  public int getType ()
  {
    return MODEL_GROUP_DEF;
  }

  public ModelGroup getModelGroup ()
  {
    return modelGroup;
  }

  @Override
  public void accept (final TopLevelVisitor visitor) throws Exception
  {
    visitor.modelGroupDef (getName (), modelGroup);
  }
}
