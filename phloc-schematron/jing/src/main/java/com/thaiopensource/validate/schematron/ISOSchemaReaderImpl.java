package com.thaiopensource.validate.schematron;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TemplatesHandler;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import com.thaiopensource.util.Localizer;
import com.thaiopensource.util.PropertyId;
import com.thaiopensource.util.PropertyMap;
import com.thaiopensource.util.PropertyMapBuilder;
import com.thaiopensource.validate.AbstractSchemaReader;
import com.thaiopensource.validate.IncorrectSchemaException;
import com.thaiopensource.validate.Option;
import com.thaiopensource.validate.ResolverFactory;
import com.thaiopensource.validate.Schema;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.validate.Validator;
import com.thaiopensource.validate.prop.rng.RngProperty;
import com.thaiopensource.validate.prop.schematron.SchematronProperty;
import com.thaiopensource.validate.rng.CompactSchemaReader;
import com.thaiopensource.xml.sax.CountingErrorHandler;
import com.thaiopensource.xml.sax.DelegatingContentHandler;
import com.thaiopensource.xml.sax.DraconianErrorHandler;

class ISOSchemaReaderImpl extends AbstractSchemaReader
{
  static final String SCHEMATRON_URI = "http://purl.oclc.org/dsdl/schematron";
  private static final String LOCATION_URI = "http://www.thaiopensource.com/ns/location";
  private static final String ERROR_URI = "http://www.thaiopensource.com/ns/error";
  private final Localizer localizer = new Localizer (ISOSchemaReaderImpl.class);

  private final Class <? extends SAXTransformerFactory> transformerFactoryClass;
  private final TransformerFactoryInitializer transformerFactoryInitializer;
  private final Templates schematron;
  private final Schema schematronSchema;
  private static final String SCHEMATRON_SCHEMA = "iso-schematron.rnc";
  private static final String SCHEMATRON_STYLESHEET = "iso-schematron.xsl";
  // XSLTC has some problems with extension functions and function-available, so
  // we need a separate stylesheet. See
  // https://issues.apache.org/jira/browse/XALANJ-2464
  // https://issues.apache.org/jira/browse/XALANJ-2465
  private static final String SCHEMATRON_XSLTC_STYLESHEET = "iso-schematron-xsltc.xsl";
  private static final PropertyId <?> [] supportedPropertyIds = { ValidateProperty.ERROR_HANDLER,
                                                                 ValidateProperty.XML_READER_CREATOR,
                                                                 ValidateProperty.ENTITY_RESOLVER,
                                                                 ValidateProperty.URI_RESOLVER,
                                                                 ValidateProperty.RESOLVER,
                                                                 SchematronProperty.DIAGNOSE,
                                                                 SchematronProperty.PHASE, };

  ISOSchemaReaderImpl (final SAXTransformerFactory transformerFactory,
                       final TransformerFactoryInitializer transformerFactoryInitializer) throws TransformerConfigurationException,
                                                                                         IncorrectSchemaException
  {
    this.transformerFactoryClass = transformerFactory.getClass ();
    this.transformerFactoryInitializer = transformerFactoryInitializer;
    final boolean isXsltc = isXsltc (transformerFactoryClass);
    final String stylesheet = isXsltc ? SCHEMATRON_XSLTC_STYLESHEET : SCHEMATRON_STYLESHEET;
    final String resourceName = fullResourceName (stylesheet);
    final StreamSource source = new StreamSource (getResourceAsStream (resourceName));
    initTransformerFactory (transformerFactory);
    schematron = transformerFactory.newTemplates (source);
    final InputSource schemaSource = new InputSource (getResourceAsStream (fullResourceName (SCHEMATRON_SCHEMA)));
    final PropertyMapBuilder builder = new PropertyMapBuilder ();
    builder.put (ValidateProperty.ERROR_HANDLER, new DraconianErrorHandler ());
    RngProperty.CHECK_ID_IDREF.add (builder);
    try
    {
      schematronSchema = CompactSchemaReader.getInstance ().createSchema (schemaSource, builder.toPropertyMap ());
    }
    catch (final SAXException e)
    {
      throw new IncorrectSchemaException ();
    }
    catch (final IOException e)
    {
      throw new IncorrectSchemaException ();
    }
  }

  static boolean isXsltc (final Class <? extends SAXTransformerFactory> cls)
  {
    return cls.getName ().indexOf (".xsltc.") >= 0;
  }

  public Option getOption (final String uri)
  {
    return SchematronProperty.getOption (uri);
  }

  private void initTransformerFactory (final TransformerFactory factory)
  {
    transformerFactoryInitializer.initTransformerFactory (factory);
  }

  static class UserException extends Exception
  {
    private final SAXException exception;

    UserException (final SAXException exception)
    {
      this.exception = exception;
    }

    SAXException getException ()
    {
      return exception;
    }
  }

  static class UserWrapErrorHandler extends CountingErrorHandler
  {
    UserWrapErrorHandler (final ErrorHandler errorHandler)
    {
      super (errorHandler);
    }

    @Override
    public void warning (final SAXParseException exception) throws SAXException
    {
      try
      {
        super.warning (exception);
      }
      catch (final SAXException e)
      {
        throw new SAXException (new UserException (e));
      }
    }

    @Override
    public void error (final SAXParseException exception) throws SAXException
    {
      try
      {
        super.error (exception);
      }
      catch (final SAXException e)
      {
        throw new SAXException (new UserException (e));
      }
    }

    @Override
    public void fatalError (final SAXParseException exception) throws SAXException
    {
      try
      {
        super.fatalError (exception);
      }
      catch (final SAXException e)
      {
        throw new SAXException (new UserException (e));
      }
    }
  }

  static class ErrorFilter extends DelegatingContentHandler
  {
    private final ErrorHandler eh;
    private final Localizer localizer;
    private Locator locator;

    ErrorFilter (final ContentHandler delegate, final ErrorHandler eh, final Localizer localizer)
    {
      super (delegate);
      this.eh = eh;
      this.localizer = localizer;
    }

    @Override
    public void setDocumentLocator (final Locator locator)
    {
      this.locator = locator;
      super.setDocumentLocator (locator);
    }

    @Override
    public void startElement (final String namespaceURI,
                              final String localName,
                              final String qName,
                              final Attributes atts) throws SAXException
    {
      if (namespaceURI.equals (ERROR_URI) && localName.equals ("error"))
        eh.error (new SAXParseException (localizer.message (atts.getValue ("", "message"), atts.getValue ("", "arg")),
                                         locator));
      super.startElement (namespaceURI, localName, qName, atts);
    }
  }

  static class LocationFilter extends DelegatingContentHandler implements Locator
  {
    private final String mainSystemId;
    private String systemId = null;
    private int lineNumber = -1;
    private int columnNumber = -1;
    private SAXException exception = null;

    LocationFilter (final ContentHandler delegate, final String systemId)
    {
      super (delegate);
      this.mainSystemId = systemId;
    }

    SAXException getException ()
    {
      return exception;
    }

    @Override
    public void setDocumentLocator (final Locator locator)
    {}

    @Override
    public void startDocument () throws SAXException
    {
      getDelegate ().setDocumentLocator (this);
      super.startDocument ();
    }

    @Override
    public void startElement (final String namespaceURI,
                              final String localName,
                              final String qName,
                              final Attributes atts) throws SAXException
    {
      systemId = getLocationAttribute (atts, "system-id");
      lineNumber = toInteger (getLocationAttribute (atts, "line-number"));
      columnNumber = toInteger (getLocationAttribute (atts, "column-number"));
      try
      {
        super.startElement (namespaceURI, localName, qName, atts);
      }
      catch (final SAXException e)
      {
        this.exception = e;
        setDelegate (null);
      }
      systemId = null;
      lineNumber = -1;
      columnNumber = -1;
    }

    private static String getLocationAttribute (final Attributes atts, final String name)
    {
      return atts.getValue (LOCATION_URI, name);
    }

    private static int toInteger (final String value)
    {
      if (value == null)
        return -1;
      try
      {
        return Integer.parseInt (value);
      }
      catch (final NumberFormatException e)
      {
        return -1;
      }
    }

    public String getPublicId ()
    {
      return null;
    }

    public String getSystemId ()
    {
      if (systemId != null && !systemId.equals (""))
        return systemId;
      return mainSystemId;
    }

    public int getLineNumber ()
    {
      return lineNumber;
    }

    public int getColumnNumber ()
    {
      return columnNumber;
    }
  }

  static class SAXErrorListener implements ErrorListener
  {
    private final ErrorHandler eh;
    private final String systemId;
    private boolean hadError = false;

    SAXErrorListener (final ErrorHandler eh, final String systemId)
    {
      this.eh = eh;
      this.systemId = systemId;
    }

    boolean getHadError ()
    {
      return hadError;
    }

    public void warning (final TransformerException exception) throws TransformerException
    {
      final SAXParseException spe = transform (exception);
      try
      {
        eh.warning (spe);
      }
      catch (final SAXException e)
      {
        throw new TransformerException (new UserException (e));
      }
    }

    public void error (final TransformerException exception) throws TransformerException
    {
      hadError = true;
      final SAXParseException spe = transform (exception);
      try
      {
        eh.error (spe);
      }
      catch (final SAXException e)
      {
        throw new TransformerException (new UserException (e));
      }
    }

    public void fatalError (final TransformerException exception) throws TransformerException
    {
      hadError = true;
      final SAXParseException spe = transform (exception);
      try
      {
        eh.fatalError (spe);
      }
      catch (final SAXException e)
      {
        throw new TransformerException (new UserException (e));
      }
    }

    SAXParseException transform (final TransformerException exception) throws TransformerException
    {
      final Throwable cause = exception.getException ();
      // Xalan takes it upon itself to catch exceptions and pass them to the
      // ErrorListener.
      if (cause instanceof RuntimeException)
        throw (RuntimeException) cause;
      if (cause instanceof SAXException || cause instanceof IncorrectSchemaException || cause instanceof IOException)
        throw exception;
      final SourceLocator locator = exception.getLocator ();
      if (locator == null)
        return new SAXParseException (exception.getMessage (), null);
      // Xalan sometimes loses the mainSystemId; work around this.
      String s = locator.getSystemId ();
      if (s == null)
        s = systemId;
      return new SAXParseException (exception.getMessage (),
                                    locator.getPublicId (),
                                    s,
                                    locator.getLineNumber (),
                                    locator.getColumnNumber ());
    }
  }

  // Minor problem is that
  // Saxon 6.5.2 prints to System.err in TemplatesHandlerImpl.getTemplates().

  public Schema createSchema (final SAXSource source, final PropertyMap properties) throws IOException,
                                                                                   SAXException,
                                                                                   IncorrectSchemaException
  {
    final ErrorHandler eh = properties.get (ValidateProperty.ERROR_HANDLER);
    final CountingErrorHandler ceh = new CountingErrorHandler (eh);
    final InputSource in = source.getInputSource ();
    final String systemId = in.getSystemId ();
    final IfValidHandler ifValidHandler = new IfValidHandler ();
    ifValidHandler.setErrorHandler (ceh);
    try
    {
      final SAXTransformerFactory factory = transformerFactoryClass.newInstance ();
      initTransformerFactory (factory);
      final TransformerHandler transformerHandler = factory.newTransformerHandler (schematron);
      ifValidHandler.setDelegate (transformerHandler);
      final Transformer transformer = transformerHandler.getTransformer ();
      final String phase = properties.get (SchematronProperty.PHASE);
      if (phase != null)
        transformer.setParameter ("phase", phase);
      final boolean diagnose = properties.contains (SchematronProperty.DIAGNOSE);
      if (diagnose)
        transformer.setParameter ("diagnose", Boolean.TRUE);
      final PropertyMapBuilder builder = new PropertyMapBuilder (properties);
      builder.put (ValidateProperty.ERROR_HANDLER, ifValidHandler);
      final Validator validator = schematronSchema.createValidator (builder.toPropertyMap ());
      ifValidHandler.setValidator (validator.getContentHandler ());
      XMLReader xr = source.getXMLReader ();
      if (xr == null)
        xr = ResolverFactory.createResolver (properties).createXMLReader ();
      xr.setContentHandler (ifValidHandler);
      xr.setDTDHandler (validator.getDTDHandler ()); // not strictly necessary
      factory.setErrorListener (new SAXErrorListener (ceh, systemId));
      final TemplatesHandler templatesHandler = factory.newTemplatesHandler ();
      templatesHandler.setSystemId (systemId);
      final LocationFilter stage2 = new LocationFilter (new ErrorFilter (templatesHandler, ceh, localizer), systemId);
      transformerHandler.setResult (new SAXResult (stage2));
      xr.setErrorHandler (ceh);
      xr.parse (in);
      final SAXException exception = stage2.getException ();
      if (exception != null)
        throw exception;
      if (ceh.getHadErrorOrFatalError ())
        throw new IncorrectSchemaException ();
      // Getting the templates can cause errors to be generated.
      final Templates templates = templatesHandler.getTemplates ();
      if (ceh.getHadErrorOrFatalError ())
        throw new IncorrectSchemaException ();
      return new SchemaImpl (templates, transformerFactoryClass, properties, supportedPropertyIds);
    }
    catch (final SAXException e)
    {
      throw cleanupSAXException (e);
    }
    catch (final TransformerConfigurationException e)
    {
      throw new SAXException (localizer.message ("unexpected_schema_creation_error"));
    }
    catch (final InstantiationException e)
    {
      throw new SAXException (e);
    }
    catch (final IllegalAccessException e)
    {
      throw new SAXException (e);
    }
  }

  private static String fullResourceName (final String name)
  {
    final String className = ISOSchemaReaderImpl.class.getName ();
    return className.substring (0, className.lastIndexOf ('.')).replace ('.', '/') + "/resources/" + name;
  }

  private static InputStream getResourceAsStream (final String resourceName)
  {
    final ClassLoader cl = ISOSchemaReaderImpl.class.getClassLoader ();
    // XXX see if we should borrow 1.2 code from Service
    if (cl == null)
      return ClassLoader.getSystemResourceAsStream (resourceName);
    else
      return cl.getResourceAsStream (resourceName);
  }

  private static SAXException cleanupSAXException (final SAXException saxException)
  {
    if (exceptionHasLocation (saxException))
      return saxException;
    final Exception exception = saxException.getException ();
    if (exception instanceof SAXException && exception.getMessage () == null)
      return cleanupSAXException ((SAXException) exception);
    if (exception instanceof TransformerException)
      return cleanupTransformerException ((TransformerException) exception);
    return saxException;
  }

  private static SAXException cleanupTransformerException (final TransformerException e)
  {
    String message = e.getMessage ();
    final Throwable cause = e.getException ();
    final SourceLocator transformLoc = e.getLocator ();
    // a TransformerException created with just a Throwable t argument
    // gets a message of t.toString()
    if (message != null && cause != null && message.equals (cause.toString ()))
      message = null;
    if (message == null && cause instanceof SAXException && transformLoc == null)
      return cleanupSAXException ((SAXException) cause);
    if (cause instanceof TransformerException && transformLoc == null)
      return cleanupTransformerException ((TransformerException) cause);
    Exception exception = null;
    if (cause instanceof Exception)
      exception = (Exception) cause;
    String publicId = null;
    String systemId = null;
    int lineNumber = -1;
    int columnNumber = -1;
    if (transformLoc != null)
    {
      publicId = transformLoc.getPublicId ();
      systemId = transformLoc.getSystemId ();
      lineNumber = transformLoc.getLineNumber ();
      columnNumber = transformLoc.getColumnNumber ();
    }
    if (publicId != null || systemId != null || lineNumber >= 0 || columnNumber >= 0)
      return new SAXParseException (message, publicId, systemId, lineNumber, columnNumber, exception);
    return new SAXException (message, exception);
  }

  private static boolean exceptionHasLocation (final SAXException saxException)
  {
    if (!(saxException instanceof SAXParseException))
      return false;
    final SAXParseException pe = (SAXParseException) saxException;
    return (pe.getPublicId () != null || pe.getSystemId () != null || pe.getLineNumber () >= 0 || pe.getColumnNumber () >= 0);
  }
}
