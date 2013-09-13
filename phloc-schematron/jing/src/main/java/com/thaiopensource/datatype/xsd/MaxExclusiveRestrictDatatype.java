package com.thaiopensource.datatype.xsd;

import org.relaxng.datatype.DatatypeException;

class MaxExclusiveRestrictDatatype extends ValueRestrictDatatype
{
  private final OrderRelation order;
  private final Object limit;
  private final String limitString;

  MaxExclusiveRestrictDatatype (final DatatypeBase base, final Object limit, final String limitString)
  {
    super (base);
    this.order = base.getOrderRelation ();
    this.limit = limit;
    this.limitString = limitString;
  }

  @Override
  void checkRestriction (final Object value) throws DatatypeException
  {
    if (!order.isLessThan (value, limit))
      throw new DatatypeException (localizer ().message ("max_exclusive_violation",
                                                         getDescriptionForRestriction (),
                                                         limitString));
  }
}
