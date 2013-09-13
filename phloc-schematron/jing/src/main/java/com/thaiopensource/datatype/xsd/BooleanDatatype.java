package com.thaiopensource.datatype.xsd;

import org.relaxng.datatype.ValidationContext;

class BooleanDatatype extends DatatypeBase
{
  @Override
  boolean lexicallyAllows (final String str)
  {
    return str.equals ("true") || str.equals ("false") || str.equals ("1") || str.equals ("0");
  }

  @Override
  Object getValue (final String str, final ValidationContext vc)
  {
    switch (str.charAt (0))
    {
      case 't':
      case '1':
        return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }

  @Override
  String getLexicalSpaceKey ()
  {
    return "boolean";
  }
}
