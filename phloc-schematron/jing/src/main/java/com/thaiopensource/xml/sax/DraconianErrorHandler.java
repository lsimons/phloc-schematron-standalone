package com.thaiopensource.xml.sax;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * An <code>ErrorHandler</code> implementing a brutal error handling policy.
 * Fatal errors and errors are handled by throwing the exception. Warnings are
 * ignored.
 * 
 * @author <a href="mailto:jjc@jclark.com">James Clark</a>
 */
public class DraconianErrorHandler implements ErrorHandler
{
  public void warning (final SAXParseException e) throws SAXException
  {}

  public void error (final SAXParseException e) throws SAXException
  {
    throw e;
  }

  public void fatalError (final SAXParseException e) throws SAXException
  {
    throw e;
  }
}
