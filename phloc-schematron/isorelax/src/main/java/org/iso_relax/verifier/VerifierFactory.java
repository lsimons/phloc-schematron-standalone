package org.iso_relax.verifier;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ServiceLoader;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;

/**
 * VerifierFactory
 * 
 * @since Feb. 23, 2001
 * @version Apr. 17, 2001
 * @author ASAMI, Tomoharu (asami@zeomtech.com)
 * @author <a href="mailto:kohsukekawaguchi@yahoo.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class VerifierFactory
{

  /**
   * parses a schema at the specified location and returns a Verifier object
   * that validates documents by using that schema.
   * <p>
   * Some of XML parsers accepts filenames as well as URLs, while others reject
   * them. Therefore, to parse a file as a schema, you should use a File object.
   * 
   * @param uri
   *        URI of a schema file
   */
  public Verifier newVerifier (final String uri) throws VerifierConfigurationException, SAXException, IOException
  {

    return compileSchema (uri).newVerifier ();
  }

  /**
   * parses a schema from the specified file and returns a Verifier object that
   * validates documents by using that schema.
   * 
   * @param file
   *        File of a schema file
   */
  public Verifier newVerifier (final File file) throws VerifierConfigurationException, SAXException, IOException
  {

    return compileSchema (file).newVerifier ();
  }

  /**
   * parses a schema from the specified InputStream and returns a Verifier
   * object that validates documents by using that schema.
   */
  public Verifier newVerifier (final InputStream stream) throws VerifierConfigurationException,
                                                        SAXException,
                                                        IOException
  {

    return compileSchema (stream, null).newVerifier ();
  }

  /**
   * parses a schema from the specified InputStream and returns a Verifier
   * object that validates documents by using that schema.
   * 
   * @param systemId
   *        System ID of this stream.
   */
  public Verifier newVerifier (final InputStream stream, final String systemId) throws VerifierConfigurationException,
                                                                               SAXException,
                                                                               IOException
  {

    return compileSchema (stream, systemId).newVerifier ();
  }

  /**
   * parses a schema from the specified InputSource and returns a Verifier
   * object that validates documents by using that schema.
   * 
   * @param source
   *        InputSource of a schema file
   */
  public Verifier newVerifier (final InputSource source) throws VerifierConfigurationException,
                                                        SAXException,
                                                        IOException
  {

    return compileSchema (source).newVerifier ();
  }

  /**
   * processes a schema into a Schema object, which is a compiled representation
   * of a schema. The obtained schema object can then be used concurrently
   * across multiple threads.
   */
  public abstract Schema compileSchema (InputSource is) throws VerifierConfigurationException,
                                                       SAXException,
                                                       IOException;

  /**
   * processes a schema into a Schema object, which is a compiled representation
   * of a schema. The obtained schema object can then be used concurrently
   * across multiple threads.
   * <p>
   * Some of XML parsers accepts filenames as well as URLs, while others reject
   * them. Therefore, to parse a file as a schema, you should use a File object.
   * 
   * @param url
   *        A source url of a schema file to be compiled.
   */
  public Schema compileSchema (final String url) throws VerifierConfigurationException, SAXException, IOException
  {

    return compileSchema (new InputSource (url));
  }

  /**
   * processes a schema into a Schema object, which is a compiled representation
   * of a schema. The obtained schema object can then be used concurrently
   * across multiple threads.
   * 
   * @param stream
   *        A stream object that holds a schema.
   */
  public Schema compileSchema (final InputStream stream) throws VerifierConfigurationException,
                                                        SAXException,
                                                        IOException
  {

    return compileSchema (stream, null);
  }

  /**
   * processes a schema into a Schema object, which is a compiled representation
   * of a schema. The obtained schema object can then be used concurrently
   * across multiple threads.
   * 
   * @param systemId
   *        The system Id of this input stream.
   */
  public Schema compileSchema (final InputStream stream, final String systemId) throws VerifierConfigurationException,
                                                                               SAXException,
                                                                               IOException
  {

    final InputSource is = new InputSource (stream);
    is.setSystemId (systemId);
    return compileSchema (is);
  }

  /**
   * processes a schema into a Schema object, which is a compiled representation
   * of a schema. The obtained schema object can then be used concurrently
   * across multiple threads.
   * 
   * @param f
   *        A schema file to be compiled
   */
  public Schema compileSchema (final File f) throws VerifierConfigurationException, SAXException, IOException
  {

    String uri = "file:" + f.getAbsolutePath ();
    if (File.separatorChar == '\\')
    {
      uri = uri.replace ('\\', '/');
    }
    return compileSchema (new InputSource (uri));
  }

  /**
   * Indicates whether if the feature is supported, or not.
   * 
   * @param feature
   *        feature name
   */
  @SuppressWarnings ("deprecation")
  public boolean isFeature (final String feature) throws SAXNotRecognizedException
  {

    if (Verifier.FEATURE_HANDLER.equals (feature) || Verifier.FEATURE_FILTER.equals (feature))
      return true;

    throw new SAXNotRecognizedException (feature);
  }

  /**
   * Sets feature value
   * 
   * @param feature
   *        feature name
   * @param value
   *        feature value
   */
  public void setFeature (final String feature, final boolean value) throws SAXNotRecognizedException
  {
    throw new SAXNotRecognizedException (feature);
  }

  /**
   * Gets property value
   * 
   * @param property
   *        property name
   */
  public Object getProperty (final String property) throws SAXNotRecognizedException
  {
    throw new SAXNotRecognizedException (property);
  }

  /**
   * Sets property value
   * 
   * @param property
   *        property name
   * @param value
   *        property value
   */
  public void setProperty (final String property, final Object value) throws SAXNotRecognizedException

  {
    throw new SAXNotRecognizedException (property);
  }

  private EntityResolver resolver = null;

  /**
   * Sets an EntityResolver This entity resolver is used to resolve entities
   * encountered while parsing a schema.
   */
  public void setEntityResolver (final EntityResolver _resolver)
  {
    this.resolver = _resolver;
  }

  /**
   * Gets the current entity resolver, which was set by the
   * <code>SetEntityResolver</code> method.
   */
  public EntityResolver getEntityResolver ()
  {
    return resolver;
  }

  /**
   * Creates a new instance of a VerifierFactory.
   * 
   * @deprecated
   */
  @Deprecated
  public static VerifierFactory newInstance () throws VerifierConfigurationException
  {

    return newInstance ("http://www.xml.gr.jp/xmlns/relaxNamespace");
  }

  /**
   * Creates a new instance of a VerifierFactory for the specified schema
   * language.
   * 
   * @param language
   *        URI that specifies the schema language.
   *        <p>
   *        It is preferable to use the namespace URI of the schema language to
   *        designate the schema language. For example,
   *        <table>
   *        <thead>
   *        <tr>
   *        <td>URI</td>
   *        <td>language</td>
   *        </tr>
   *        </thead><tbody>
   *        <tr>
   *        <td><tt>http://relaxng.org/ns/structure/0.9</tt></td>
   *        <td><a href="http://www.oasis-open.org/committees/relax-ng/"> RELAX
   *        NG </a></td>
   *        </tr>
   *        <tr>
   *        <td><tt>http://www.xml.gr.jp/xmlns/relaxCore</tt></td>
   *        <td><a href="http://www.xml.gr.jp/relax"> RELAX Core </a></td>
   *        </tr>
   *        <tr>
   *        <td><tt>http://www.xml.gr.jp/xmlns/relaxNamespace</tt></td>
   *        <td><a href="http://www.xml.gr.jp/relax"> RELAX Namespace </a></td>
   *        </tr>
   *        <tr>
   *        <td><tt>http://www.thaiopensource.com/trex</tt></td>
   *        <td><a href="http://www.thaiopensource.com/trex"> TREX </a></td>
   *        </tr>
   *        <tr>
   *        <td><tt>http://www.w3.org/2001/XMLSchema</tt></td>
   *        <td><a href="http://www.w3.org/TR/xmlschema-1"> W3C XML Schema </a></td>
   *        </tr>
   *        <tr>
   *        <td><tt>http://www.w3.org/XML/1998/namespace</tt></td>
   *        <td><a href="http://www.w3.org/TR/REC-xml"> XML DTD </a></td>
   *        </tr>
   *        </tbody>
   *        </table>
   * @param classLoader
   *        This class loader is used to search the available implementation.
   * @return a non-null valid VerifierFactory instance.
   * @exception VerifierConfigurationException
   *            if no implementation is available for the specified language.
   */
  public static VerifierFactory newInstance (final String language, final ClassLoader classLoader) throws VerifierConfigurationException
  {
    for (final VerifierFactoryLoader loader : ServiceLoader.load (VerifierFactoryLoader.class, classLoader))
    {
      try
      {
        final VerifierFactory factory = loader.createFactory (language);
        if (factory != null)
          return factory;
      }
      catch (final Throwable t)
      {} // ignore any error
    }
    throw new VerifierConfigurationException ("no validation engine available for: " + language);
  }

  public static VerifierFactory newInstance (final String language) throws VerifierConfigurationException
  {
    return newInstance (language, VerifierFactoryLoader.class.getClassLoader ());
  }
}
