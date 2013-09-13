package com.thaiopensource.validate.xerces;

import org.apache.xerces.util.ErrorHandlerWrapper;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLParseException;
import org.xml.sax.ErrorHandler;

class SAXXMLErrorHandler extends ErrorHandlerWrapper
{
  private boolean hadError = false;

  SAXXMLErrorHandler (final ErrorHandler errorHandler)
  {
    super (errorHandler);
  }

  void reset ()
  {
    hadError = false;
  }

  @Override
  public void error (final String domain, final String key, final XMLParseException exception) throws XNIException
  {
    hadError = true;
    if (fErrorHandler == null)
      return;
    super.error (domain, key, exception);
  }

  @Override
  public void warning (final String domain, final String key, final XMLParseException exception) throws XNIException
  {
    if (fErrorHandler == null)
      return;
    super.warning (domain, key, exception);
  }

  @Override
  public void fatalError (final String domain, final String key, final XMLParseException exception) throws XNIException
  {
    hadError = true;
    if (fErrorHandler == null)
      return;
    super.fatalError (domain, key, exception);
  }

  boolean getHadError ()
  {
    return hadError;
  }
}
