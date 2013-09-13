package com.thaiopensource.validate.nvdl;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Utility class, stores a set of objects. It uses a Hashtable for internal
 * storage.
 */
class Hashset
{
  /**
   * The internal storage, a hashtable.
   */
  private final Hashtable <Object, Object> table = new Hashtable <Object, Object> ();

  /**
   * Test if an object belongs to this set or not.
   * 
   * @param key
   *        The object.
   * @return true if the object is contained in this set.
   */
  boolean contains (final Object key)
  {
    return table.get (key) != null;
  }

  /**
   * Adds an object to this set.
   * 
   * @param key
   *        The object to be added.
   */
  void add (final Object key)
  {
    table.put (key, key);
  }

  /**
   * Adds all the objects from another set to this set - union.
   * 
   * @param set
   *        The other set.
   */
  void addAll (final Hashset set)
  {
    for (final Enumeration <Object> e = set.table.keys (); e.hasMoreElements ();)
      add (e.nextElement ());
  }

  /**
   * Removes all the objects from this set.
   */
  void clear ()
  {
    table.clear ();
  }

  /**
   * Get an enumeration will all the objects from this set.
   * 
   * @return an enumeration with all the objects from this set.
   */
  Enumeration <Object> members ()
  {
    return table.keys ();
  }
}
