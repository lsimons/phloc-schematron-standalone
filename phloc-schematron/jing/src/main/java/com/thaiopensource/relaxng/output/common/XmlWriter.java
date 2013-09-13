package com.thaiopensource.relaxng.output.common;

import java.io.CharConversionException;
import java.io.IOException;
import java.io.Writer;
import java.util.Stack;

import com.thaiopensource.util.Utf16;
import com.thaiopensource.xml.out.CharRepertoire;

public class XmlWriter
{
  private final String lineSep;
  private final String indentString;
  private final Writer w;
  private final CharRepertoire cr;
  private final Stack <String> tagStack = new Stack <String> ();
  private boolean inStartTag = false;
  private boolean inText = false;
  private int level = 0;
  private final String [] topLevelAttributes;

  public static class WrappedException extends RuntimeException
  {
    private final IOException cause;

    @Override
    public Throwable getCause ()
    {
      return cause;
    }

    public IOException getIOException ()
    {
      return cause;
    }

    private WrappedException (final IOException cause)
    {
      this.cause = cause;
    }
  }

  public XmlWriter (final Writer w,
                    final String encoding,
                    final CharRepertoire cr,
                    final String lineSep,
                    final int indent,
                    final String [] topLevelAttributes)
  {
    this.w = w;
    this.lineSep = lineSep;
    this.cr = cr;
    this.topLevelAttributes = topLevelAttributes;
    final char [] tem = new char [indent];
    for (int i = 0; i < indent; i++)
      tem[i] = ' ';
    this.indentString = new String (tem);
    write ("<?xml version=\"1.0\" encoding=\"");
    write (encoding);
    write ("\"?>");
    newline ();
  }

  public void startElement (final String name)
  {
    if (inStartTag)
    {
      maybeWriteTopLevelAttributes ();
      inStartTag = false;
      write (">");
      newline ();
    }
    if (inText)
      inText = false;
    else
      indent ();
    write ('<');
    write (name);
    tagStack.push (name);
    inStartTag = true;
    level++;
  }

  public void endElement ()
  {
    if (inStartTag)
    {
      maybeWriteTopLevelAttributes ();
      level--;
      inStartTag = false;
      tagStack.pop ();
      write ("/>");
    }
    else
    {
      level--;
      if (inText)
        inText = false;
      else
        indent ();
      write ("</");
      write (tagStack.pop ());
      write (">");
    }
    newline ();
  }

  public void attribute (final String name, final String value)
  {
    if (!inStartTag)
      throw new IllegalStateException ("attribute outside of start-tag");
    write (' ');
    write (name);
    write ('=');
    write ('"');
    data (value);
    write ('"');
  }

  public void text (final String s)
  {
    if (s.length () == 0)
      return;
    if (inStartTag)
    {
      maybeWriteTopLevelAttributes ();
      inStartTag = false;
      write (">");
    }
    data (s);
    inText = true;
  }

  public void comment (final String s)
  {
    if (inStartTag)
    {
      maybeWriteTopLevelAttributes ();
      inStartTag = false;
      write (">");
      newline ();
    }
    if (!inText)
      indent ();
    write ("<!--");
    int start = 0;
    level++;
    for (;;)
    {
      final int i = s.indexOf ('\n', start);
      if (i < 0)
      {
        if (start > 0)
        {
          newline ();
          indent ();
          write (s.substring (start));
          level--;
          newline ();
          indent ();
        }
        else
        {
          level--;
          if (s.length () != 0)
          {
            write (' ');
            write (s);
            if (s.charAt (s.length () - 1) != ' ')
              write (' ');
          }
        }
        break;
      }
      newline ();
      indent ();
      write (s.substring (start, i));
      start = i + 1;
    }
    write ("-->");
    if (!inText)
      newline ();
  }

  private void data (final String s)
  {
    final int n = s.length ();
    for (int i = 0; i < n; i++)
    {
      final char c = s.charAt (i);
      switch (c)
      {
        case '<':
          write ("&lt;");
          break;
        case '>':
          write ("&gt;");
          break;
        case '&':
          write ("&amp;");
          break;
        case '\r':
          write ("&#xD;");
          break;
        case '\n':
          write (lineSep);
          break;
        default:
          if (Utf16.isSurrogate (c))
          {
            if (!Utf16.isSurrogate1 (c) || i + 1 == n || !Utf16.isSurrogate2 (s.charAt (i + 1)))
              throw new WrappedException (new CharConversionException ("surrogate pair integrity failure"));
            final char c2 = s.charAt (++i);
            if (cr.contains (c, c2))
            {
              write (c);
              write (c2);
            }
            else
              charRef (Utf16.scalarValue (c, c2));
          }
          else
            if (!cr.contains (c))
              charRef (c);
            else
              write (c);
          break;
      }
    }
  }

  private void charRef (final int n)
  {
    write ("&#x");
    write (Integer.toHexString (n));
    write (';');
  }

  private void indent ()
  {
    for (int i = 0; i < level; i++)
      write (indentString);
  }

  private void newline ()
  {
    write (lineSep);
  }

  private void maybeWriteTopLevelAttributes ()
  {
    if (level != 1)
      return;
    for (int i = 0; i < topLevelAttributes.length; i += 2)
      attribute (topLevelAttributes[i], topLevelAttributes[i + 1]);
  }

  private void write (final String s)
  {
    try
    {
      w.write (s);
    }
    catch (final IOException e)
    {
      throw new WrappedException (e);
    }
  }

  private void write (final char c)
  {
    try
    {
      w.write (c);
    }
    catch (final IOException e)
    {
      throw new WrappedException (e);
    }
  }

  public void close ()
  {
    try
    {
      w.close ();
    }
    catch (final IOException e)
    {
      throw new WrappedException (e);
    }
  }

}
