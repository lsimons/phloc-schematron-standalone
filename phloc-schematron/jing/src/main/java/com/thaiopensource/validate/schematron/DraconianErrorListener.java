package com.thaiopensource.validate.schematron;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

class DraconianErrorListener implements ErrorListener
{
  DraconianErrorListener ()
  {}

  public void warning (final TransformerException exception) throws TransformerException
  {}

  public void error (final TransformerException exception) throws TransformerException
  {
    throw exception;
  }

  public void fatalError (final TransformerException exception) throws TransformerException
  {
    throw exception;
  }
}
