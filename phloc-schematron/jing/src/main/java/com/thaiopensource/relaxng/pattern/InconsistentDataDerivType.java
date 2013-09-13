package com.thaiopensource.relaxng.pattern;

class InconsistentDataDerivType extends DataDerivType
{
  static private final InconsistentDataDerivType instance = new InconsistentDataDerivType ();

  static InconsistentDataDerivType getInstance ()
  {
    return instance;
  }

  private InconsistentDataDerivType ()
  {}

  @Override
  DataDerivType combine (final DataDerivType ddt)
  {
    return this;
  }

  @Override
  DataDerivType copy ()
  {
    return this;
  }
}
