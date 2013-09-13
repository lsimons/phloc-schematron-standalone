package com.thaiopensource.datatype.xsd;

import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;

import com.thaiopensource.util.Utf16;

class TokenDatatype extends DatatypeBase implements Measure
{

  TokenDatatype ()
  {}

  TokenDatatype (final int whiteSpace)
  {
    super (whiteSpace);
  }

  @Override
  public boolean lexicallyAllows (final String str)
  {
    return true;
  }

  @Override
  String getLexicalSpaceKey ()
  {
    return "string";
  }

  @Override
  public boolean alwaysValid ()
  {
    return true;
  }

  @Override
  Object getValue (final String str, final ValidationContext vc) throws DatatypeException
  {
    return str;
  }

  @Override
  Measure getMeasure ()
  {
    return this;
  }

  public int getLength (final Object obj)
  {
    final String str = (String) obj;
    final int len = str.length ();
    int nSurrogatePairs = 0;
    for (int i = 0; i < len; i++)
      if (Utf16.isSurrogate1 (str.charAt (i)))
        nSurrogatePairs++;
    return len - nSurrogatePairs;
  }
}
