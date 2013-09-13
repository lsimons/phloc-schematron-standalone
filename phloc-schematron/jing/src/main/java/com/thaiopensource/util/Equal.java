package com.thaiopensource.util;

public class Equal
{
  private Equal ()
  {}

  static public boolean equal (final Object obj1, final Object obj2)
  {
    return obj1 == null ? obj2 == null : obj1.equals (obj2);
  }
}
