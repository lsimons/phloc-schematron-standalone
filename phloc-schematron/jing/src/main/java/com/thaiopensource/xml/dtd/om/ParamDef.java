package com.thaiopensource.xml.dtd.om;

public class ParamDef extends Def
{
  private final String value;

  public ParamDef (final String name, final String value)
  {
    super (name);
    this.value = value;
  }

  @Override
  public int getType ()
  {
    return PARAM_DEF;
  }

  public String getValue ()
  {
    return value;
  }

  @Override
  public void accept (final TopLevelVisitor visitor) throws Exception
  {
    visitor.paramDef (getName (), value);
  }

}
