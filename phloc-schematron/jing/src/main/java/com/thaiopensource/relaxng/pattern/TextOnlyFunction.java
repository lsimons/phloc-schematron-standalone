package com.thaiopensource.relaxng.pattern;

class TextOnlyFunction extends EndAttributesFunction
{
  TextOnlyFunction (final ValidatorPatternBuilder builder)
  {
    super (builder);
  }

  @Override
  public Pattern caseAttribute (final AttributePattern p)
  {
    return p;
  }

  @Override
  public Pattern caseElement (final ElementPattern p)
  {
    return getPatternBuilder ().makeNotAllowed ();
  }

  @Override
  PatternMemo apply (final PatternMemo memo)
  {
    return memo.textOnly (this);
  }

}
