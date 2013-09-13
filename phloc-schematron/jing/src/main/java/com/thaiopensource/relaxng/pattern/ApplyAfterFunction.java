package com.thaiopensource.relaxng.pattern;

abstract class ApplyAfterFunction extends AbstractPatternFunction <Pattern>
{
  private final ValidatorPatternBuilder builder;

  ApplyAfterFunction (final ValidatorPatternBuilder builder)
  {
    this.builder = builder;
  }

  @Override
  public Pattern caseAfter (final AfterPattern p)
  {
    return builder.makeAfter (p.getOperand1 (), apply (p.getOperand2 ()));
  }

  @Override
  public Pattern caseChoice (final ChoicePattern p)
  {
    return builder.makeChoice (p.getOperand1 ().apply (this), p.getOperand2 ().apply (this));
  }

  @Override
  public Pattern caseNotAllowed (final NotAllowedPattern p)
  {
    return p;
  }

  @Override
  public Pattern caseOther (final Pattern p)
  {
    throw new AssertionError ("ApplyAfterFunction applied to " + p.getClass ().getName ());
  }

  abstract Pattern apply (Pattern p);
}
