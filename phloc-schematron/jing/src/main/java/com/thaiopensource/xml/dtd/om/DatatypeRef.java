package com.thaiopensource.xml.dtd.om;

public class DatatypeRef extends Datatype
{

  private final String name;
  private final Datatype datatype;

  public DatatypeRef (final String name, final Datatype datatype)
  {
    this.name = name;
    this.datatype = datatype;
  }

  @Override
  public int getType ()
  {
    return DATATYPE_REF;
  }

  public Datatype getDatatype ()
  {
    return datatype;
  }

  public String getName ()
  {
    return name;
  }

  @Override
  public void accept (final DatatypeVisitor visitor) throws Exception
  {
    visitor.datatypeRef (name, datatype);
  }

  @Override
  public Datatype deref ()
  {
    return datatype.deref ();
  }
}
