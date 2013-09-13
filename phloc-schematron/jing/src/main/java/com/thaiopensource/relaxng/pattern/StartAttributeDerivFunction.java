package com.thaiopensource.relaxng.pattern;

import com.thaiopensource.xml.util.Name;

class StartAttributeDerivFunction extends StartTagOpenDerivFunction
{
  StartAttributeDerivFunction (final Name name, final ValidatorPatternBuilder builder)
  {
    super (name, builder);
  }

  @Override
  public Pattern caseElement (final ElementPattern p)
  {
    return getPatternBuilder ().makeNotAllowed ();
  }

  @Override
  public Pattern caseGroup (final GroupPattern p)
  {
    final Pattern p1 = p.getOperand1 ();
    final Pattern p2 = p.getOperand2 ();
    return getPatternBuilder ().makeChoice (memoApply (p1).apply (new ApplyAfterFunction (getPatternBuilder ())
    {
      @Override
      Pattern apply (final Pattern x)
      {
        return getPatternBuilder ().makeGroup (x, p2);
      }
    }), memoApply (p2).apply (new ApplyAfterFunction (getPatternBuilder ())
    {
      @Override
      Pattern apply (final Pattern x)
      {
        return getPatternBuilder ().makeGroup (p1, x);
      }
    }));
  }

  @Override
  public Pattern caseAttribute (final AttributePattern p)
  {
    if (!p.getNameClass ().contains (getName ()))
      return getPatternBuilder ().makeNotAllowed ();
    return getPatternBuilder ().makeAfter (p.getContent (), getPatternBuilder ().makeEmpty ());
  }

  @Override
  PatternMemo apply (final PatternMemo memo)
  {
    return memo.startAttributeDeriv (this);
  }
}
