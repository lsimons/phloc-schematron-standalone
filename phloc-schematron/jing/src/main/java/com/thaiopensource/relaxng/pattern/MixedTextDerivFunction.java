package com.thaiopensource.relaxng.pattern;

class MixedTextDerivFunction extends EndAttributesFunction
{

  MixedTextDerivFunction (final ValidatorPatternBuilder builder)
  {
    super (builder);
  }

  @Override
  public Pattern caseText (final TextPattern p)
  {
    return p;
  }

  @Override
  public Pattern caseGroup (final GroupPattern p)
  {
    final Pattern p1 = p.getOperand1 ();
    final Pattern p2 = p.getOperand2 ();
    final Pattern q1 = memoApply (p1);
    final Pattern tem = (q1 == p1) ? p : getPatternBuilder ().makeGroup (q1, p2);
    if (!p1.isNullable ())
      return tem;
    return getPatternBuilder ().makeChoice (tem, memoApply (p2));
  }

  @Override
  public Pattern caseInterleave (final InterleavePattern p)
  {
    final Pattern p1 = p.getOperand1 ();
    final Pattern p2 = p.getOperand2 ();
    final Pattern q1 = memoApply (p1);
    final Pattern q2 = memoApply (p2);
    final Pattern i1 = (q1 == p1) ? p : getPatternBuilder ().makeInterleave (q1, p2);
    final Pattern i2 = (q2 == p2) ? p : getPatternBuilder ().makeInterleave (p1, q2);
    return getPatternBuilder ().makeChoice (i1, i2);
  }

  @Override
  public Pattern caseOneOrMore (final OneOrMorePattern p)
  {
    return getPatternBuilder ().makeGroup (memoApply (p.getOperand ()), getPatternBuilder ().makeOptional (p));
  }

  @Override
  public Pattern caseOther (final Pattern p)
  {
    return getPatternBuilder ().makeNotAllowed ();
  }

  @Override
  PatternMemo apply (final PatternMemo memo)
  {
    return memo.mixedTextDeriv (this);
  }
}
