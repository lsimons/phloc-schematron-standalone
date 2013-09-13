package com.thaiopensource.relaxng.pattern;

class IgnoreMissingAttributesFunction extends EndAttributesFunction
{
  IgnoreMissingAttributesFunction (final ValidatorPatternBuilder builder)
  {
    super (builder);
  }

  @Override
  public Pattern caseAttribute (final AttributePattern p)
  {
    return getPatternBuilder ().makeEmpty ();
  }

  @Override
  PatternMemo apply (final PatternMemo memo)
  {
    return memo.ignoreMissingAttributes (this);
  }
}
