package com.thaiopensource.relaxng.pattern;

import java.util.List;

import org.relaxng.datatype.ValidationContext;

class BlankDataDerivType extends DataDerivType
{
  private PatternMemo blankMemo;
  private PatternMemo nonBlankMemo;

  BlankDataDerivType ()
  {}

  @Override
  PatternMemo dataDeriv (final ValidatorPatternBuilder builder,
                         final Pattern p,
                         final String str,
                         final ValidationContext vc,
                         final List <DataDerivFailure> fail)
  {
    if (DataDerivFunction.isBlank (str))
    {
      if (blankMemo == null || (fail != null && blankMemo.isNotAllowed ()))
        blankMemo = super.dataDeriv (builder, p, str, vc, fail);
      return blankMemo;
    }
    else
    {
      if (nonBlankMemo == null || (fail != null && nonBlankMemo.isNotAllowed ()))
        nonBlankMemo = super.dataDeriv (builder, p, str, vc, fail);
      return nonBlankMemo;
    }
  }

  @Override
  DataDerivType copy ()
  {
    return new BlankDataDerivType ();
  }

  @Override
  DataDerivType combine (final DataDerivType ddt)
  {
    if (ddt instanceof BlankDataDerivType || ddt instanceof SingleDataDerivType)
      return this;
    return InconsistentDataDerivType.getInstance ();
  }
}
