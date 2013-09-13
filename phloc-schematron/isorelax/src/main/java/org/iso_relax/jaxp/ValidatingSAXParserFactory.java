package org.iso_relax.jaxp;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.iso_relax.verifier.Schema;
import org.iso_relax.verifier.VerifierConfigurationException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 * Wraps another {@link SAXParserFactory} and adds validation capability.
 * 
 * @author Daisuke OKAJIMA
 */
public class ValidatingSAXParserFactory extends SAXParserFactory
{
  protected SAXParserFactory _WrappedFactory;
  protected Schema _Schema;

  private boolean validation = true;

  /**
   * creates a new instance that wraps the default DocumentBuilderFactory
   * 
   * @param schema
   *        the compiled Schema object. It can not be null.
   */
  public ValidatingSAXParserFactory (final Schema schema)
  {
    this (SAXParserFactory.newInstance (), schema);
  }

  /**
   * creates a new instance with an internal SAXParserFactory and Schema.
   * 
   * @param wrapped
   *        internal SAXParser
   * @param schema
   *        compiled schema.
   */
  public ValidatingSAXParserFactory (final SAXParserFactory wrapped, final Schema schema)
  {
    _WrappedFactory = wrapped;
    _Schema = schema;
  }

  /**
   * returns a new SAX parser. If setValidating(false) is called previously,
   * this method simply returns the implementation of wrapped SAXParser.
   */
  @Override
  public SAXParser newSAXParser () throws ParserConfigurationException, SAXException
  {
    if (isValidating ())
    {
      try
      {
        return new ValidatingSAXParser (_WrappedFactory.newSAXParser (), _Schema.newVerifier ());
      }
      catch (final VerifierConfigurationException ex)
      {
        throw new ParserConfigurationException (ex.getMessage ());
      }
    }
    return _WrappedFactory.newSAXParser ();
  }

  /**
   * @see SAXParserFactory#setFeature(String, boolean)
   */
  @Override
  public void setFeature (final String name, final boolean value) throws ParserConfigurationException,
                                                                 SAXNotRecognizedException,
                                                                 SAXNotSupportedException
  {
    _WrappedFactory.setFeature (name, value);
  }

  /**
   * @see SAXParserFactory#getFeature(String)
   */
  @Override
  public boolean getFeature (final String name) throws ParserConfigurationException,
                                               SAXNotRecognizedException,
                                               SAXNotSupportedException
  {
    return _WrappedFactory.getFeature (name);
  }

  @Override
  public boolean isNamespaceAware ()
  {
    return _WrappedFactory.isNamespaceAware ();
  }

  @Override
  public void setNamespaceAware (final boolean awareness)
  {
    _WrappedFactory.setNamespaceAware (awareness);
  }

  @Override
  public boolean isValidating ()
  {
    return validation;
  }

  @Override
  public void setValidating (final boolean validating)
  {
    validation = validating;
  }
}
