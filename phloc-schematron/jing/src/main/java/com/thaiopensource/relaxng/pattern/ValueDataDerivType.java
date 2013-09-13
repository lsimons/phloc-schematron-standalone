package com.thaiopensource.relaxng.pattern;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;

import com.thaiopensource.xml.util.Name;

/**
 * DataDerivType for a pattern which is a choice of values of the same datatype.
 */
class ValueDataDerivType extends DataDerivType
{
  private final Datatype dt;
  private final Name dtName;
  private PatternMemo noValue;
  private Map <DatatypeValue, PatternMemo> valueMap;

  ValueDataDerivType (final Datatype dt, final Name dtName)
  {
    this.dt = dt;
    this.dtName = dtName;
  }

  @Override
  DataDerivType copy ()
  {
    return new ValueDataDerivType (dt, dtName);
  }

  @Override
  PatternMemo dataDeriv (final ValidatorPatternBuilder builder,
                         final Pattern p,
                         final String str,
                         final ValidationContext vc,
                         final List <DataDerivFailure> fail)
  {
    final Object value = dt.createValue (str, vc);
    if (value == null)
    {
      if (noValue == null)
        noValue = super.dataDeriv (builder, p, str, vc, fail);
      else
        if (fail != null && noValue.isNotAllowed ())
        {
          try
          {
            dt.checkValid (str, vc);
          }
          catch (final DatatypeException e)
          {
            fail.add (new DataDerivFailure (dt, dtName, e));
          }
        }
      return noValue;
    }
    else
    {
      final DatatypeValue dtv = new DatatypeValue (value, dt);
      if (valueMap == null)
        valueMap = new HashMap <DatatypeValue, PatternMemo> ();
      PatternMemo tem = valueMap.get (dtv);
      if (tem == null)
      {
        tem = super.dataDeriv (builder, p, str, vc, fail);
        valueMap.put (dtv, tem);
      }
      else
        if (tem.isNotAllowed () && fail != null)
          super.dataDeriv (builder, p, str, vc, fail);
      return tem;
    }
  }

  @Override
  DataDerivType combine (final DataDerivType ddt)
  {
    if (ddt instanceof ValueDataDerivType)
    {
      if (((ValueDataDerivType) ddt).dt == this.dt)
        return this;
      else
        return InconsistentDataDerivType.getInstance ();
    }
    else
      return ddt.combine (this);
  }

  Datatype getDatatype ()
  {
    return dt;
  }
}
