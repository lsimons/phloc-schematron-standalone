package com.thaiopensource.datatype.xsd;

import com.thaiopensource.datatype.xsd.regex.Regex;
import com.thaiopensource.datatype.xsd.regex.RegexEngine;
import com.thaiopensource.datatype.xsd.regex.RegexSyntaxException;

class RegexDatatype extends TokenDatatype
{
  private final String pattern;
  private Regex regex;

  RegexDatatype (final String pattern)
  {
    this.pattern = pattern;
  }

  synchronized void compile (final RegexEngine engine) throws RegexSyntaxException
  {
    if (regex == null)
      regex = engine.compile (pattern);
  }

  @Override
  public boolean lexicallyAllows (final String str)
  {
    return regex.matches (str);
  }

  @Override
  public boolean alwaysValid ()
  {
    return false;
  }
}
