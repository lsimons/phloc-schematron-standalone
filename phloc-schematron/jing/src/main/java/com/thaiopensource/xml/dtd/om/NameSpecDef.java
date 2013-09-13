package com.thaiopensource.xml.dtd.om;

public class NameSpecDef extends Def
{
  private final NameSpec nameSpec;

  public NameSpecDef (final String name, final NameSpec nameSpec)
  {
    super (name);
    this.nameSpec = nameSpec;
  }

  @Override
  public int getType ()
  {
    return NAME_SPEC_DEF;
  }

  public NameSpec getNameSpec ()
  {
    return nameSpec;
  }

  @Override
  public void accept (final TopLevelVisitor visitor) throws Exception
  {
    visitor.nameSpecDef (getName (), nameSpec);
  }
}
