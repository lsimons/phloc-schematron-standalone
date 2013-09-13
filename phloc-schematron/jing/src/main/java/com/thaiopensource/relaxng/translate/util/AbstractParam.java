package com.thaiopensource.relaxng.translate.util;

public class AbstractParam implements Param
{

  public boolean allowRepeat ()
  {
    return false;
  }

  public void set (final String value) throws InvalidParamValueException, ParamValuePresenceException
  {
    throw new ParamValuePresenceException ();
  }

  public void set (final boolean value) throws InvalidParamValueException, ParamValuePresenceException
  {
    throw new ParamValuePresenceException ();
  }
}
