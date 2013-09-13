package com.thaiopensource.datatype.xsd;

import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;

class EntityDatatype extends NCNameDatatype
{
  @Override
  boolean allowsValue (final String str, final ValidationContext vc)
  {
    return vc.isUnparsedEntity (str);
  }

  @Override
  Object getValue (final String str, final ValidationContext vc) throws DatatypeException
  {
    if (!allowsValue (str, vc))
      throw new DatatypeException (localizer ().message ("entity_violation"));
    return str;
  }
}
