package com.thaiopensource.relaxng.pattern;

class DataDerivTypeFunction extends AbstractPatternFunction <DataDerivType>
{
  private final ValidatorPatternBuilder builder;

  DataDerivTypeFunction (final ValidatorPatternBuilder builder)
  {
    this.builder = builder;
  }

  static DataDerivType dataDerivType (final ValidatorPatternBuilder builder, final Pattern pattern)
  {
    return pattern.apply (builder.getDataDerivTypeFunction ());
  }

  @Override
  public DataDerivType caseOther (final Pattern p)
  {
    return new SingleDataDerivType ();
  }

  @Override
  public DataDerivType caseRef (final RefPattern p)
  {
    return apply (p.getPattern ());
  }

  @Override
  public DataDerivType caseAfter (final AfterPattern p)
  {
    final Pattern p1 = p.getOperand1 ();
    final DataDerivType ddt = apply (p.getOperand1 ());
    if (!p1.isNullable ())
      return ddt;
    return ddt.combine (new BlankDataDerivType ());
  }

  private DataDerivType caseBinary (final BinaryPattern p)
  {
    return apply (p.getOperand1 ()).combine (apply (p.getOperand2 ()));
  }

  @Override
  public DataDerivType caseChoice (final ChoicePattern p)
  {
    return caseBinary (p);
  }

  @Override
  public DataDerivType caseGroup (final GroupPattern p)
  {
    return caseBinary (p);
  }

  @Override
  public DataDerivType caseInterleave (final InterleavePattern p)
  {
    return caseBinary (p);
  }

  @Override
  public DataDerivType caseOneOrMore (final OneOrMorePattern p)
  {
    return apply (p.getOperand ());
  }

  @Override
  public DataDerivType caseList (final ListPattern p)
  {
    return InconsistentDataDerivType.getInstance ();
  }

  @Override
  public DataDerivType caseValue (final ValuePattern p)
  {
    return new ValueDataDerivType (p.getDatatype (), p.getDatatypeName ());
  }

  @Override
  public DataDerivType caseData (final DataPattern p)
  {
    if (p.allowsAnyString ())
      return new SingleDataDerivType ();
    return new DataDataDerivType (p);
  }

  @Override
  public DataDerivType caseDataExcept (final DataExceptPattern p)
  {
    if (p.allowsAnyString ())
      return apply (p.getExcept ());
    return new DataDataDerivType (p).combine (apply (p.getExcept ()));
  }

  private DataDerivType apply (final Pattern p)
  {
    return builder.getPatternMemo (p).dataDerivType ();
  }
}
