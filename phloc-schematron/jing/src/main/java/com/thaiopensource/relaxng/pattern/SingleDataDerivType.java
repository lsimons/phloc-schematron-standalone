package com.thaiopensource.relaxng.pattern;

import java.util.List;

import org.relaxng.datatype.ValidationContext;

/**
 * DerivType for a Pattern whose derivative wrt any data is always the same.
 */
class SingleDataDerivType extends DataDerivType
{
  private PatternMemo memo;

  SingleDataDerivType ()
  {}

  @Override
  PatternMemo dataDeriv (final ValidatorPatternBuilder builder,
                         final Pattern p,
                         final String str,
                         final ValidationContext vc,
                         final List <DataDerivFailure> fail)
  {
    if (memo == null)
      // this type never adds any failures
      memo = super.dataDeriv (builder, p, str, vc, null);
    return memo;
  }

  @Override
  DataDerivType copy ()
  {
    return new SingleDataDerivType ();
  }

  @Override
  DataDerivType combine (final DataDerivType ddt)
  {
    return ddt;
  }
}
