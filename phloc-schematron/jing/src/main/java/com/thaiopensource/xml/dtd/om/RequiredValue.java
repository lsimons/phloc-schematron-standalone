package com.thaiopensource.xml.dtd.om;

public class RequiredValue extends AttributeDefault
{
  @Override
  public int getType ()
  {
    return REQUIRED_VALUE;
  }

  @Override
  public void accept (final AttributeDefaultVisitor visitor) throws Exception
  {
    visitor.requiredValue ();
  }

  @Override
  public boolean isRequired ()
  {
    return true;
  }
}
