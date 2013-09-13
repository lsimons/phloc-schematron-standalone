package com.thaiopensource.xml.dtd.om;

public class Ignore extends Flag
{

  @Override
  public int getType ()
  {
    return IGNORE;
  }

  @Override
  public void accept (final FlagVisitor visitor) throws Exception
  {
    visitor.ignore ();
  }
}
