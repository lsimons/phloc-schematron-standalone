package com.thaiopensource.relaxng.pattern;

import com.thaiopensource.xml.util.Name;

class StartTagOpenDerivFunction extends AbstractPatternFunction <Pattern>
{
  private final Name name;
  private final ValidatorPatternBuilder builder;

  StartTagOpenDerivFunction (final Name name, final ValidatorPatternBuilder builder)
  {
    this.name = name;
    this.builder = builder;
  }

  @Override
  public Pattern caseChoice (final ChoicePattern p)
  {
    return builder.makeChoice (memoApply (p.getOperand1 ()), memoApply (p.getOperand2 ()));
  }

  @Override
  public Pattern caseGroup (final GroupPattern p)
  {
    final Pattern p1 = p.getOperand1 ();
    final Pattern p2 = p.getOperand2 ();
    final Pattern tem = memoApply (p1).apply (new ApplyAfterFunction (builder)
    {
      @Override
      Pattern apply (final Pattern x)
      {
        return builder.makeGroup (x, p2);
      }
    });
    return p1.isNullable () ? builder.makeChoice (tem, memoApply (p2)) : tem;
  }

  @Override
  public Pattern caseInterleave (final InterleavePattern p)
  {
    final Pattern p1 = p.getOperand1 ();
    final Pattern p2 = p.getOperand2 ();
    return builder.makeChoice (memoApply (p1).apply (new ApplyAfterFunction (builder)
    {
      @Override
      Pattern apply (final Pattern x)
      {
        return builder.makeInterleave (x, p2);
      }
    }), memoApply (p2).apply (new ApplyAfterFunction (builder)
    {
      @Override
      Pattern apply (final Pattern x)
      {
        return builder.makeInterleave (p1, x);
      }
    }));
  }

  @Override
  public Pattern caseAfter (final AfterPattern p)
  {
    final Pattern p1 = p.getOperand1 ();
    final Pattern p2 = p.getOperand2 ();
    return memoApply (p1).apply (new ApplyAfterFunction (builder)
    {
      @Override
      Pattern apply (final Pattern x)
      {
        return builder.makeAfter (x, p2);
      }
    });
  }

  @Override
  public Pattern caseOneOrMore (final OneOrMorePattern p)
  {
    final Pattern p1 = p.getOperand ();
    return memoApply (p1).apply (new ApplyAfterFunction (builder)
    {
      @Override
      Pattern apply (final Pattern x)
      {
        return builder.makeGroup (x, builder.makeOptional (p));
      }
    });
  }

  @Override
  public Pattern caseElement (final ElementPattern p)
  {
    if (!p.getNameClass ().contains (name))
      return builder.makeNotAllowed ();
    return builder.makeAfter (p.getContent (), builder.makeEmpty ());
  }

  @Override
  public Pattern caseOther (final Pattern p)
  {
    return builder.makeNotAllowed ();
  }

  final Pattern memoApply (final Pattern p)
  {
    return apply (builder.getPatternMemo (p)).getPattern ();
  }

  PatternMemo apply (final PatternMemo memo)
  {
    return memo.startTagOpenDeriv (this);
  }

  Name getName ()
  {
    return name;
  }

  ValidatorPatternBuilder getPatternBuilder ()
  {
    return builder;
  }
}
