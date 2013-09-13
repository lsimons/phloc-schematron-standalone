package com.thaiopensource.xml.out;

import java.io.CharConversionException;
import java.io.IOException;
import java.io.Writer;

import com.thaiopensource.util.Utf16;

public class XmlWriter
{
  static final private String indentString = "  ";
  private final Writer writer;
  private final CharRepertoire cr;

  private static final int OTHER = 0; // must be at beginning of line
  private static final int IN_START_TAG = 1;
  private static final int AFTER_DATA = 2;
  private int state = OTHER;

  private String [] stack = new String [20];
  private int level = 0;
  private String newline = "\n";

  public XmlWriter (final Writer writer, final CharRepertoire cr)
  {
    this.writer = writer;
    this.cr = cr;
  }

  public void setNewline (final String newline)
  {
    this.newline = newline;
  }

  public void close () throws IOException
  {
    writer.close ();
  }

  public void flush () throws IOException
  {
    writer.flush ();
  }

  public void writeXmlDecl (final String enc) throws IOException
  {
    writer.write ("<?xml version=\"1.0\" encoding=\"");
    writer.write (enc);
    writer.write ("\"?>");
    writer.write (newline);
  }

  public void startElement (final String name) throws IOException
  {
    switch (state)
    {
      case IN_START_TAG:
        writer.write ('>');
        writer.write (newline);
        indent ();
        break;
      case OTHER:
        indent ();
        // fall through
      case AFTER_DATA:
        state = IN_START_TAG;
        break;
    }
    writer.write ('<');
    outputMarkup (name);
    push (name);
  }

  public void endElement () throws IOException
  {
    final String name = pop ();
    switch (state)
    {
      case IN_START_TAG:
        writer.write ("/>");
        break;
      case OTHER:
        indent ();
        // fall through
      case AFTER_DATA:
        writer.write ("</");
        outputMarkup (name);
        writer.write ('>');
        break;
    }
    writer.write (newline);
    state = OTHER;
  }

  public void attribute (final String name, final String value) throws IOException
  {
    if (state != IN_START_TAG)
      throw new IllegalStateException ();
    writer.write (' ');
    outputMarkup (name);
    writer.write ('=');
    writer.write ('"');
    outputData (value, true, false);
    writer.write ('"');
  }

  public void characters (final String str) throws IOException
  {
    characters (str, false);
  }

  public void characters (final String str, final boolean useCharRef) throws IOException
  {
    if (state == IN_START_TAG)
      writer.write ('>');
    state = AFTER_DATA;
    outputData (str, false, useCharRef);
  }

  public void comment (final String str) throws IOException
  {
    if (state == IN_START_TAG)
    {
      writer.write ('>');
      state = OTHER;
      writer.write (newline);
    }
    writer.write ("<!--");
    outputMarkup (str);
    writer.write ("-->");
    if (state != AFTER_DATA)
      writer.write (newline);
  }

  public void processingInstruction (final String target, final String str) throws IOException
  {
    if (state == IN_START_TAG)
    {
      writer.write ('>');
      state = OTHER;
      writer.write (newline);
    }
    writer.write ("<?");
    outputMarkup (target);
    if (str.length () != 0)
    {
      writer.write (' ');
      outputMarkup (str);
    }
    writer.write ("?>");
    if (state != AFTER_DATA)
      writer.write (newline);
  }

  private void outputMarkup (final String str) throws IOException
  {
    final int len = str.length ();
    for (int i = 0; i < len; i++)
    {
      final char c = str.charAt (i);
      if (Utf16.isSurrogate1 (c))
      {
        if (i == len || !Utf16.isSurrogate2 (str.charAt (i)))
          throw new CharConversionException ("surrogate pair integrity failure");
        if (!cr.contains (c, str.charAt (i)))
          throw new CharConversionException ();
      }
      else
        if (!cr.contains (c))
          throw new CharConversionException ();
    }
    outputLines (str);
  }

  private void outputLines (String str) throws IOException
  {
    while (str.length () > 0)
    {
      final int i = str.indexOf ('\n');
      if (i < 0)
      {
        writer.write (str);
        break;
      }
      if (i > 0)
        writer.write (str.substring (0, i));
      writer.write (newline);
      str = str.substring (i + 1);
    }
  }

  private void outputData (final String str, final boolean inAttribute, final boolean useCharRef) throws IOException
  {
    final int len = str.length ();
    for (int i = 0; i < len; i++)
    {
      final char c = str.charAt (i);
      switch (c)
      {
        case '<':
          writer.write ("&lt;");
          break;
        case '>':
          writer.write ("&gt;");
          break;
        case '&':
          writer.write ("&amp;");
          break;
        case '"':
          writer.write ("&quot;");
          break;
        case '\r':
          writer.write ("&#xD;");
          break;
        case '\t':
          if (inAttribute)
            writer.write ("&#x9;");
          else
            writer.write ('\t');
          break;
        case '\n':
          if (inAttribute)
            writer.write ("&#xA;");
          else
            writer.write (newline);
          break;
        default:
          if (Utf16.isSurrogate1 (c))
          {
            ++i;
            if (i < len)
            {
              final char c2 = str.charAt (i);
              if (Utf16.isSurrogate2 (c))
              {
                charRef (Utf16.scalarValue (c, c2));
                break;
              }
            }
            throw new CharConversionException ("surrogate pair integrity failure");
          }
          if (cr.contains (c) && !useCharRef)
            writer.write (c);
          else
            charRef (c);
          break;
      }
    }
  }

  private void charRef (final int c) throws IOException
  {
    writer.write ("&#x");
    final int nDigits = c > 0xFFFF ? 6 : 4;
    for (int i = 0; i < nDigits; i++)
      writer.write ("0123456789ABCDEF".charAt ((c >> (4 * (nDigits - 1 - i))) & 0xF));
    writer.write (";");
  }

  private void indent () throws IOException
  {
    for (int i = 0; i < level; i++)
      writer.write (indentString);
  }

  private final void push (final String name)
  {
    if (level == stack.length)
    {
      final String [] tem = stack;
      stack = new String [stack.length * 2];
      System.arraycopy (tem, 0, stack, 0, tem.length);
    }
    stack[level++] = name;
  }

  private final String pop ()
  {
    return stack[--level];
  }
}
