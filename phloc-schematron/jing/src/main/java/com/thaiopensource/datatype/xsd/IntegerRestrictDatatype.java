package com.thaiopensource.datatype.xsd;

import org.relaxng.datatype.DatatypeException;

class IntegerRestrictDatatype extends ScaleRestrictDatatype
{
  IntegerRestrictDatatype (final DatatypeBase base)
  {
    super (base, 0);
  }

  @Override
  boolean lexicallyAllows (final String str)
  {
    return super.lexicallyAllows (str) && str.charAt (str.length () - 1) != '.';
  }

  @Override
  void checkLexicallyAllows (final String str) throws DatatypeException
  {
    if (!lexicallyAllows (str))
      throw createLexicallyInvalidException ();
  }

  @Override
  String getLexicalSpaceKey ()
  {
    return "integer";
  }
}
