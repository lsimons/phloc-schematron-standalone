package com.thaiopensource.datatype.xsd;

import com.thaiopensource.xml.util.Naming;

class NameDatatype extends TokenDatatype
{
  @Override
  public boolean lexicallyAllows (final String str)
  {
    return Naming.isName (str);
  }

  @Override
  public int getLength (final Object obj)
  {
    // Surrogates are not possible in an Name.
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
    return "name";
  }
}
