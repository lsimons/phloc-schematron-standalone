package com.thaiopensource.relaxng.pattern;

import java.util.List;

import org.relaxng.datatype.ValidationContext;

abstract class DataDerivType
{
  abstract DataDerivType copy ();

  abstract DataDerivType combine (DataDerivType ddt);

  PatternMemo dataDeriv (final ValidatorPatternBuilder builder,
                         final Pattern p,
                         final String str,
                         final ValidationContext vc,
                         final List <DataDerivFailure> fail)
  {
    return builder.getPatternMemo (p.apply (new DataDerivFunction (str, vc, builder, fail)));
  }
}
