package com.thaiopensource.xml.dtd.om;

public class CdataDatatype extends Datatype
{
  public CdataDatatype ()
  {}

  @Override
  public int getType ()
  {
    return CDATA;
  }

  @Override
  public void accept (final DatatypeVisitor visitor) throws Exception
  {
    visitor.cdataDatatype ();
  }
}
