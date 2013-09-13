package com.thaiopensource.xml.dtd.om;

public class Include extends Flag
{

  @Override
  public int getType ()
  {
    return INCLUDE;
  }

  @Override
  public void accept (final FlagVisitor visitor) throws Exception
  {
    visitor.include ();
  }
}
