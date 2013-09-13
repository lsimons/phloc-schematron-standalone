package com.thaiopensource.xml.dtd.om;

public class FlagRef extends Flag
{
  private final String name;
  private final Flag flag;

  public FlagRef (final String name, final Flag flag)
  {
    this.name = name;
    this.flag = flag;
  }

  @Override
  public int getType ()
  {
    return FLAG_REF;
  }

  public Flag getFlag ()
  {
    return flag;
  }

  public String getName ()
  {
    return name;
  }

  @Override
  public void accept (final FlagVisitor visitor) throws Exception
  {
    visitor.flagRef (name, flag);
  }
}
