package com.thaiopensource.datatype.xsd;

import org.relaxng.datatype.ValidationContext;

class FloatDatatype extends DoubleDatatype
{

  @Override
  Object getValue (final String str, final ValidationContext vc)
  {
    if (str.equals ("INF"))
      return Float.valueOf (Float.POSITIVE_INFINITY);
    if (str.equals ("-INF"))
      return Float.valueOf (Float.NEGATIVE_INFINITY);
    if (str.equals ("NaN"))
      return Float.valueOf (Float.NaN);
    return new Float (str);
  }

  @Override
  public boolean isLessThan (final Object obj1, final Object obj2)
  {
    return ((Float) obj1).compareTo ((Float) obj2) < 0;
  }

  @Override
  public boolean sameValue (final Object value1, final Object value2)
  {
    final float f1 = ((Float) value1).floatValue ();
    final float f2 = ((Float) value2).floatValue ();
    // NaN = NaN
    return f1 == f2 || (f1 != f1 && f2 != f2);
  }
}
