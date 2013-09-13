package com.thaiopensource.util;

import java.util.HashMap;
import java.util.Map;

public class PropertyMapBuilder
{
  private Map <PropertyId <?>, Object> map;
  private PropertyId <?> [] keys;

  private static class PropertyMapImpl implements PropertyMap
  {
    private final Map <PropertyId <?>, Object> map;
    private final PropertyId <?> [] keys;

    private PropertyMapImpl (final Map <PropertyId <?>, Object> map, final PropertyId <?> [] keys)
    {
      this.map = map;
      this.keys = keys;
    }

    public <T> T get (final PropertyId <T> pid)
    {
      return pid.getValueClass ().cast (map.get (pid));
    }

    public int size ()
    {
      return keys.length;
    }

    public boolean contains (final PropertyId <?> pid)
    {
      return map.get (pid) != null;
    }

    public PropertyId <?> getKey (final int i)
    {
      return keys[i];
    }
  }

  public PropertyMapBuilder ()
  {
    this.map = new HashMap <PropertyId <?>, Object> ();
  }

  public PropertyMapBuilder (final PropertyMap pm)
  {
    if (pm instanceof PropertyMapImpl)
    {
      final PropertyMapImpl pmi = (PropertyMapImpl) pm;
      this.map = pmi.map;
      this.keys = pmi.keys;
    }
    else
    {
      this.map = new HashMap <PropertyId <?>, Object> ();
      add (pm);
    }
  }

  public void add (final PropertyMap pm)
  {
    for (int i = 0, len = pm.size (); i < len; i++)
      copy (pm.getKey (i), pm);
  }

  private <T> void copy (final PropertyId <T> pid, final PropertyMap pm)
  {
    put (pid, pm.get (pid));
  }

  private void lock ()
  {
    if (keys != null)
      return;
    keys = new PropertyId <?> [map.size ()];
    int i = 0;
    for (final PropertyId <?> propertyId : map.keySet ())
      keys[i++] = propertyId;
  }

  private void copyIfLocked ()
  {
    if (keys == null)
      return;
    final Map <PropertyId <?>, Object> newMap = new HashMap <PropertyId <?>, Object> ();
    for (final PropertyId <?> key : keys)
      newMap.put (key, map.get (key));
    map = newMap;
    keys = null;
  }

  public PropertyMap toPropertyMap ()
  {
    lock ();
    return new PropertyMapImpl (map, keys);
  }

  public <T> T put (final PropertyId <T> id, final T value)
  {
    copyIfLocked ();
    final Class <T> cls = id.getValueClass ();
    if (value == null)
      return cls.cast (map.remove (id));
    return cls.cast (map.put (id, cls.cast (value)));
  }

  public <T> T get (final PropertyId <T> pid)
  {
    return pid.getValueClass ().cast (map.get (pid));
  }

  public boolean contains (final PropertyId <?> pid)
  {
    return map.get (pid) != null;
  }
}
