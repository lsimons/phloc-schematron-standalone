package com.thaiopensource.util;

import java.io.UnsupportedEncodingException;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 */
public class Utf8Test
{
  @Test
  public void testEncode () throws UnsupportedEncodingException
  {
    for (int i = 0; i < 0x10FFFF; i++)
    {
      if (Utf16.isSurrogate (i))
        continue;
      char [] chars;
      if (i <= 0xFFFF)
        chars = new char [] { (char) i };
      else
        chars = new char [] { Utf16.surrogate1 (i), Utf16.surrogate2 (i) };
      Assert.assertEquals (Utf8.encode (i), new String (chars).getBytes ("UTF-8"));
    }
  }

}
