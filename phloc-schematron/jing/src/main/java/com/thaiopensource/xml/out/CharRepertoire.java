package com.thaiopensource.xml.out;

import java.io.UnsupportedEncodingException;

public class CharRepertoire
{
  private final byte [] allUnknown = new byte [256];
  private final byte [][] charTable = new byte [256] [];

  private static final byte UNKNOWN = 0;
  private static final byte IN = 1;
  private static final byte OUT = -1;

  private final String enc;

  CharRepertoire (final String enc)
  {
    this.enc = enc;
    for (int i = 0; i < charTable.length; i++)
      charTable[i] = allUnknown;
  }

  public static CharRepertoire getInstance (final String enc) throws UnsupportedEncodingException
  {
    "x".getBytes (enc); // check whether encoding supported
    return new CharRepertoire (enc);
  }

  public final boolean contains (final char c)
  {
    final byte b = charTable[c >> 8][c & 0xFF];
    return b == 0 ? contains1 (c) : b > 0;
  }

  private boolean contains1 (final char c)
  {
    final int i = c >> 8;
    if (charTable[i] == allUnknown)
      charTable[i] = new byte [256];
    if (contains2 (c))
    {
      charTable[i][c & 0xFF] = IN;
      return true;
    }
    else
    {
      charTable[i][c & 0xFF] = OUT;
      return false;
    }
  }

  private boolean contains2 (final char c)
  {
    try
    {
      final String s = new String (new String (new char [] { c }).getBytes (enc), enc);
      return s.length () == 1 && s.charAt (0) == c;
    }
    catch (final UnsupportedEncodingException e)
    {
      return false;
    }
  }

  /* For surrogates. */
  public final boolean contains (final char c1, final char c2)
  {
    try
    {
      final String s = new String (new String (new char [] { c1, c2 }).getBytes (enc), enc);
      return s.length () == 2 && s.charAt (0) == c1 && s.charAt (1) == c2;
    }
    catch (final UnsupportedEncodingException e)
    {
      return false;
    }
    // work around gcj bug (libgcj/9802)
    catch (final RuntimeException e)
    {
      return false;
    }
  }

}
