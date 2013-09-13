package com.thaiopensource.validate;

import com.thaiopensource.util.PropertyId;
import com.thaiopensource.util.PropertyMap;
import com.thaiopensource.util.PropertyMapBuilder;

public abstract class AbstractSchema implements Schema
{
  private final PropertyMap properties;

  public AbstractSchema ()
  {
    this (PropertyMap.EMPTY);
  }

  public AbstractSchema (final PropertyMap properties)
  {
    this.properties = properties;
  }

  public AbstractSchema (final PropertyMap properties, final PropertyId <?> [] supportedPropertyIds)
  {
    this (filterProperties (properties, supportedPropertyIds));
  }

  public PropertyMap getProperties ()
  {
    return properties;
  }

  static public PropertyMap filterProperties (final PropertyMap properties, final PropertyId <?> [] supportedPropertyIds)
  {
    final PropertyMapBuilder builder = new PropertyMapBuilder ();
    for (final PropertyId <?> supportedPropertyId : supportedPropertyIds)
      copy (builder, supportedPropertyId, properties);
    return builder.toPropertyMap ();
  }

  static private <T> void copy (final PropertyMapBuilder builder, final PropertyId <T> pid, final PropertyMap properties)
  {
    final T value = properties.get (pid);
    if (value != null)
      builder.put (pid, value);
  }
}
