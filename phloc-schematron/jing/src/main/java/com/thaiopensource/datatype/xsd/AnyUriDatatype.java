package com.thaiopensource.datatype.xsd;

import com.thaiopensource.util.Uri;

class AnyUriDatatype extends TokenDatatype
{
  @Override
  public boolean lexicallyAllows (final String str)
  {
    return Uri.isValid (str);
  }

  @Override
  String getLexicalSpaceKey ()
  {
    return "uri";
  }

  @Override
  public boolean alwaysValid ()
  {
    return false;
  }
}
