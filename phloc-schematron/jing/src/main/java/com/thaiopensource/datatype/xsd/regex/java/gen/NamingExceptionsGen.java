package com.thaiopensource.datatype.xsd.regex.java.gen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.Vector;

import com.thaiopensource.xml.util.Naming;

public class NamingExceptionsGen
{
  static public void main (final String [] args) throws IOException
  {
    if (args.length != 2)
    {
      System.err.println ("Usage: " + NamingExceptionsGen.class.toString () + " className srcDir");
      System.exit (1);
    }
    String className = args[0];
    String srcDir = args[1];
    final int lastDot = className.lastIndexOf ('.');
    String pkg;
    if (lastDot < 0)
      pkg = null;
    else
    {
      pkg = className.substring (0, lastDot);
      className = className.substring (lastDot + 1);
      srcDir = srcDir + File.separator + pkg.replace ('.', File.separatorChar);
    }
    final String srcFile = srcDir + File.separator + className + ".java";
    final OutputStream stm = new FileOutputStream (srcFile);
    final Writer w = new BufferedWriter (new OutputStreamWriter (stm));
    final String lineSep = System.getProperty ("line.separator");
    w.write ("// This file was automatically generated by ");
    w.write (NamingExceptionsGen.class.getName ());
    w.write (lineSep);
    if (pkg != null)
      w.write ("package " + pkg + ";" + lineSep + lineSep);
    w.write ("class " + className + " {" + lineSep);
    gen (true, w, lineSep);
    gen (false, w, lineSep);
    w.write ("}" + lineSep);
    w.close ();
  }

  static private void gen (final boolean isStart, final Writer w, final String lineSep) throws IOException
  {
    final char [] buf = new char [1];
    boolean excluding = false;
    char excludeMin = 0;
    char excludeMax = 0;
    final List <Character> includeList = new Vector <Character> ();
    final List <Character> excludeRangeList = new Vector <Character> ();
    for (int i = 0; i < 65536; i++)
    {
      final char ch = (char) i;
      buf[0] = ch;
      final String s = new String (buf);
      final boolean isName = isStart ? Naming.isName (s) : Naming.isNmtoken (s);
      if (isName && excluding)
      {
        excludeRangeList.add (new Character (excludeMin));
        excludeRangeList.add (new Character (excludeMax));
        excluding = false;
      }
      if (isName != isApproxName (ch, isStart))
      {
        if (isName)
          includeList.add (new Character (ch));
        else
        {
          if (!excluding)
          {
            excluding = true;
            excludeMin = ch;
          }
          excludeMax = ch;
        }
      }
    }
    if (excluding)
    {
      excludeRangeList.add (new Character (excludeMin));
      excludeRangeList.add (new Character (excludeMax));
    }
    final String prefix = isStart ? "NMSTRT" : "NMCHAR";
    genList (prefix + "_INCLUDES", includeList, w, lineSep);
    genList (prefix + "_EXCLUDE_RANGES", excludeRangeList, w, lineSep);
    w.write (INDENT);
    w.write ("static final String ");
    w.write (prefix);
    w.write ("_CATEGORIES = \"");
    w.write (isStart ? NMSTRT_CATEGORIES : NMCHAR_CATEGORIES);
    w.write ("\";");
    w.write (lineSep);
  }

  static private final int CHARS_PER_LINE = 10;
  static private final String INDENT = "  ";

  static private void genList (final String varName, final List <Character> includeList, final Writer w, final String lineSep) throws IOException
  {
    w.write (INDENT);
    w.write ("static final String ");
    w.write (varName);
    w.write (" =");
    w.write (lineSep);
    w.write (INDENT);
    w.write (INDENT);
    w.write ('"');
    for (int i = 0, len = includeList.size (); i < len; i++)
    {
      w.write ("\\u");
      w.write (hex (includeList.get (i).charValue ()));
      if (i % CHARS_PER_LINE == CHARS_PER_LINE - 1 && i + 1 != len)
      {
        w.write ("\" +");
        w.write (lineSep);
        w.write (INDENT);
        w.write (INDENT);
        w.write ('"');
      }
    }
    w.write ("\";");
    w.write (lineSep);
  }

  static private final String HEX_DIGITS = "0123456789ABCDEF";

  static String hex (final char c)
  {
    final char [] buf = new char [4];
    buf[0] = HEX_DIGITS.charAt ((c >> 12) & 0xF);
    buf[1] = HEX_DIGITS.charAt ((c >> 8) & 0xF);
    buf[2] = HEX_DIGITS.charAt ((c >> 4) & 0xF);
    buf[3] = HEX_DIGITS.charAt (c & 0xF);
    return new String (buf);
  }

  static private final String NMSTRT_CATEGORIES = "LlLuLoLtNl";
  static private final String NMCHAR_CATEGORIES = NMSTRT_CATEGORIES + "McMeMnLmNd";

  static private boolean isApproxName (final char c, final boolean isStart)
  {
    switch (Character.getType (c))
    {
      case Character.LOWERCASE_LETTER: // Ll
      case Character.UPPERCASE_LETTER: // Lu
      case Character.OTHER_LETTER: // Lo
      case Character.TITLECASE_LETTER: // Lt
      case Character.LETTER_NUMBER: // Nl
        return true;
      case Character.COMBINING_SPACING_MARK: // Mc
      case Character.ENCLOSING_MARK: // Me
      case Character.NON_SPACING_MARK: // Mn
      case Character.MODIFIER_LETTER: // Lm
      case Character.DECIMAL_DIGIT_NUMBER: // Nd
        return !isStart;
    }
    return false;
  }
}
