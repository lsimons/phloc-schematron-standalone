package com.thaiopensource.relaxng.pattern;

import org.relaxng.datatype.Datatype;

import com.thaiopensource.xml.util.Name;

class ValuePattern extends StringPattern
{
  private final Object obj;
  private final Datatype dt;
  private final Name dtName;
  private final String stringValue;

  ValuePattern (final Datatype dt, final Name dtName, final Object obj, final String stringValue)
  {
    super (combineHashCode (VALUE_HASH_CODE, dt.valueHashCode (obj)));
    this.dt = dt;
    this.dtName = dtName;
    this.obj = obj;
    this.stringValue = stringValue;
  }

  @Override
  boolean samePattern (final Pattern other)
  {
    if (getClass () != other.getClass ())
      return false;
    if (!(other instanceof ValuePattern))
      return false;
    return (dt.equals (((ValuePattern) other).dt) && dt.sameValue (obj, ((ValuePattern) other).obj));
  }

  @Override
  <T> T apply (final PatternFunction <T> f)
  {
    return f.caseValue (this);
  }

  @Override
  void checkRestrictions (final int context, final DuplicateAttributeDetector dad, final Alphabet alpha) throws RestrictionViolationException
  {
    switch (context)
    {
      case START_CONTEXT:
        throw new RestrictionViolationException ("start_contains_value");
    }
  }

  Datatype getDatatype ()
  {
    return dt;
  }

  Name getDatatypeName ()
  {
    return dtName;
  }

  Object getValue ()
  {
    return obj;
  }

  String getStringValue ()
  {
    return stringValue;
  }
}
