package com.thaiopensource.relaxng.pattern;

import com.thaiopensource.util.VoidValue;

/**
 * PatternFunction to compute the name class of possible attributes. Computes a
 * NormalizedNameClass.
 */
class PossibleAttributeNamesFunction extends PossibleNamesFunction
{
  @Override
  public VoidValue caseAttribute (final AttributePattern p)
  {
    add (p.getNameClass ());
    return VoidValue.VOID;
  }

  @Override
  public VoidValue caseGroup (final GroupPattern p)
  {
    return caseBinary (p);
  }
}
