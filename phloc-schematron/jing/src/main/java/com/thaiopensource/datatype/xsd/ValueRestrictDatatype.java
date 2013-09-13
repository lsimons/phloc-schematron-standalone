package com.thaiopensource.datatype.xsd;

import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;

abstract class ValueRestrictDatatype extends RestrictDatatype
{
  ValueRestrictDatatype (final DatatypeBase base)
  {
    super (base);
  }

  @Override
  Object getValue (final String str, final ValidationContext vc) throws DatatypeException
  {
    final Object obj = super.getValue (str, vc);
    checkRestriction (obj);
    return obj;
  }

  abstract void checkRestriction (Object obj) throws DatatypeException;
}
