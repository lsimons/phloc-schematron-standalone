package com.thaiopensource.validate;

public class StringOption implements Option
{
  private final StringPropertyId pid;

  public StringOption (final StringPropertyId pid)
  {
    this.pid = pid;
  }

  public StringPropertyId getPropertyId ()
  {
    return pid;
  }

  public String valueOf (final String arg) throws OptionArgumentException
  {
    if (arg == null)
      return defaultValue ();
    return normalize (arg);
  }

  public String defaultValue () throws OptionArgumentPresenceException
  {
    throw new OptionArgumentPresenceException ();
  }

  public String normalize (final String value) throws OptionArgumentFormatException
  {
    return value;
  }

  public Object combine (final Object [] values)
  {
    return null;
  }
}
