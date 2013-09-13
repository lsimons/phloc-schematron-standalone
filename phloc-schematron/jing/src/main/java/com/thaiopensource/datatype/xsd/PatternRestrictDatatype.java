package com.thaiopensource.datatype.xsd;

import org.relaxng.datatype.DatatypeException;

import com.thaiopensource.datatype.xsd.regex.Regex;

class PatternRestrictDatatype extends RestrictDatatype
{
  private final Regex pattern;
  private final String patternString;

  PatternRestrictDatatype (final DatatypeBase base, final Regex pattern, final String patternString)
  {
    super (base);
    this.pattern = pattern;
    this.patternString = patternString;
  }

  @Override
  boolean lexicallyAllows (final String str)
  {
    return pattern.matches (str) && super.lexicallyAllows (str);
  }

  @Override
  void checkLexicallyAllows (final String str) throws DatatypeException
  {
    super.checkLexicallyAllows (str);
    if (!pattern.matches (str))
      throw new DatatypeException (localizer ().message ("pattern_violation",
                                                         getDescriptionForRestriction (),
                                                         patternString));
  }
}
