package com.thaiopensource.relaxng.pattern;

class EndTagDerivFunction extends AbstractPatternFunction <Pattern>
{
  private final ValidatorPatternBuilder builder;

  EndTagDerivFunction (final ValidatorPatternBuilder builder)
  {
    this.builder = builder;
  }

  @Override
  public Pattern caseOther (final Pattern p)
  {
    return builder.makeNotAllowed ();
  }

  @Override
  public Pattern caseChoice (final ChoicePattern p)
  {
    return builder.makeChoice (memoApply (p.getOperand1 ()), memoApply (p.getOperand2 ()));
  }

  @Override
  public Pattern caseAfter (final AfterPattern p)
  {
    if (p.getOperand1 ().isNullable ())
      return p.getOperand2 ();
    else
      return builder.makeNotAllowed ();
  }

  private Pattern memoApply (final Pattern p)
  {
    return apply (builder.getPatternMemo (p)).getPattern ();
  }

  private PatternMemo apply (final PatternMemo memo)
  {
    return memo.endTagDeriv (this);
  }
}
