package com.thaiopensource.datatype.xsd.regex.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.thaiopensource.datatype.xsd.regex.Regex;
import com.thaiopensource.datatype.xsd.regex.RegexEngine;
import com.thaiopensource.datatype.xsd.regex.RegexSyntaxException;
import com.thaiopensource.util.Utf16;

public class CategoryTest
{
  static private final String categories = "LMNPZSC";
  static private final String subCategories = "LuLlLtLmLoMnMcMeNdNlNoPcPdPsPePiPfPoZsZlZpSmScSkSoCcCfCoCn";

  private final Regex [] categoryPosRegexes = new Regex [categories.length ()];
  private final Regex [] categoryNegRegexes = new Regex [categories.length ()];
  private final Regex [] subCategoryPosRegexes = new Regex [subCategories.length () / 2];
  private final Regex [] subCategoryNegRegexes = new Regex [subCategories.length () / 2];

  static public void main (final String [] args) throws IOException,
                                                RegexSyntaxException,
                                                ClassNotFoundException,
                                                IllegalAccessException,
                                                InstantiationException
  {
    if (args.length != 2)
    {
      System.err.println ("usage: " + CategoryTest.class.getName () + " engineClass UnicodeData");
      System.exit (2);
    }
    final BufferedReader r = new BufferedReader (new InputStreamReader (new FileInputStream (args[1])));
    final Class <?> cls = CategoryTest.class.getClassLoader ().loadClass (args[0]);
    final RegexEngine engine = (RegexEngine) cls.newInstance ();
    final int nFail = new CategoryTest (engine).testAll (r);
    System.err.println (nFail + " tests failed");
    System.exit (nFail > 0 ? 1 : 0);
  }

  CategoryTest (final RegexEngine engine) throws RegexSyntaxException
  {
    for (int i = 0, len = categories.length (); i < len; i++)
    {
      final String ch = categories.substring (i, i + 1);
      categoryPosRegexes[i] = engine.compile ("\\p{" + ch + "}");
      categoryNegRegexes[i] = engine.compile ("\\P{" + ch + "}");
    }
    for (int i = 0, len = subCategories.length (); i < len; i += 2)
    {
      final String name = subCategories.substring (i, i + 2);
      subCategoryPosRegexes[i / 2] = engine.compile ("\\p{" + name + "}");
      subCategoryNegRegexes[i / 2] = engine.compile ("\\P{" + name + "}");
    }
  }

  int testAll (final BufferedReader r) throws IOException
  {
    int lastCode = -1;
    for (;;)
    {
      final String line = r.readLine ();
      if (line == null)
        break;
      final int semi = line.indexOf (';');
      if (semi < 0)
        continue;
      final int code = Integer.parseInt (line.substring (0, semi), 16);
      final int semi2 = line.indexOf (';', semi + 1);
      final String name = line.substring (semi, semi2);
      final String category = line.substring (semi2 + 1, semi2 + 3);
      if (lastCode + 1 != code)
      {
        final String missingCategory = name.endsWith (", Last>") ? category : "Cn";
        for (int i = lastCode + 1; i < code; i++)
          test (i, missingCategory);
      }
      test (code, category);
      lastCode = code;
    }
    for (++lastCode; lastCode < 0x110000; lastCode++)
      test (lastCode, "Cn");
    return nFail;
  }

  void test (final int ch, final String category)
  {
    if (!isXmlChar (ch))
      return;
    if (subCategories.indexOf (category) < 0)
    {
      System.err.println ("Missing category: " + category);
      System.exit (2);
    }
    for (int i = 0, len = categories.length (); i < len; i++)
      check (ch,
             categoryPosRegexes[i],
             categoryNegRegexes[i],
             category.charAt (0) == categories.charAt (i),
             categories.substring (i, i + 1));
    for (int i = 0, len = subCategories.length (); i < len; i += 2)
      check (ch,
             subCategoryPosRegexes[i / 2],
             subCategoryNegRegexes[i / 2],
             category.equals (subCategories.substring (i, i + 2)),
             subCategories.substring (i, i + 2));
  }

  void check (final int ch, final Regex pos, final Regex neg, final boolean inPos, final String cat)
  {
    String str;
    if (ch > 0xFFFF)
      str = new String (new char [] { Utf16.surrogate1 (ch), Utf16.surrogate2 (ch) });
    else
      str = new String (new char [] { (char) ch });
    if (pos.matches (str) != inPos)
      fail (ch, cat);
    if (neg.matches (str) != !inPos)
      fail (ch, "-" + cat);
  }

  int nFail = 0;

  void fail (final int ch, final String cat)
  {
    nFail++;
    System.err.println ("Failed: " + Integer.toHexString (ch) + "/" + cat);
  }

  static boolean isXmlChar (final int code)
  {
    switch (code)
    {
      case '\r':
      case '\n':
      case '\t':
        return true;
      case 0xFFFE:
      case 0xFFFF:
        return false;
      default:
        if (code < 0x20)
          return false;
        if (code >= 0xD800 && code < 0xE000)
          return false;
        return true;
    }
  }
}
