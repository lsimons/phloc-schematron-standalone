package com.thaiopensource.xml.dtd.om;

public class FlagDef extends Def
{

  private final Flag flag;

  public FlagDef (final String name, final Flag flag)
  {
    super (name);
    this.flag = flag;
  }

  @Override
  public int getType ()
  {
    return FLAG_DEF;
  }

  public Flag getFlag ()
  {
    return flag;
  }

  @Override
  public void accept (final TopLevelVisitor visitor) throws Exception
  {
    visitor.flagDef (getName (), flag);
  }
}
