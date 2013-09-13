package com.thaiopensource.xml.dtd.om;

public class Any extends ModelGroup
{

  public Any ()
  {}

  @Override
  public int getType ()
  {
    return ANY;
  }

  @Override
  public void accept (final ModelGroupVisitor visitor) throws Exception
  {
    visitor.any ();
  }
}
