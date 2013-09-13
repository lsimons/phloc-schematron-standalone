package com.thaiopensource.xml.dtd.om;

public class Name extends NameSpec
{
  private final String value;

  public Name (final String value)
  {
    this.value = value;
  }

  @Override
  public int getType ()
  {
    return NAME;
  }

  @Override
  public String getValue ()
  {
    return value;
  }

  @Override
  public void accept (final NameSpecVisitor visitor) throws Exception
  {
    visitor.name (value);
  }
}
