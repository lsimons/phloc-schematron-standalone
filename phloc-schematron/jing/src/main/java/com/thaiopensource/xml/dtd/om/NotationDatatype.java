package com.thaiopensource.xml.dtd.om;

public class NotationDatatype extends EnumDatatype
{
  public NotationDatatype (final EnumGroup enumGroup)
  {
    super (enumGroup);
  }

  @Override
  public int getType ()
  {
    return NOTATION;
  }

  @Override
  public void accept (final DatatypeVisitor visitor) throws Exception
  {
    visitor.notationDatatype (getEnumGroup ());
  }
}
