package com.thaiopensource.validate.jarv;

import org.iso_relax.verifier.Verifier;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.thaiopensource.util.PropertyMap;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.validate.Validator;

public class VerifierValidator implements Validator
{
  private final Verifier verifier;
  private ContentHandler handler;

  private static class ExceptionReportHandler extends DefaultHandler
  {
    private final SAXException storedException;

    ExceptionReportHandler (final SAXException storedException)
    {
      this.storedException = storedException;
    }

    @Override
    public void startDocument () throws SAXException
    {
      throw storedException;
    }
  }

  public VerifierValidator (final Verifier verifier, final PropertyMap properties)
  {
    this.verifier = verifier;
    verifier.setErrorHandler (properties.get (ValidateProperty.ERROR_HANDLER));
    final EntityResolver er = properties.get (ValidateProperty.ENTITY_RESOLVER);
    if (er != null)
      verifier.setEntityResolver (er);
    try
    {
      handler = verifier.getVerifierHandler ();
    }
    catch (final SAXException e)
    {
      handler = new ExceptionReportHandler (e);
    }
  }

  public void reset ()
  {
    try
    {
      handler = verifier.getVerifierHandler ();
    }
    catch (final SAXException e)
    {
      handler = new ExceptionReportHandler (e);
    }
  }

  public ContentHandler getContentHandler ()
  {
    return handler;
  }

  public DTDHandler getDTDHandler ()
  {
    return null;
  }
}
