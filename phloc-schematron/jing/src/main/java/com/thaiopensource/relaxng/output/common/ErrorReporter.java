package com.thaiopensource.relaxng.output.common;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.thaiopensource.relaxng.edit.SourceLocation;
import com.thaiopensource.util.Localizer;

public class ErrorReporter
{
  private final Localizer localizer;
  private final ErrorHandler eh;
  private boolean hadError = false;

  static public class WrappedSAXException extends RuntimeException
  {
    private final SAXException exception;

    private WrappedSAXException (final SAXException exception)
    {
      this.exception = exception;
    }

    public SAXException getException ()
    {
      return exception;
    }
  }

  public ErrorReporter (final ErrorHandler eh, final Class <?> cls)
  {
    this.eh = eh;
    this.localizer = new Localizer (cls);
  }

  public void error (final String key, final SourceLocation loc)
  {
    hadError = true;
    if (eh == null)
      return;
    try
    {
      eh.error (makeParseException (localizer.message (key), loc));
    }
    catch (final SAXException e)
    {
      throw new WrappedSAXException (e);
    }
  }

  public void error (final String key, final String arg, final SourceLocation loc)
  {
    hadError = true;
    if (eh == null)
      return;
    try
    {
      eh.error (makeParseException (localizer.message (key, arg), loc));
    }
    catch (final SAXException e)
    {
      throw new WrappedSAXException (e);
    }
  }

  public void error (final String key, final String arg1, final String arg2, final SourceLocation loc)
  {
    hadError = true;
    if (eh == null)
      return;
    try
    {
      eh.error (makeParseException (localizer.message (key, arg1, arg2), loc));
    }
    catch (final SAXException e)
    {
      throw new WrappedSAXException (e);
    }
  }

  public void warning (final String key, final SourceLocation loc)
  {
    if (eh == null)
      return;
    try
    {
      eh.warning (makeParseException (localizer.message (key), loc));
    }
    catch (final SAXException e)
    {
      throw new WrappedSAXException (e);
    }
  }

  public void warning (final String key, final String arg, final SourceLocation loc)
  {
    if (eh == null)
      return;
    try
    {
      eh.warning (makeParseException (localizer.message (key, arg), loc));
    }
    catch (final SAXException e)
    {
      throw new WrappedSAXException (e);
    }
  }

  public void warning (final String key, final String arg1, final String arg2, final SourceLocation loc)
  {
    if (eh == null)
      return;
    try
    {
      eh.warning (makeParseException (localizer.message (key, arg1, arg2), loc));
    }
    catch (final SAXException e)
    {
      throw new WrappedSAXException (e);
    }
  }

  public boolean getHadError ()
  {
    return hadError;
  }

  private static SAXParseException makeParseException (final String message, final SourceLocation loc)
  {
    if (loc == null)
      return new SAXParseException (message, null);
    return new SAXParseException (message, null, loc.getUri (), loc.getLineNumber (), loc.getColumnNumber ());
  }

  public Localizer getLocalizer ()
  {
    return localizer;
  }
}
