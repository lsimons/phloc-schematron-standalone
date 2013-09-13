package com.thaiopensource.relaxng.pattern;

import java.util.List;

import org.relaxng.datatype.Datatype;
import org.xml.sax.Locator;

import com.thaiopensource.xml.util.Name;

class DataExceptPattern extends DataPattern
{
  private final Pattern except;
  private final Locator loc;

  DataExceptPattern (final Datatype dt,
                     final Name dtName,
                     final List <String> params,
                     final Pattern except,
                     final Locator loc)
  {
    super (dt, dtName, params);
    this.except = except;
    this.loc = loc;
  }

  @Override
  boolean samePattern (final Pattern other)
  {
    if (!super.samePattern (other))
      return false;
    return except.samePattern (((DataExceptPattern) other).except);
  }

  @Override
  <T> T apply (final PatternFunction <T> f)
  {
    return f.caseDataExcept (this);
  }

  @Override
  void checkRestrictions (final int context, final DuplicateAttributeDetector dad, final Alphabet alpha) throws RestrictionViolationException
  {
    super.checkRestrictions (context, dad, alpha);
    try
    {
      except.checkRestrictions (DATA_EXCEPT_CONTEXT, null, null);
    }
    catch (final RestrictionViolationException e)
    {
      e.maybeSetLocator (loc);
      throw e;
    }
  }

  Pattern getExcept ()
  {
    return except;
  }
}
