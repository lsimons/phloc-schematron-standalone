package com.thaiopensource.xml.dtd.om;

public class DatatypeDef extends Def
{

  private final Datatype datatype;

  public DatatypeDef (final String name, final Datatype datatype)
  {
    super (name);
    this.datatype = datatype;
  }

  @Override
  public int getType ()
  {
    return DATATYPE_DEF;
  }

  public Datatype getDatatype ()
  {
    return datatype;
  }

  @Override
  public void accept (final TopLevelVisitor visitor) throws Exception
  {
    visitor.datatypeDef (getName (), datatype);
  }
}
