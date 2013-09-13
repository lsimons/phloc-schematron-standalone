package com.thaiopensource.relaxng.pattern;

import org.relaxng.datatype.ValidationContext;

class TokenDatatype extends StringDatatype
{
  @Override
  public Object createValue (final String str, final ValidationContext vc)
  {
    return StringNormalizer.normalize (str);
  }
}
