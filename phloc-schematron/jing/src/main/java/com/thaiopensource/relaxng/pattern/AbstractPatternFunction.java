package com.thaiopensource.relaxng.pattern;

abstract class AbstractPatternFunction <T> implements PatternFunction <T>
{
  public T caseEmpty (final EmptyPattern p)
  {
    return caseOther (p);
  }

  public T caseNotAllowed (final NotAllowedPattern p)
  {
    return caseOther (p);
  }

  public T caseError (final ErrorPattern p)
  {
    return caseOther (p);
  }

  public T caseGroup (final GroupPattern p)
  {
    return caseOther (p);
  }

  public T caseInterleave (final InterleavePattern p)
  {
    return caseOther (p);
  }

  public T caseChoice (final ChoicePattern p)
  {
    return caseOther (p);
  }

  public T caseOneOrMore (final OneOrMorePattern p)
  {
    return caseOther (p);
  }

  public T caseElement (final ElementPattern p)
  {
    return caseOther (p);
  }

  public T caseAttribute (final AttributePattern p)
  {
    return caseOther (p);
  }

  public T caseData (final DataPattern p)
  {
    return caseOther (p);
  }

  public T caseDataExcept (final DataExceptPattern p)
  {
    return caseOther (p);
  }

  public T caseValue (final ValuePattern p)
  {
    return caseOther (p);
  }

  public T caseText (final TextPattern p)
  {
    return caseOther (p);
  }

  public T caseList (final ListPattern p)
  {
    return caseOther (p);
  }

  public T caseAfter (final AfterPattern p)
  {
    return caseOther (p);
  }

  public T caseRef (final RefPattern p)
  {
    return caseOther (p);
  }

  public abstract T caseOther (Pattern p);
}
