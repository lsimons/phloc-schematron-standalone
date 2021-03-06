package com.thaiopensource.validate.schematron;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ErrorHandler;

import com.thaiopensource.resolver.Resolver;
import com.thaiopensource.resolver.xml.transform.Transform;
import com.thaiopensource.util.PropertyMap;
import com.thaiopensource.validate.ResolverFactory;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.validate.Validator;

class ValidatorImpl implements Validator
{
  private final Templates templates;
  private final SAXTransformerFactory factory;
  private final ContentHandler outputHandler;
  private TransformerHandler transformerHandler;
  private final Resolver resolver;

  ValidatorImpl (final Templates templates, final SAXTransformerFactory factory, final PropertyMap properties)
  {
    this.templates = templates;
    this.factory = factory;
    final ErrorHandler eh = properties.get (ValidateProperty.ERROR_HANDLER);
    outputHandler = new OutputHandler (eh);
    resolver = ResolverFactory.createResolver (properties).getResolver ();
    initTransformerHandler ();
  }

  public ContentHandler getContentHandler ()
  {
    return transformerHandler;
  }

  public DTDHandler getDTDHandler ()
  {
    return transformerHandler;
  }

  public void reset ()
  {
    initTransformerHandler ();
  }

  private void initTransformerHandler ()
  {
    try
    {
      transformerHandler = factory.newTransformerHandler (templates);
      // When you specify a URIResolver, XSLTC uses a DOMCache, which
      // doesn't seem to work too well.
      if (!SchemaReaderImpl.isXsltc (factory.getClass ()))
        transformerHandler.getTransformer ().setURIResolver (Transform.createSAXURIResolver (resolver));
      // XXX set up transformer with an ErrorListener that just throws
      // XXX (what about errors from document() calls?)
    }
    catch (final TransformerConfigurationException e)
    {
      throw new RuntimeException ("could not create transformer");
    }
    transformerHandler.setResult (new SAXResult (outputHandler));
  }
}
