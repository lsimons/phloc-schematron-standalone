package com.thaiopensource.util;

public abstract class Utf16
{
  // 110110XX XXXXXX 110111XX XXXXXX
  static public boolean isSurrogate (final char c)
  {
    return (c & 0xF800) == 0xD800;
  }

  static public boolean isSurrogate (final int c)
  {
    return c >= 0 && c <= 0xFFFF && isSurrogate ((char) c);
  }

  static public boolean isSurrogate1 (final char c)
  {
    return (c & 0xFC00) == 0xD800;
  }

  static public boolean isSurrogate2 (final char c)
  {
    return (c & 0xFC00) == 0xDC00;
  }

  static public int scalarValue (final char c1, final char c2)
  {
    return (((c1 & 0x3FF) << 10) | (c2 & 0x3FF)) + 0x10000;
  }

  static public char surrogate1 (final int c)
  {
    return (char) (((c - 0x10000) >> 10) | 0xD800);
  }

  static public char surrogate2 (final int c)
  {
    return (char) (((c - 0x10000) & 0x3FF) | 0xDC00);
  }
}
