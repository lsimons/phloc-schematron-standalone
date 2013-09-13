package com.thaiopensource.xml.dtd.om;

public class ImpliedValue extends AttributeDefault
{
  @Override
  public int getType ()
  {
    return IMPLIED_VALUE;
  }

  @Override
  public void accept (final AttributeDefaultVisitor visitor) throws Exception
  {
    visitor.impliedValue ();
  }
}
