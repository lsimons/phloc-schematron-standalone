package com.thaiopensource.relaxng.pattern;

import java.util.HashSet;
import java.util.Set;

import com.thaiopensource.util.VoidValue;
import com.thaiopensource.xml.util.Name;

class FindElementFunction extends AbstractPatternFunction <VoidValue>
{
  private final ValidatorPatternBuilder builder;
  private final Name name;
  private final Set <Pattern> processed = new HashSet <Pattern> ();
  private int specificity = NameClass.SPECIFICITY_NONE;
  private Pattern pattern = null;

  static public Pattern findElement (final ValidatorPatternBuilder builder, final Name name, final Pattern start)
  {
    final FindElementFunction f = new FindElementFunction (builder, name);
    start.apply (f);
    if (f.pattern == null)
      return builder.makeNotAllowed ();
    return f.pattern;
  }

  private FindElementFunction (final ValidatorPatternBuilder builder, final Name name)
  {
    this.builder = builder;
    this.name = name;
  }

  private boolean haveProcessed (final Pattern p)
  {
    if (processed.contains (p))
      return true;
    processed.add (p);
    return false;
  }

  private VoidValue caseBinary (final BinaryPattern p)
  {
    if (!haveProcessed (p))
    {
      p.getOperand1 ().apply (this);
      p.getOperand2 ().apply (this);
    }
    return VoidValue.VOID;

  }

  @Override
  public VoidValue caseGroup (final GroupPattern p)
  {
    return caseBinary (p);
  }

  @Override
  public VoidValue caseInterleave (final InterleavePattern p)
  {
    return caseBinary (p);
  }

  @Override
  public VoidValue caseChoice (final ChoicePattern p)
  {
    return caseBinary (p);
  }

  @Override
  public VoidValue caseOneOrMore (final OneOrMorePattern p)
  {
    if (!haveProcessed (p))
      p.getOperand ().apply (this);
    return VoidValue.VOID;
  }

  @Override
  public VoidValue caseElement (final ElementPattern p)
  {
    if (!haveProcessed (p))
    {
      final int s = p.getNameClass ().containsSpecificity (name);
      if (s > specificity)
      {
        specificity = s;
        pattern = p.getContent ();
      }
      else
        if (s == specificity && s != NameClass.SPECIFICITY_NONE)
          pattern = builder.makeChoice (pattern, p.getContent ());
      p.getContent ().apply (this);
    }
    return VoidValue.VOID;
  }

  @Override
  public VoidValue caseOther (final Pattern p)
  {
    return VoidValue.VOID;
  }
}
