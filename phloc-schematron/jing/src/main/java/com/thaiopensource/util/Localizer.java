package com.thaiopensource.util;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class Localizer
{
  private final Class <?> cls;
  private ResourceBundle bundle;

  public Localizer (final Class <?> cls)
  {
    this.cls = cls;
  }

  public String message (final String key)
  {
    return MessageFormat.format (getBundle ().getString (key), new Object [] {});
  }

  public String message (final String key, final Object arg)
  {
    return MessageFormat.format (getBundle ().getString (key), new Object [] { arg });
  }

  public String message (final String key, final Object arg1, final Object arg2)
  {
    return MessageFormat.format (getBundle ().getString (key), new Object [] { arg1, arg2 });
  }

  public String message (final String key, final Object [] args)
  {
    return MessageFormat.format (getBundle ().getString (key), args);
  }

  private ResourceBundle getBundle ()
  {
    if (bundle == null)
    {
      String s = cls.getName ();
      final int i = s.lastIndexOf ('.');
      if (i > 0)
        s = s.substring (0, i + 1);
      else
        s = "";
      bundle = ResourceBundle.getBundle (s + "resources.Messages");
    }
    return bundle;
  }
}
