package com.thaiopensource.validate.nrl;

import java.util.Enumeration;
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

  void addAll (final Hashset set)
  {
    for (final Enumeration <Object> e = set.table.keys (); e.hasMoreElements ();)
      add (e.nextElement ());
  }

  void clear ()
  {
    table.clear ();
  }

  Enumeration <Object> members ()
  {
    return table.keys ();
  }
}
