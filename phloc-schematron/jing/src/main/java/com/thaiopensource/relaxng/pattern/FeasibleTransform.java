package com.thaiopensource.relaxng.pattern;

import java.util.HashSet;
import java.util.Set;

public class FeasibleTransform
{
  private static class FeasiblePatternFunction extends AbstractPatternFunction <Pattern>
  {
    private final SchemaPatternBuilder spb;
    private final Set <ElementPattern> elementDone = new HashSet <ElementPattern> ();

    FeasiblePatternFunction (final SchemaPatternBuilder spb)
    {
      this.spb = spb;
    }

    @Override
    public Pattern caseChoice (final ChoicePattern p)
    {
      return spb.makeChoice (p.getOperand1 ().apply (this), p.getOperand2 ().apply (this));
    }

    @Override
    public Pattern caseGroup (final GroupPattern p)
    {
      return spb.makeGroup (p.getOperand1 ().apply (this), p.getOperand2 ().apply (this));
    }

    @Override
    public Pattern caseInterleave (final InterleavePattern p)
    {
      return spb.makeInterleave (p.getOperand1 ().apply (this), p.getOperand2 ().apply (this));
    }

    @Override
    public Pattern caseOneOrMore (final OneOrMorePattern p)
    {
      return spb.makeOneOrMore (p.getOperand ().apply (this));
    }

    @Override
    public Pattern caseElement (final ElementPattern p)
    {
      if (!elementDone.contains (p))
      {
        elementDone.add (p);
        p.setContent (p.getContent ().apply (this));
      }
      return spb.makeOptional (p);
    }

    @Override
    public Pattern caseOther (final Pattern p)
    {
      return spb.makeOptional (p);
    }
  }

  public static Pattern transform (final SchemaPatternBuilder spb, final Pattern p)
  {
    return p.apply (new FeasiblePatternFunction (spb));
  }
}
