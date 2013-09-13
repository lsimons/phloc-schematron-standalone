package com.thaiopensource.datatype.xsd;

class IdrefDatatype extends NCNameDatatype
{
  @Override
  public int getIdType ()
  {
    return ID_TYPE_IDREF;
  }
}
