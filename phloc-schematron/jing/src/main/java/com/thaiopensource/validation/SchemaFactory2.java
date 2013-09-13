package com.thaiopensource.validation;

import java.io.File;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 * Extends the SchemaFactory abstract class. All methods of SchemaFactory that
 * return a Schema are overridden to return a Schema2. Default implementations
 * of several methods are provided.
 * 
 * @see SchemaFactory
 */
public abstract class SchemaFactory2 extends SchemaFactory
{
  // Corresponds to XMLConstants.FEATURE_SECURE_PROCESSING.
  private boolean secureProcessing = false;
  private ErrorHandler errorHandler = null;
  private LSResourceResolver resourceResolver = null;

  /**
   * Create a new Schema from a SAXSource. Subclasses must implement this.
   * 
   * @see SchemaFactory#newSchema(Source)
   */
  public abstract Schema2 newSchema (SAXSource schema) throws SAXException;

  @Override
  public Schema2 newSchema (final Source [] schemas) throws SAXException
  {
    if (schemas.length != 1)
      throw new UnsupportedOperationException ();
    return newSchema (schemas[0]);
  }

  /**
   * This implementation of SchemaFactory#newSchema simply throws
   * UnsupportedOperationException.
   * 
   * @see SchemaFactory#newSchema
   */
  @Override
  public Schema2 newSchema () throws SAXException
  {
    throw new UnsupportedOperationException ();
  }

  @Override
  public Schema2 newSchema (final Source source) throws SAXException
  {
    if (source == null)
      throw new NullPointerException ();
    if (source instanceof SAXSource)
      return newSchema ((SAXSource) source);
    final InputSource inputSource = SAXSource.sourceToInputSource (source);
    // XXX support other types of Source for the schema
    if (inputSource == null)
      throw new IllegalArgumentException ("unsupported type of Source for schema");
    return newSchema (new SAXSource (inputSource));
  }

  @Override
  public Schema2 newSchema (final File schema) throws SAXException
  {
    return newSchema (new StreamSource (schema));
  }

  @Override
  public Schema2 newSchema (final URL schema) throws SAXException
  {
    return newSchema (new StreamSource (schema.toExternalForm ()));
  }

  @Override
  public void setErrorHandler (final ErrorHandler errorHandler)
  {
    this.errorHandler = errorHandler;
  }

  @Override
  public ErrorHandler getErrorHandler ()
  {
    return errorHandler;
  }

  @Override
  public void setResourceResolver (final LSResourceResolver resourceResolver)
  {
    this.resourceResolver = resourceResolver;
  }

  @Override
  public LSResourceResolver getResourceResolver ()
  {
    return resourceResolver;
  }

  /**
   * Extends SchemaFactory.setFeature by implementing the secure processing
   * feature. The implementation simply sets an internal flag, which can be
   * accessed using getSecureProcessing.
   * 
   * @see SchemaFactory#setFeature
   * @see #getSecureProcessing
   */
  @Override
  public void setFeature (final String name, final boolean value) throws SAXNotRecognizedException,
                                                                 SAXNotSupportedException
  {
    if (XMLConstants.FEATURE_SECURE_PROCESSING.equals (name))
      secureProcessing = value;
    else
      super.setFeature (name, value);
  }

  /**
   * Extends SchemaFactory.setFeature by implementing the secure processing
   * feature. The implementation simply sets an internal flag, which can be
   * accessed using getSecureProcessing.
   * 
   * @see SchemaFactory#getFeature
   * @see #getSecureProcessing
   */
  @Override
  public boolean getFeature (final String name) throws SAXNotRecognizedException, SAXNotSupportedException
  {
    if (XMLConstants.FEATURE_SECURE_PROCESSING.equals (name))
      return secureProcessing;
    return super.getFeature (name);
  }

  public void setSecureProcessing (final boolean secureProcessing)
  {
    this.secureProcessing = secureProcessing;
  }

  public boolean getSecureProcessing ()
  {
    return secureProcessing;
  }
}
