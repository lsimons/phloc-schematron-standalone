package com.thaiopensource.xml.sax;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class CountingErrorHandler implements ErrorHandler
{
  private ErrorHandler errorHandler;
  private int fatalErrorCount = 0;
  private int errorCount = 0;
  private int warningCount = 0;
  private boolean hadErrorOrFatalError = false;

  public CountingErrorHandler ()
  {
    this (null);
  }

  public CountingErrorHandler (final ErrorHandler errorHandler)
  {
    this.errorHandler = errorHandler;
  }

  public void reset ()
  {
    fatalErrorCount = 0;
    errorCount = 0;
    warningCount = 0;
    hadErrorOrFatalError = false;
  }

  public boolean getHadErrorOrFatalError ()
  {
    return hadErrorOrFatalError;
  }

  public int getFatalErrorCount ()
  {
    return fatalErrorCount;
  }

  public int getErrorCount ()
  {
    return errorCount;
  }

  public int getWarningCount ()
  {
    return warningCount;
  }

  public ErrorHandler getErrorHandler ()
  {
    return errorHandler;
  }

  public void setErrorHandler (final ErrorHandler errorHandler)
  {
    this.errorHandler = errorHandler;
  }

  public void warning (final SAXParseException exception) throws SAXException
  {
    warningCount++;
    if (errorHandler != null)
      errorHandler.warning (exception);
  }

  public void error (final SAXParseException exception) throws SAXException
  {
    errorCount++;
    hadErrorOrFatalError = true;
    if (errorHandler != null)
      errorHandler.error (exception);
  }

  public void fatalError (final SAXParseException exception) throws SAXException
  {
    fatalErrorCount++;
    hadErrorOrFatalError = true;
    if (errorHandler != null)
      errorHandler.fatalError (exception);
  }
}
