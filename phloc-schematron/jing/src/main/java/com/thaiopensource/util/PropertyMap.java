package com.thaiopensource.util;

public interface PropertyMap
{
  public static final PropertyMap EMPTY = new PropertyMap ()
  {
    public <T> T get (final PropertyId <T> pid)
    {
      return null;
    }

    public boolean contains (final PropertyId <?> pid)
    {
      return false;
    }

    public int size ()
    {
      return 0;
    }

    public PropertyId <?> getKey (final int i)
    {
      throw new IndexOutOfBoundsException ();
    }
  };

  <T> T get (PropertyId <T> pid);

  boolean contains (PropertyId <?> pid);

  int size ();

  PropertyId <?> getKey (int i);
}
