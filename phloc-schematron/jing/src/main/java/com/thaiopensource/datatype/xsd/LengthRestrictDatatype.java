package com.thaiopensource.datatype.xsd;

import org.relaxng.datatype.DatatypeException;

class LengthRestrictDatatype extends ValueRestrictDatatype
{
  private final int length;
  private final Measure measure;

  LengthRestrictDatatype (final DatatypeBase base, final int length)
  {
    super (base);
    this.measure = base.getMeasure ();
    this.length = length;
  }

  @Override
  void checkRestriction (final Object obj) throws DatatypeException
  {
    final int actualLength = measure.getLength (obj);
    if (actualLength != length)
      throw new DatatypeException (localizer ().message ("length_violation",
                                                         new Object [] { getDescriptionForRestriction (),
                                                                        Integer.valueOf (length),
                                                                        Integer.valueOf (actualLength) }));
  }
}
