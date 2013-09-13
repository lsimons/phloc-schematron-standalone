package com.thaiopensource.relaxng.pattern;

class EndAttributesFunction extends AbstractPatternFunction <Pattern>
{
  private final ValidatorPatternBuilder builder;

  EndAttributesFunction (final ValidatorPatternBuilder builder)
  {
    this.builder = builder;
  }

  @Override
  public Pattern caseOther (final Pattern p)
  {
    return p;
  }

  @Override
  public Pattern caseGroup (final GroupPattern p)
  {
    final Pattern p1 = p.getOperand1 ();
    final Pattern p2 = p.getOperand2 ();
    final Pattern q1 = memoApply (p1);
    final Pattern q2 = memoApply (p2);
    if (p1 == q1 && p2 == q2)
      return p;
    return builder.makeGroup (q1, q2);
  }

  @Override
  public Pattern caseInterleave (final InterleavePattern p)
  {
    final Pattern p1 = p.getOperand1 ();
    final Pattern p2 = p.getOperand2 ();
    final Pattern q1 = memoApply (p1);
    final Pattern q2 = memoApply (p2);
    if (p1 == q1 && p2 == q2)
      return p;
    return builder.makeInterleave (q1, q2);
  }

  @Override
  public Pattern caseChoice (final ChoicePattern p)
  {
    final Pattern p1 = p.getOperand1 ();
    final Pattern p2 = p.getOperand2 ();
    final Pattern q1 = memoApply (p1);
    final Pattern q2 = memoApply (p2);
    if (p1 == q1 && p2 == q2)
      return p;
    return builder.makeChoice (q1, q2);
  }

  @Override
  public Pattern caseOneOrMore (final OneOrMorePattern p)
  {
    final Pattern p1 = p.getOperand ();
    final Pattern q1 = memoApply (p1);
    if (p1 == q1)
      return p;
    return builder.makeOneOrMore (q1);
  }

  @Override
  public Pattern caseAfter (final AfterPattern p)
  {
    final Pattern p1 = p.getOperand1 ();
    final Pattern q1 = memoApply (p1);
    if (p1 == q1)
      return p;
    return builder.makeAfter (q1, p.getOperand2 ());
  }

  @Override
  public Pattern caseAttribute (final AttributePattern p)
  {
    return builder.makeNotAllowed ();
  }

  final Pattern memoApply (final Pattern p)
  {
    return apply (builder.getPatternMemo (p)).getPattern ();
  }

  PatternMemo apply (final PatternMemo memo)
  {
    return memo.endAttributes (this);
  }

  ValidatorPatternBuilder getPatternBuilder ()
  {
    return builder;
  }
}
