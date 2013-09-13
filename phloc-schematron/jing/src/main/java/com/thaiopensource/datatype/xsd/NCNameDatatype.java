package com.thaiopensource.datatype.xsd;

import com.thaiopensource.xml.util.Naming;

class NCNameDatatype extends NameDatatype
{
  @Override
  public boolean lexicallyAllows (final String str)
  {
    return Naming.isNcname (str);
  }

  @Override
  String getLexicalSpaceKey ()
  {
    return "ncname";
  }
}
