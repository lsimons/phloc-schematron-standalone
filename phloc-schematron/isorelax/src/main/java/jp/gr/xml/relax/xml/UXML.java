package jp.gr.xml.relax.xml;

/**
 * UXML
 * 
 * @since Jan. 29, 2000
 * @version Feb. 24, 2001
 * @author ASAMI, Tomoharu (asami@zeomtech.com)
 */
public final class UXML
{
  public static String escape (final String string)
  {
    if (string.indexOf ('<') == -1 &&
        string.indexOf ('>') == -1 &&
        string.indexOf ('&') == -1 &&
        string.indexOf ('"') == -1 &&
        string.indexOf ('\'') == -1)
    {

      return (string);
    }
    final int size = string.length ();
    final StringBuilder buffer = new StringBuilder (size * 2);
    for (int i = 0; i < size; i++)
    {
      final char c = string.charAt (i);
      if (c == '<')
      {
        buffer.append ("&lt;");
      }
      else
        if (c == '>')
        {
          buffer.append ("&gt;");
        }
        else
          if (c == '&')
          {
            buffer.append ("&amp;");
          }
          else
            if (c == '"')
            {
              buffer.append ("&quot;");
            }
            else
              if (c == '\'')
              {
                buffer.append ("&apos;");
              }
              else
              {
                buffer.append (c);
              }
    }
    return buffer.toString ();
  }

  public static String escapeEntityQuot (final String string)
  {
    if (string.indexOf ('%') == -1 && string.indexOf ('&') == -1 && string.indexOf ('"') == -1)
    {

      return (string);
    }
    final int size = string.length ();
    final StringBuilder buffer = new StringBuilder (size * 2);
    for (int i = 0; i < size; i++)
    {
      final char c = string.charAt (i);
      if (c == '%')
      {
        buffer.append ("&---;");
      }
      else
        if (c == '&')
        {
          buffer.append ("&amp;");
        }
        else
          if (c == '"')
          {
            buffer.append ("&quot;");
          }
          else
          {
            buffer.append (c);
          }
    }
    return buffer.toString ();
  }

  public static String escapeEntityApos (final String string)
  {
    if (string.indexOf ('%') == -1 && string.indexOf ('&') == -1 && string.indexOf ('\'') == -1)
    {

      return (string);
    }
    final int size = string.length ();
    final StringBuilder buffer = new StringBuilder (size * 2);
    for (int i = 0; i < size; i++)
    {
      final char c = string.charAt (i);
      if (c == '%')
      {
        buffer.append ("&#x25;");
      }
      else
        if (c == '&')
        {
          buffer.append ("&amp;");
        }
        else
          if (c == '\'')
          {
            buffer.append ("&apos;");
          }
          else
          {
            buffer.append (c);
          }
    }
    return buffer.toString ();
  }

  public static String escapeAttrQuot (final String string)
  {
    if (string.indexOf ('<') == -1 && string.indexOf ('&') == -1 && string.indexOf ('"') == -1)
    {
      return (string);
    }
    final int size = string.length ();
    final StringBuilder buffer = new StringBuilder (size * 2);
    for (int i = 0; i < size; i++)
    {
      final char c = string.charAt (i);
      if (c == '<')
      {
        buffer.append ("&lt;");
      }
      else
        if (c == '&')
        {
          buffer.append ("&amp;");
        }
        else
          if (c == '"')
          {
            buffer.append ("&quot;");
          }
          else
          {
            buffer.append (c);
          }
    }
    return buffer.toString ();
  }

  public static String escapeAttrApos (final String string)
  {
    if (string.indexOf ('<') == -1 && string.indexOf ('&') == -1 && string.indexOf ('\'') == -1)
    {

      return (string);
    }
    final int size = string.length ();
    final StringBuilder buffer = new StringBuilder (size * 2);
    for (int i = 0; i < size; i++)
    {
      final char c = string.charAt (i);
      if (c == '<')
      {
        buffer.append ("&lt;");
      }
      else
        if (c == '&')
        {
          buffer.append ("&amp;");
        }
        else
          if (c == '\'')
          {
            buffer.append ("&apos;");
          }
          else
          {
            buffer.append (c);
          }
    }
    return buffer.toString ();
  }

  public static String escapeSystemQuot (final String string)
  {
    if (string.indexOf ('"') == -1)
    {
      return (string);
    }
    final int size = string.length ();
    final StringBuilder buffer = new StringBuilder (size * 2);
    for (int i = 0; i < size; i++)
    {
      final char c = string.charAt (i);
      if (c == '"')
      {
        buffer.append ("&quot;");
      }
      else
      {
        buffer.append (c);
      }
    }
    return buffer.toString ();
  }

  public static String escapeSystemApos (final String string)
  {
    if (string.indexOf ('\'') == -1)
    {
      return (string);
    }
    final int size = string.length ();
    final StringBuilder buffer = new StringBuilder (size * 2);
    for (int i = 0; i < size; i++)
    {
      final char c = string.charAt (i);
      if (c == '\'')
      {
        buffer.append ("&apos;");
      }
      else
      {
        buffer.append (c);
      }
    }
    return buffer.toString ();
  }

  public static String escapeCharData (final String string)
  {
    if (string.indexOf ('<') == -1 && string.indexOf ('&') == -1 && string.indexOf ("]]>") == -1)
    {

      return (string);
    }
    final int size = string.length ();
    final StringBuilder buffer = new StringBuilder (size * 2);
    int nBrackets = 0;
    for (int i = 0; i < size; i++)
    {
      final char c = string.charAt (i);
      if (c == '<')
      {
        buffer.append ("&lt;");
      }
      else
        if (c == '&')
        {
          buffer.append ("&amp;");
        }
        else
          if (c == '>' && nBrackets >= 2)
          {
            buffer.append ("&gt;");
          }
          else
          {
            buffer.append (c);
          }
      if (c == ']')
      {
        nBrackets++;
      }
      else
      {
        nBrackets = 0;
      }
    }
    return buffer.toString ();
  }
}
