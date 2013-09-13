package com.thaiopensource.relaxng.pattern;

import com.thaiopensource.xml.util.Name;

class StartTagOpenRecoverDerivFunction extends StartTagOpenDerivFunction
{
  StartTagOpenRecoverDerivFunction (final Name name, final ValidatorPatternBuilder builder)
  {
    super (name, builder);
  }

  @Override
  public Pattern caseGroup (final GroupPattern p)
  {
    final Pattern tem = super.caseGroup (p);
    if (p.getOperand1 ().isNullable ())
      return tem;
    return getPatternBuilder ().makeChoice (tem, memoApply (p.getOperand2 ()));
  }

  @Override
  PatternMemo apply (final PatternMemo memo)
  {
    return memo.startTagOpenRecoverDeriv (this);
  }
}
