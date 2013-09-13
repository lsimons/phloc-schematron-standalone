package com.thaiopensource.datatype.xsd;

import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;

abstract class RestrictDatatype extends DatatypeBase
{
  protected final DatatypeBase base;

  RestrictDatatype (final DatatypeBase base)
  {
    this (base, base.getWhiteSpace ());
  }

  RestrictDatatype (final DatatypeBase base, final int whiteSpace)
  {
    super (whiteSpace);
    this.base = base;
  }

  @Override
  boolean lexicallyAllows (final String str)
  {
    return base.lexicallyAllows (str);
  }

  @Override
  void checkLexicallyAllows (final String str) throws DatatypeException
  {
    base.checkLexicallyAllows (str);
  }

  @Override
  String getLexicalSpaceKey ()
  {
    return base.getLexicalSpaceKey ();
  }

  @Override
  OrderRelation getOrderRelation ()
  {
    return base.getOrderRelation ();
  }

  @Override
  Measure getMeasure ()
  {
    return base.getMeasure ();
  }

  @Override
  DatatypeBase getPrimitive ()
  {
    return base.getPrimitive ();
  }

  @Override
  public int getIdType ()
  {
    return base.getIdType ();
  }

  @Override
  public boolean sameValue (final Object value1, final Object value2)
  {
    return base.sameValue (value1, value2);
  }

  @Override
  public int valueHashCode (final Object value)
  {
    return base.valueHashCode (value);
  }

  @Override
  Object getValue (final String str, final ValidationContext vc) throws DatatypeException
  {
    return base.getValue (str, vc);
  }
}
