package com.thaiopensource.validate.mns;

import java.util.Hashtable;

class Hashset
{
  private final Hashtable <Object, Object> table = new Hashtable <Object, Object> ();

  boolean contains (final Object key)
  {
    return table.get (key) != null;
  }

  void add (final Object key)
  {
    table.put (key, key);
  }

  void clear ()
  {
    table.clear ();
  }
}
