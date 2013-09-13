package com.thaiopensource.validation;

import java.io.IOException;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

import com.thaiopensource.resolver.Resolver;
import com.thaiopensource.resolver.xml.ls.LS;
import com.thaiopensource.resolver.xml.sax.SAXResolver;
import com.thaiopensource.xml.sax.DraconianErrorHandler;

/**
 * Implements Validator2 in terms of ValidatorHandler2.
 */
class ValidatorImpl extends Validator2
{
  private final ValidatorHandler2 handler;
  private XMLReader cachedXMLReader = null;
  private LSResourceResolver cachedResourceResolver = null;
  private boolean needReset = false;

  private static final String LEXICAL_HANDLER_PROPERTY = "http://xml.org/sax/properties/lexical-handler";

  public ValidatorImpl (final ValidatorHandler2 handler)
  {
    this.handler = handler;
  }

  @Override
  public void reset ()
  {
    handler.reset ();
    needReset = false;
    // XXX not sure if we should do this
    handler.setErrorHandler (null);
    handler.setResourceResolver (null);
  }

  @Override
  public void validate (final Source source, final Result result) throws SAXException, IOException
  {
    if (source == null)
      throw new NullPointerException ();
    try
    {
      if (source instanceof SAXSource)
      {
        if (result != null && !(result instanceof SAXResult))
          throw new IllegalArgumentException ();
        doValidate ((SAXSource) source, result);
      }
      else
        if (source instanceof StreamSource)
        {
          if (result != null && !(result instanceof StreamResult))
            throw new IllegalArgumentException ();
          doValidate (new SAXSource (SAXSource.sourceToInputSource (source)), result);
        }
        else
          if (source instanceof DOMSource)
          {
            if (result != null && !(result instanceof DOMResult))
              throw new IllegalArgumentException ();
            doValidate ((DOMSource) source, (DOMResult) result);
          }
          // else if (source instanceof StAXSource) {
          // if (result != null && !(result instanceof StAXResult))
          // throw new IllegalArgumentException();
          // doValidate((StAXSource)source, (StAXResult)result);
          // }
          else
            throw new IllegalArgumentException ("unsupported type of Source: " + source.getClass ().getName ());
    }
    catch (final TransformerException e)
    {
      // XXX unwrap if possible
      throw new SAXException (e);
    }
  }

  // private void doValidate(StAXSource source, StAXResult result)
  // throws SAXException, IOException, TransformerException {
  // // XXX transform source and result
  // throw new IllegalArgumentException();
  // }

  private void doValidate (final DOMSource source, final DOMResult result) throws SAXException,
                                                                          IOException,
                                                                          TransformerException
  {
    // XXX transform source and result
    throw new IllegalArgumentException ();
  }

  private TransformerHandler getIdentityTransformerHandler () throws SAXException, TransformerConfigurationException
  {
    final TransformerFactory transformerFactory = TransformerFactory.newInstance ();
    if (!transformerFactory.getFeature (SAXTransformerFactory.FEATURE))
      throw new SAXException ("TransformerFactory must implement SAXTransformerFactory");
    return ((SAXTransformerFactory) transformerFactory).newTransformerHandler ();
  }

  private void doValidate (final SAXSource source, final Result result) throws SAXException,
                                                                       IOException,
                                                                       TransformerConfigurationException
  {
    if (result == null)
      doValidate (source, null, null, null);
    else
      if (result instanceof SAXResult)
      {
        final SAXResult saxResult = (SAXResult) result;
        doValidate (source, saxResult.getHandler (), saxResult.getLexicalHandler (), null);
      }
      else
      {
        final TransformerHandler identityHandler = getIdentityTransformerHandler ();
        identityHandler.setResult (result);
        doValidate (source, identityHandler, identityHandler, identityHandler);
      }
  }

  private void doValidate (final SAXSource source,
                           final ContentHandler contentHandler,
                           final LexicalHandler lexicalHandler,
                           final DTDHandler dtdHandler) throws SAXException, IOException
  {
    XMLReader xr = source.getXMLReader ();
    if (xr == null)
    {
      final LSResourceResolver resourceResolver = handler.getResourceResolver ();
      if (cachedXMLReader != null && cachedResourceResolver == resourceResolver)
        xr = cachedXMLReader;
      else
      {
        Resolver resolver = null;
        if (resourceResolver != null)
          resolver = LS.createResolver (resourceResolver);
        xr = new SAXResolver (resolver).createXMLReader ();
        cachedXMLReader = xr;
        cachedResourceResolver = resourceResolver;
      }
    }

    handler.setContentHandler (contentHandler);
    handler.setDTDHandler (dtdHandler);
    // always set the lexical handler to avoid problems when reusing the
    // XMLReader
    try
    {
      xr.setProperty (LEXICAL_HANDLER_PROPERTY, lexicalHandler);
    }
    catch (final SAXNotRecognizedException e)
    {
      // ignore it
    }
    catch (final SAXNotSupportedException e)
    {
      // ignore it
    }
    xr.setContentHandler (handler);
    xr.setDTDHandler (handler);
    ErrorHandler eh = handler.getErrorHandler ();
    if (eh == null)
      eh = new DraconianErrorHandler ();
    xr.setErrorHandler (eh);
    if (needReset)
      handler.reset ();
    else
      needReset = true;
    xr.parse (source.getInputSource ());
  }

  @Override
  public void setErrorHandler (final ErrorHandler errorHandler)
  {
    handler.setErrorHandler (errorHandler);
  }

  @Override
  public ErrorHandler getErrorHandler ()
  {
    return handler.getErrorHandler ();
  }

  @Override
  public void setResourceResolver (final LSResourceResolver resourceResolver)
  {
    handler.setResourceResolver (resourceResolver);
  }

  @Override
  public LSResourceResolver getResourceResolver ()
  {
    return handler.getResourceResolver ();
  }

  @Override
  public boolean getFeature (final String name) throws SAXNotRecognizedException, SAXNotSupportedException
  {
    return handler.getFeature (name);
  }

  @Override
  public void setFeature (final String name, final boolean value) throws SAXNotRecognizedException,
                                                                 SAXNotSupportedException
  {
    handler.setFeature (name, value);
  }

  @Override
  public void setProperty (final String name, final Object object) throws SAXNotRecognizedException,
                                                                  SAXNotSupportedException
  {
    handler.setProperty (name, object);
  }

  @Override
  public Object getProperty (final String name) throws SAXNotRecognizedException, SAXNotSupportedException
  {
    return handler.getProperty (name);
  }
}
