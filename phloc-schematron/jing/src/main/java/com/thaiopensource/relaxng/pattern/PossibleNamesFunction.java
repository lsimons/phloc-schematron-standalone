package com.thaiopensource.relaxng.pattern;

import com.thaiopensource.util.VoidValue;

/**
 * Common base class for PossibleAttributeNamesFunction and
 * PossibleStartTagNamesFunction.
 * 
 * @see PossibleAttributeNamesFunction
 * @see PossibleStartTagNamesFunction
 */
abstract class PossibleNamesFunction extends AbstractPatternFunction <VoidValue>
{
  private final UnionNameClassNormalizer normalizer = new UnionNameClassNormalizer ();

  NormalizedNameClass applyTo (final Pattern p)
  {
    normalizer.setNameClass (new NullNameClass ());
    p.apply (this);
    return normalizer.normalize ();
  }

  void add (final NameClass nc)
  {
    normalizer.add (nc);
  }

  @Override
  public VoidValue caseAfter (final AfterPattern p)
  {
    return p.getOperand1 ().apply (this);
  }

  public VoidValue caseBinary (final BinaryPattern p)
  {
    p.getOperand1 ().apply (this);
    p.getOperand2 ().apply (this);
    return VoidValue.VOID;
  }

  @Override
  public VoidValue caseChoice (final ChoicePattern p)
  {
    return caseBinary (p);
  }

  @Override
  public VoidValue caseInterleave (final InterleavePattern p)
  {
    return caseBinary (p);
  }

  @Override
  public VoidValue caseOneOrMore (final OneOrMorePattern p)
  {
    return p.getOperand ().apply (this);
  }

  @Override
  public VoidValue caseOther (final Pattern p)
  {
    return VoidValue.VOID;
  }
}
