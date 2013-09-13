package com.thaiopensource.datatype.xsd.regex.xerces2;

import org.apache.xerces.impl.xpath.regex.ParseException;
import org.apache.xerces.impl.xpath.regex.RegularExpression;

import com.thaiopensource.datatype.xsd.regex.Regex;
import com.thaiopensource.datatype.xsd.regex.RegexEngine;
import com.thaiopensource.datatype.xsd.regex.RegexSyntaxException;

/**
 * An implementation of <code>RegexEngine</code> using the Xerces 2 regular
 * expression implementation.
 */
public class RegexEngineImpl implements RegexEngine
{
  public RegexEngineImpl ()
  {
    // Force a linkage error on instantiation if the Xerces classes
    // are not available.
    try
    {
      new RegularExpression ("", "X");
    }
    catch (final ParseException e)
    {}
  }

  public Regex compile (final String expr) throws RegexSyntaxException
  {
    try
    {
      final RegularExpression re = new RegularExpression (expr, "X");
      return new Regex ()
      {
        public boolean matches (final String str)
        {
          return re.matches (str);
        }
      };
    }
    catch (final ParseException e)
    {
      throw new RegexSyntaxException (e.getMessage (), e.getLocation ());
    }
  }
}
