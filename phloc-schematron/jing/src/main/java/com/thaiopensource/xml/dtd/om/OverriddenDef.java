package com.thaiopensource.xml.dtd.om;

public class OverriddenDef extends TopLevel
{
  private final boolean duplicate;
  private final Def def;

  public OverriddenDef (final Def def, final boolean duplicate)
  {
    this.def = def;
    this.duplicate = duplicate;
  }

  @Override
  public int getType ()
  {
    return OVERRIDDEN_DEF;
  }

  public Def getDef ()
  {
    return def;
  }

  public boolean isDuplicate ()
  {
    return duplicate;
  }

  @Override
  public void accept (final TopLevelVisitor visitor) throws Exception
  {
    visitor.overriddenDef (def, duplicate);
  }

}
