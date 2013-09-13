package com.thaiopensource.xml.dtd.om;

public class Pcdata extends ModelGroup
{

  public Pcdata ()
  {}

  @Override
  public int getType ()
  {
    return PCDATA;
  }

  @Override
  public void accept (final ModelGroupVisitor visitor) throws Exception
  {
    visitor.pcdata ();
  }
}
