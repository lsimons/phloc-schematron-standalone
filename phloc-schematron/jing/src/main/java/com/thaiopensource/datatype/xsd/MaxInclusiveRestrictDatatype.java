package com.thaiopensource.datatype.xsd;

import org.relaxng.datatype.DatatypeException;

class MaxInclusiveRestrictDatatype extends ValueRestrictDatatype
{
  private final OrderRelation order;
  private final Object limit;
  private final String limitString;

  MaxInclusiveRestrictDatatype (final DatatypeBase base, final Object limit, final String limitString)
  {
    super (base);
    this.order = base.getOrderRelation ();
    this.limit = limit;
    this.limitString = limitString;
  }

  @Override
  void checkRestriction (final Object value) throws DatatypeException
  {
    if (!order.isLessThan (value, limit) && !super.sameValue (value, limit))
      throw new DatatypeException (localizer ().message ("max_inclusive_violation",
                                                         getDescriptionForRestriction (),
                                                         limitString));
  }
}
