package com.thaiopensource.xml.util;

import java.io.UnsupportedEncodingException;

public abstract class EncodingMap
{
  private static final String [] aliases = { "UTF-8",
                                            "UTF8",
                                            "UTF-16",
                                            "Unicode",
                                            "UTF-16BE",
                                            "UnicodeBigUnmarked",
                                            "UTF-16LE",
                                            "UnicodeLittleUnmarked",
                                            "US-ASCII",
                                            "ASCII",
                                            "TIS-620",
                                            "TIS620" };

  static public String getJavaName (final String enc)
  {
    try
    {
      "x".getBytes (enc);
    }
    catch (final UnsupportedEncodingException e)
    {
      for (int i = 0; i < aliases.length; i += 2)
      {
        if (enc.equalsIgnoreCase (aliases[i]))
        {
          try
          {
            "x".getBytes (aliases[i + 1]);
            return aliases[i + 1];
          }
          catch (final UnsupportedEncodingException e2)
          {}
        }
      }
    }
    return enc;
  }

  static public void main (final String [] args)
  {
    System.err.println (getJavaName (args[0]));
  }
}
