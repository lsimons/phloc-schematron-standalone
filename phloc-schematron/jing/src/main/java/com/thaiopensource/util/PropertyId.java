package com.thaiopensource.util;

public class PropertyId <T>
{
  private final String name;
  private final Class <T> valueClass;

  public static <T> PropertyId <T> newInstance (final String name, final Class <T> valueClass)
  {
    return new PropertyId <T> (name, valueClass);
  }

  protected PropertyId (final String name, final Class <T> valueClass)
  {
    if (name == null || valueClass == null)
      throw new NullPointerException ();
    this.name = name;
    this.valueClass = valueClass;
  }

  public Class <T> getValueClass ()
  {
    return valueClass;
  }

  @Override
  public final int hashCode ()
  {
    return super.hashCode ();
  }

  @Override
  public final boolean equals (final Object obj)
  {
    return super.equals (obj);
  }

  @Override
  public String toString ()
  {
    return name;
  }

  /**
   * @deprecated
   */
  @Deprecated
  public T get (final PropertyMap map)
  {
    return map.get (this);
  }

  /**
   * @deprecated
   */
  @Deprecated
  public T put (final PropertyMapBuilder builder, final T value)
  {
    return builder.put (this, value);
  }
}
