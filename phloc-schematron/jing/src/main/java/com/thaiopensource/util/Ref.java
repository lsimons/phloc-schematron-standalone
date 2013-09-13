package com.thaiopensource.util;

/**
 * Simple generic class to hold a reference to an object.
 */
public class Ref <T>
{
  private T obj;

  public Ref ()
  {}

  public Ref (final T obj)
  {
    this.obj = obj;
  }

  public T get ()
  {
    return obj;
  }

  public void set (final T obj)
  {
    this.obj = obj;
  }

  public void clear ()
  {
    this.obj = null;
  }
}
