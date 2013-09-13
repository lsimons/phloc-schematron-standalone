package com.thaiopensource.xml.dtd.om;

public class InternalEntityDecl extends TopLevel
{

  private final String name;
  private final String value;

  public InternalEntityDecl (final String name, final String value)
  {
    this.name = name;
    this.value = value;
  }

  @Override
  public int getType ()
  {
    return INTERNAL_ENTITY_DECL;
  }

  public String getName ()
  {
    return name;
  }

  public String getValue ()
  {
    return value;
  }

  @Override
  public void accept (final TopLevelVisitor visitor) throws Exception
  {
    visitor.internalEntityDecl (name, value);
  }
}
