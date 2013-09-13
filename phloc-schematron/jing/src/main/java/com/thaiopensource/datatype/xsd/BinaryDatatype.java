package com.thaiopensource.datatype.xsd;

abstract class BinaryDatatype extends DatatypeBase implements Measure
{
  BinaryDatatype ()
  {
    // whiteSpace is actually collapse, but we handle it ourselves for
    // efficiency
    super (WHITE_SPACE_PRESERVE);
  }

  @Override
  public int valueHashCode (final Object value)
  {
    final byte [] v = (byte []) value;
    int hc = 0;
    for (final byte element : v)
      hc = (hc * 33) ^ (element & 0xFF);
    return hc;
  }

  @Override
  public boolean sameValue (final Object value1, final Object value2)
  {
    final byte [] v1 = (byte []) value1;
    final byte [] v2 = (byte []) value2;
    if (v1.length != v2.length)
      return false;
    for (int i = 0, len = v1.length; i < len; i++)
      if (v1[i] != v2[i])
        return false;
    return true;
  }

  public int getLength (final Object obj)
  {
    return ((byte []) obj).length;
  }

  @Override
  Measure getMeasure ()
  {
    return this;
  }
}
