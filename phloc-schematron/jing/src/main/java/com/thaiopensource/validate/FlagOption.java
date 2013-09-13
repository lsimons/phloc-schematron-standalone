package com.thaiopensource.validate;

public class FlagOption implements Option
{
  private final FlagPropertyId pid;

  public FlagOption (final FlagPropertyId pid)
  {
    this.pid = pid;
  }

  public FlagPropertyId getPropertyId ()
  {
    return pid;
  }

  public Flag valueOf (final String arg) throws OptionArgumentException
  {
    if (arg != null)
      throw new OptionArgumentPresenceException ();
    return Flag.PRESENT;
  }

  public Object combine (final Object [] values)
  {
    return null;
  }
}
