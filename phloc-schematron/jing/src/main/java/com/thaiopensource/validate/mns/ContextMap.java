package com.thaiopensource.validate.mns;

import java.util.Hashtable;
import java.util.Vector;

import com.thaiopensource.xml.util.Name;

class ContextMap
{
  private Object rootValue;
  private Object otherValue;
  private final Hashtable nameTable = new Hashtable ();

  Object get (final Vector context)
  {
    return get (context, context.size ());
  }

  boolean put (final boolean isRoot, final Vector names, final Object value)
  {
    return put (isRoot, names, names.size (), value);
  }

  private Object get (final Vector context, final int len)
  {
    if (len > 0)
    {
      final ContextMap nestedMap = (ContextMap) nameTable.get (context.elementAt (len - 1));
      if (nestedMap != null)
      {
        final Object value = nestedMap.get (context, len - 1);
        if (value != null)
          return value;
      }
    }
    if (rootValue != null && len == 0)
      return rootValue;
    return otherValue;
  }

  private boolean put (final boolean isRoot, final Vector names, final int len, final Object value)
  {
    if (len == 0)
    {
      if (isRoot)
      {
        if (rootValue != null)
          return false;
        rootValue = value;
      }
      else
      {
        if (otherValue != null)
          return false;
        otherValue = value;
      }
      return true;
    }
    else
    {
      final Name name = (Name) names.elementAt (len - 1);
      ContextMap nestedMap = (ContextMap) nameTable.get (name);
      if (nestedMap == null)
      {
        nestedMap = new ContextMap ();
        nameTable.put (name, nestedMap);
      }
      return nestedMap.put (isRoot, names, len - 1, value);
    }
  }
}
