package com.thaiopensource.relaxng.pattern;

class RecoverAfterFunction extends AbstractPatternFunction <Pattern>
{
  private final ValidatorPatternBuilder builder;

  RecoverAfterFunction (final ValidatorPatternBuilder builder)
  {
    this.builder = builder;
  }

  @Override
  public Pattern caseOther (final Pattern p)
  {
    throw new RuntimeException ("recover after botch");
  }

  @Override
  public Pattern caseChoice (final ChoicePattern p)
  {
    return builder.makeChoice (p.getOperand1 ().apply (this), p.getOperand2 ().apply (this));
  }

  @Override
  public Pattern caseAfter (final AfterPattern p)
  {
    return p.getOperand2 ();
  }
}
