package com.thaiopensource.xml.sax;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.thaiopensource.util.UriOrFile;

public class ErrorHandlerImpl implements ErrorHandler
{
  private final PrintWriter err;

  private final String bundleName = "com.thaiopensource.xml.sax.resources.Messages";

  private ResourceBundle bundle = null;

  public ErrorHandlerImpl ()
  {
    this (System.err);
  }

  public ErrorHandlerImpl (final OutputStream os)
  {
    this.err = new PrintWriter (os);
  }

  public ErrorHandlerImpl (final Writer w)
  {
    this.err = new PrintWriter (w);
  }

  public void close ()
  {
    err.close ();
  }

  private String getString (final String key)
  {
    if (bundle == null)
      bundle = ResourceBundle.getBundle (bundleName);
    return bundle.getString (key);
  }

  private String format (final String key, final Object [] args)
  {
    return MessageFormat.format (getString (key), args);
  }

  public void warning (final SAXParseException e) throws SAXParseException
  {
    print (format ("warning", new Object [] { formatMessage (e), formatLocation (e) }));
  }

  public void error (final SAXParseException e)
  {
    print (format ("error", new Object [] { formatMessage (e), formatLocation (e) }));
  }

  public void fatalError (final SAXParseException e) throws SAXParseException
  {
    throw e;
  }

  public void printException (final Throwable e)
  {
    String loc;
    if (e instanceof SAXParseException)
      loc = formatLocation ((SAXParseException) e);
    else
      loc = "";
    String message;
    if (e instanceof SAXException)
      message = formatMessage ((SAXException) e);
    else
      message = formatMessage (e);
    print (format ("fatal", new Object [] { message, loc }));
  }

  public void print (final String message)
  {
    if (message.length () != 0)
    {
      err.println (message);
      err.flush ();
    }
  }

  private String formatLocation (final SAXParseException e)
  {
    String systemId = e.getSystemId ();
    int n = e.getLineNumber ();
    final Integer lineNumber = n >= 0 ? new Integer (n) : null;
    n = e.getColumnNumber ();
    final Integer columnNumber = n >= 0 ? new Integer (n) : null;
    if (systemId != null)
    {
      systemId = UriOrFile.uriToUriOrFile (systemId);
      if (lineNumber != null)
      {
        if (columnNumber != null)
          return format ("locator_system_id_line_number_column_number", new Object [] { systemId,
                                                                                       lineNumber,
                                                                                       columnNumber });
        else
          return format ("locator_system_id_line_number", new Object [] { systemId, lineNumber });
      }
      else
        return format ("locator_system_id", new Object [] { systemId });
    }
    else
      if (lineNumber != null)
      {
        if (columnNumber != null)
          return format ("locator_line_number_column_number", new Object [] { lineNumber, columnNumber });
        else
          return format ("locator_line_number", new Object [] { lineNumber });
      }
      else
        return "";
  }

  private String formatMessage (final SAXException se)
  {
    final Exception e = se.getException ();
    String detail = se.getMessage ();
    if (e != null)
    {
      final String detail2 = e.getMessage ();
      // Crimson stupidity
      if (detail2 == detail || e.getClass ().getName ().equals (detail))
        return formatMessage (e);
      else
        if (detail2 == null)
          return format ("exception", new Object [] { e.getClass ().getName (), detail });
        else
          return format ("tunnel_exception", new Object [] { e.getClass ().getName (), detail, detail2 });
    }
    else
    {
      if (detail == null)
        detail = getString ("no_detail");
      return detail;
    }
  }

  private String formatMessage (final Throwable e)
  {
    String detail = e.getMessage ();
    if (detail == null)
      detail = getString ("no_detail");
    if (e instanceof FileNotFoundException)
      return format ("file_not_found", new Object [] { detail });
    return format ("exception", new Object [] { e.getClass ().getName (), detail });
  }
}
