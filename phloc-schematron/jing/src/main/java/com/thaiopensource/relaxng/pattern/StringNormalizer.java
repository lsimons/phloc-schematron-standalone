package com.thaiopensource.relaxng.pattern;

import java.util.StringTokenizer;

class StringNormalizer
{
  static String normalize (final String s)
  {
    final StringBuilder buf = new StringBuilder ();
    for (final StringTokenizer e = new StringTokenizer (s); e.hasMoreElements ();)
    {
      if (buf.length () > 0)
        buf.append (' ');
      buf.append ((String) e.nextElement ());
    }
    return buf.toString ();
  }
}
