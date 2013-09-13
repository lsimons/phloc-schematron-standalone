package com.thaiopensource.datatype.xsd;

import java.math.BigDecimal;

import org.relaxng.datatype.DatatypeException;

class ScaleRestrictDatatype extends ValueRestrictDatatype
{
  private final int scale;

  ScaleRestrictDatatype (final DatatypeBase base, final int scale)
  {
    super (base);
    this.scale = scale;
  }

  @Override
  void checkRestriction (final Object obj) throws DatatypeException
  {
    final int actualScale = ((BigDecimal) obj).scale ();
    if (actualScale > scale)
    {
      String message;
      switch (scale)
      {
        case 0:
          message = localizer ().message ("scale_0_violation");
          break;
        case 1:
          message = localizer ().message ("scale_1_violation", Integer.valueOf (actualScale));
          break;
        default:
          message = localizer ().message ("scale_violation", Integer.valueOf (scale), Integer.valueOf (actualScale));
          break;
      }
      throw new DatatypeException (message);
    }
  }
}
