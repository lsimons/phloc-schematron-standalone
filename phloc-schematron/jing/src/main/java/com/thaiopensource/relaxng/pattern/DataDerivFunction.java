package com.thaiopensource.relaxng.pattern;

import java.util.List;

import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;

// invariant: if return is not notAllowed, then no failures are added to fail
class DataDerivFunction extends AbstractPatternFunction <Pattern>
{
  private final ValidatorPatternBuilder builder;
  private final ValidationContext vc;
  private final String str;
  private final List <DataDerivFailure> fail;

  DataDerivFunction (final String str,
                     final ValidationContext vc,
                     final ValidatorPatternBuilder builder,
                     final List <DataDerivFailure> fail)
  {
    this.str = str;
    this.vc = vc;
    this.builder = builder;
    this.fail = fail;
  }

  static boolean isBlank (final String str)
  {
    final int len = str.length ();
    for (int i = 0; i < len; i++)
    {
      switch (str.charAt (i))
      {
        case '\r':
        case '\n':
        case ' ':
        case '\t':
          break;
        default:
          return false;
      }
    }
    return true;
  }

  @Override
  public Pattern caseText (final TextPattern p)
  {
    return p;
  }

  @Override
  public Pattern caseRef (final RefPattern p)
  {
    return memoApply (p.getPattern ());
  }

  @Override
  public Pattern caseList (final ListPattern p)
  {
    final int len = str.length ();
    int tokenIndex = 0;
    int tokenStart = -1;
    PatternMemo memo = builder.getPatternMemo (p.getOperand ());
    for (int i = 0; i < len; i++)
    {
      switch (str.charAt (i))
      {
        case '\r':
        case '\n':
        case ' ':
        case '\t':
          if (tokenStart >= 0)
          {
            memo = tokenDeriv (memo, tokenIndex++, tokenStart, i);
            tokenStart = -1;
          }
          break;
        default:
          if (tokenStart < 0)
            tokenStart = i;
          break;
      }
    }
    if (tokenStart >= 0)
      memo = tokenDeriv (memo, tokenIndex++, tokenStart, len);
    if (memo.getPattern ().isNullable ())
      return builder.makeEmpty ();
    if (memo.isNotAllowed ())
      return memo.getPattern ();
    // pseudo-token to try and force some failures
    tokenDeriv (memo, tokenIndex, len, len);
    // XXX handle the case where this didn't produce any failures
    return builder.makeNotAllowed ();
  }

  private PatternMemo tokenDeriv (final PatternMemo p, final int tokenIndex, final int start, final int end)
  {
    final int failStartSize = failSize ();
    final PatternMemo deriv = p.dataDeriv (str.substring (start, end), vc, fail);
    if (fail != null && deriv.isNotAllowed ())
    {
      for (int i = fail.size () - 1; i >= failStartSize; --i)
        fail.get (i).setToken (tokenIndex, start, end);
    }
    return deriv;
  }

  @Override
  public Pattern caseValue (final ValuePattern p)
  {
    final Datatype dt = p.getDatatype ();
    final Object value = dt.createValue (str, vc);
    if (value != null && dt.sameValue (p.getValue (), value))
      return builder.makeEmpty ();
    if (fail != null)
    {
      if (value == null)
      {
        try
        {
          dt.checkValid (str, vc);
        }
        catch (final DatatypeException e)
        {
          fail.add (new DataDerivFailure (dt, p.getDatatypeName (), e));
        }
      }
      else
        fail.add (new DataDerivFailure (p));
    }
    return builder.makeNotAllowed ();
  }

  @Override
  public Pattern caseData (final DataPattern p)
  {
    if (p.allowsAnyString ())
      return builder.makeEmpty ();
    if (fail != null)
    {
      try
      {
        p.getDatatype ().checkValid (str, vc);
        return builder.makeEmpty ();
      }
      catch (final DatatypeException e)
      {
        fail.add (new DataDerivFailure (p, e));
        return builder.makeNotAllowed ();
      }
    }
    if (p.getDatatype ().isValid (str, vc))
      return builder.makeEmpty ();
    else
      return builder.makeNotAllowed ();
  }

  @Override
  public Pattern caseDataExcept (final DataExceptPattern p)
  {
    final Pattern tem = caseData (p);
    if (tem.isNullable () && memoApply (p.getExcept ()).isNullable ())
    {
      if (fail != null)
        fail.add (new DataDerivFailure (p));
      return builder.makeNotAllowed ();
    }
    return tem;
  }

  @Override
  public Pattern caseAfter (final AfterPattern p)
  {
    final Pattern p1 = p.getOperand1 ();
    final int failStartSize = failSize ();
    if (memoApplyWithFailure (p1).isNullable ())
      return p.getOperand2 ();
    if (p1.isNullable () && isBlank (str))
    {
      clearFailures (failStartSize);
      return p.getOperand2 ();
    }
    return builder.makeNotAllowed ();
  }

  @Override
  public Pattern caseChoice (final ChoicePattern p)
  {
    final int failStartSize = failSize ();
    final Pattern tem = builder.makeChoice (memoApplyWithFailure (p.getOperand1 ()),
                                            memoApplyWithFailure (p.getOperand2 ()));
    if (!tem.isNotAllowed ())
      clearFailures (failStartSize);
    return tem;
  }

  @Override
  public Pattern caseGroup (final GroupPattern p)
  {
    final int failStartSize = failSize ();
    final Pattern p1 = p.getOperand1 ();
    final Pattern p2 = p.getOperand2 ();
    Pattern tem = builder.makeGroup (memoApplyWithFailure (p1), p2);
    if (p1.isNullable ())
      tem = builder.makeChoice (tem, memoApplyWithFailure (p2));
    if (!tem.isNotAllowed ())
      clearFailures (failStartSize);
    return tem;
  }

  // list//interleave is prohibited, so I don't think this can happen
  @Override
  public Pattern caseInterleave (final InterleavePattern p)
  {
    final Pattern p1 = p.getOperand1 ();
    final Pattern p2 = p.getOperand2 ();
    return builder.makeChoice (builder.makeInterleave (memoApply (p1), p2), builder.makeInterleave (p1, memoApply (p2)));
  }

  @Override
  public Pattern caseOneOrMore (final OneOrMorePattern p)
  {
    return builder.makeGroup (memoApplyWithFailure (p.getOperand ()), builder.makeOptional (p));
  }

  @Override
  public Pattern caseOther (final Pattern p)
  {
    return builder.makeNotAllowed ();
  }

  private Pattern memoApply (final Pattern p)
  {
    return builder.getPatternMemo (p).dataDeriv (str, vc).getPattern ();
  }

  private Pattern memoApplyWithFailure (final Pattern p)
  {
    return builder.getPatternMemo (p).dataDeriv (str, vc, fail).getPattern ();
  }

  private int failSize ()
  {
    return fail == null ? 0 : fail.size ();
  }

  private void clearFailures (final int failStartSize)
  {
    if (fail != null)
    {
      for (int i = fail.size () - 1; i >= failStartSize; --i)
        fail.remove (i);
    }
  }
}
