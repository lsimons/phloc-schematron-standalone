package com.thaiopensource.util;

public class SinglePropertyMap <T> implements PropertyMap
{
  private final PropertyId <T> pid;
  private final T value;

  private SinglePropertyMap (final PropertyId <T> pid, final T value)
  {
    if (!(pid.getValueClass ().isInstance (value)))
    {
      if (value == null)
        throw new NullPointerException ();
      throw new ClassCastException ();
    }
    this.pid = pid;
    this.value = value;
  }

  public <V> V get (final PropertyId <V> pid)
  {
    if (pid != this.pid)
      return null;
    // it would be nice to avoid the cast,
    // but I can't figure out how to do it
    return pid.getValueClass ().cast (value);
  }

  public boolean contains (final PropertyId <?> pid)
  {
    return pid == this.pid;
  }

  public int size ()
  {
    return 1;
  }

  public PropertyId <?> getKey (final int i)
  {
    if (i != 0)
      throw new IndexOutOfBoundsException ();
    return pid;
  }

  public static <T> SinglePropertyMap newInstance (final PropertyId <T> pid, final T value)
  {
    return new SinglePropertyMap <T> (pid, value);
  }
}
