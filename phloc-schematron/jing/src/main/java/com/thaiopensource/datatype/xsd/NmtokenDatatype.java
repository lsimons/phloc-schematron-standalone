package com.thaiopensource.datatype.xsd;

import com.thaiopensource.xml.util.Naming;

class NmtokenDatatype extends TokenDatatype
{
  @Override
  public boolean lexicallyAllows (final String str)
  {
    return Naming.isNmtoken (str);
  }

  @Override
  public int getLength (final Object obj)
  {
    // Surrogates are not possible in an NMTOKEN.
    return ((String) obj).length ();
  }

  @Override
  public boolean alwaysValid ()
  {
    return false;
  }

  @Override
  String getLexicalSpaceKey ()
  {
    return "nmtoken";
  }
}
