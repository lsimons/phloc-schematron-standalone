package com.thaiopensource.datatype.xsd;

import java.math.BigDecimal;

import org.relaxng.datatype.DatatypeException;

class PrecisionRestrictDatatype extends ValueRestrictDatatype
{
  private final int precision;

  PrecisionRestrictDatatype (final DatatypeBase base, final int precision)
  {
    super (base);
    this.precision = precision;
  }

  @Override
  void checkRestriction (final Object obj) throws DatatypeException
  {
    final int actualPrecision = getPrecision ((BigDecimal) obj);
    if (actualPrecision > precision)
    {
      String message;
      if (precision == 1)
        message = localizer ().message ("precision_1_violation",
                                        getDescriptionForRestriction (),
                                        Integer.valueOf (actualPrecision));
      else
        message = localizer ().message ("precision_violation",
                                        new Object [] { getDescriptionForRestriction (),
                                                       Integer.valueOf (precision),
                                                       Integer.valueOf (actualPrecision) });
      throw new DatatypeException (message);
    }
  }

  static int getPrecision (final BigDecimal n)
  {
    return n.movePointRight (n.scale ()).abs ().toString ().length ();
  }
}
