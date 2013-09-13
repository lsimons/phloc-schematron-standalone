package com.thaiopensource.relaxng.pattern;

import java.util.Collections;
import java.util.List;

import org.relaxng.datatype.Datatype;

import com.thaiopensource.datatype.Datatype2;
import com.thaiopensource.xml.util.Name;

class DataPattern extends StringPattern
{
  private final Datatype dt;
  private final Name dtName;
  private final List <String> params;

  DataPattern (final Datatype dt, final Name dtName, final List <String> params)
  {
    super (combineHashCode (DATA_HASH_CODE, dt.hashCode ()));
    this.dt = dt;
    this.dtName = dtName;
    this.params = params;
  }

  @Override
  boolean samePattern (final Pattern other)
  {
    if (other.getClass () != this.getClass ())
      return false;
    return dt.equals (((DataPattern) other).dt);
  }

  @Override
  <T> T apply (final PatternFunction <T> f)
  {
    return f.caseData (this);
  }

  Datatype getDatatype ()
  {
    return dt;
  }

  Name getDatatypeName ()
  {
    return dtName;
  }

  List <String> getParams ()
  {
    return Collections.unmodifiableList (params);
  }

  boolean allowsAnyString ()
  {
    return dt instanceof Datatype2 && ((Datatype2) dt).alwaysValid ();
  }

  @Override
  void checkRestrictions (final int context, final DuplicateAttributeDetector dad, final Alphabet alpha) throws RestrictionViolationException
  {
    switch (context)
    {
      case START_CONTEXT:
        throw new RestrictionViolationException ("start_contains_data");
    }
  }
}
