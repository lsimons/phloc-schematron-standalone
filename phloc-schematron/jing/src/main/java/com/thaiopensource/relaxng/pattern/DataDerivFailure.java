package com.thaiopensource.relaxng.pattern;

import java.util.List;

import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeException;

import com.thaiopensource.xml.util.Name;

/**
 * Provides information about why a DataDerivFunction returned notAllowed.
 */
final class DataDerivFailure
{
  private final Datatype datatype;
  private final Name datatypeName;
  private final List <String> datatypeParams;
  private final String message;
  private final String stringValue;
  private final Object value;
  // except non-null means it matched the except
  private final Pattern except;
  // index where error occurred if known
  private int index;
  private int tokenIndex = -1;
  private int tokenStart = -1;
  private int tokenEnd = -1;

  // not a valid instance of the datatype
  DataDerivFailure (final DataPattern p, final DatatypeException e)
  {
    this (p.getDatatype (), p.getDatatypeName (), p.getParams (), e.getMessage (), e.getIndex ());
  }

  // not a valid instance of the datatype
  DataDerivFailure (final Datatype dt, final Name dtName, final DatatypeException e)
  {
    this (dt, dtName, null, e.getMessage (), e.getIndex ());
  }

  // failed because it matched the except in a dataExcept
  DataDerivFailure (final DataExceptPattern p)
  {
    this (p.getDatatype (), p.getDatatypeName (), p.getParams (), p.getExcept ());
  }

  // not equal to the value in a value pattern
  DataDerivFailure (final ValuePattern p)
  {
    this (p.getDatatype (), p.getDatatypeName (), p.getValue (), p.getStringValue ());
  }

  private DataDerivFailure (final Datatype datatype,
                            final Name datatypeName,
                            final List <String> datatypeParams,
                            final String message,
                            final int index)
  {
    this.datatype = datatype;
    this.datatypeName = datatypeName;
    this.datatypeParams = datatypeParams;
    this.message = message;
    this.except = null;
    this.index = index == DatatypeException.UNKNOWN ? -1 : index;
    this.stringValue = null;
    this.value = null;
  }

  private DataDerivFailure (final Datatype datatype,
                            final Name datatypeName,
                            final List <String> datatypeParams,
                            final Pattern except)
  {
    this.datatype = datatype;
    this.datatypeName = datatypeName;
    this.datatypeParams = datatypeParams;
    this.message = null;
    this.except = except;
    this.index = -1;
    this.stringValue = null;
    this.value = null;
  }

  private DataDerivFailure (final Datatype datatype,
                            final Name datatypeName,
                            final Object value,
                            final String stringValue)
  {
    this.datatype = datatype;
    this.datatypeName = datatypeName;
    this.datatypeParams = null;
    this.message = null;
    this.except = null;
    this.index = -1;
    this.stringValue = stringValue;
    this.value = value;
  }

  @Override
  public boolean equals (final Object obj)
  {
    if (!(obj instanceof DataDerivFailure))
      return false;
    final DataDerivFailure other = (DataDerivFailure) obj;
    return (datatype == other.datatype &&
            equal (message, other.message) &&
            equal (stringValue, other.stringValue) &&
            except == other.except &&
            tokenIndex == other.tokenIndex && index == other.index);
  }

  @Override
  public int hashCode ()
  {
    int hc = datatype.hashCode ();
    if (message != null)
      hc ^= message.hashCode ();
    if (stringValue != null)
      hc ^= stringValue.hashCode ();
    if (except != null)
      hc ^= except.hashCode ();
    return hc;
  }

  private static boolean equal (final Object o1, final Object o2)
  {
    if (o1 == null)
      return o2 == null;
    return o1.equals (o2);
  }

  Datatype getDatatype ()
  {
    return datatype;
  }

  Name getDatatypeName ()
  {
    return datatypeName;
  }

  List <String> getDatatypeParams ()
  {
    return datatypeParams;
  }

  String getMessage ()
  {
    return message;
  }

  String getStringValue ()
  {
    return stringValue;
  }

  Object getValue ()
  {
    return value;
  }

  Pattern getExcept ()
  {
    return except;
  }

  int getIndex ()
  {
    return index;
  }

  int getTokenIndex ()
  {
    return tokenIndex;
  }

  int getTokenStart ()
  {
    return tokenStart;
  }

  int getTokenEnd ()
  {
    return tokenEnd;
  }

  void setToken (final int tokenIndex, final int tokenStart, final int tokenEnd)
  {
    this.tokenIndex = tokenIndex;
    this.tokenStart = tokenStart;
    this.tokenEnd = tokenEnd;
    if (index < 0)
      index += tokenStart;
  }

}
