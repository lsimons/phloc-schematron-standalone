package com.thaiopensource.relaxng.jaxp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.validation.ValidatorHandler;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;

import com.thaiopensource.validation.LSInputImpl;
import com.thaiopensource.validation.SchemaFactory2;
import com.thaiopensource.xml.sax.DraconianErrorHandler;

/**
 * Test SchemaFactoryImpl.
 */
public abstract class SchemaFactoryImplTest
{
  protected final Class <? extends SchemaFactory2> factoryClass;
  private static int filenameIndex = 0;

  protected SchemaFactoryImplTest (final Class <? extends SchemaFactory2> factoryClass)
  {
    this.factoryClass = factoryClass;
  }

  protected SchemaFactory2 factory ()
  {
    try
    {
      return factoryClass.newInstance ();
    }
    catch (final InstantiationException e)
    {}
    catch (final IllegalAccessException e)
    {}
    throw new AssertionError ();
  }

  @Test (dataProvider = "valid")
  public void testValidCharStream (final String schemaString, final String docString) throws SAXException, IOException
  {
    factory ().newSchema (charStreamSource (schemaString)).newValidator ().validate (charStreamSource (docString));
  }

  @Test (dataProvider = "valid")
  public void testValidFile (final String schemaString, final String docString) throws SAXException, IOException
  {
    factory ().newSchema (fileSource (schemaString)).newValidator ().validate (fileSource (docString));
  }

  @DataProvider (name = "valid")
  protected Object [][] valid ()
  {
    return new Object [] [] { { createSchema ("doc"), "<doc/>" },
                             { element ("doc", new String [] { attribute ("att") }), "<doc att='val'/>" } };
  }

  private static SAXSource charStreamSource (final String s)
  {
    return new SAXSource (new InputSource (new StringReader (s)));
  }

  private static synchronized Source fileSource (final String s) throws IOException
  {
    final File file = new File ("t" + filenameIndex++);
    final Writer w = new OutputStreamWriter (new FileOutputStream (file), "UTF-8");
    w.write (s);
    w.close ();
    return new StreamSource (file);
  }

  static private class CountErrorHandler extends DraconianErrorHandler
  {
    int errorCount = 0;

    @Override
    public void error (final SAXParseException e) throws SAXException
    {
      ++errorCount;
    }
  }

  @Test
  public void testErrorHandlerNoThrow () throws SAXException, IOException
  {
    final SchemaFactory f = factory ();
    final Validator v = f.newSchema (charStreamSource (createSchema ("doc"))).newValidator ();
    final CountErrorHandler eh = new CountErrorHandler ()
    {
      @Override
      public void error (final SAXParseException e) throws SAXException
      {
        if (errorCount == 0)
          Assert.assertEquals (e.getLineNumber (), 2);
        super.error (e);
      }
    };
    v.setErrorHandler (eh);
    Assert.assertSame (v.getErrorHandler (), eh);
    v.validate (charStreamSource ("<doc>\n<bad/></doc>"));
    Assert.assertTrue (eh.errorCount > 0);
  }

  @Test (expectedExceptions = { RuntimeException.class })
  public void testErrorHandlerThrowRuntime () throws SAXException, IOException
  {
    final SchemaFactory f = factory ();
    final Validator v = f.newSchema (charStreamSource (createSchema ("doc"))).newValidator ();
    v.setErrorHandler (new DraconianErrorHandler ()
    {
      @Override
      public void error (final SAXParseException e) throws SAXException
      {
        Assert.assertEquals (e.getLineNumber (), 2);
        throw new RuntimeException ();
      }
    });
    v.validate (charStreamSource ("<doc>\n<bad/></doc>"));
    throw new AssertionError ();
  }

  static class MySAXException extends SAXException
  {}

  @Test (expectedExceptions = { MySAXException.class })
  public void testErrorHandlerThrowSAX () throws SAXException, IOException
  {
    final SchemaFactory f = factory ();
    final Validator v = f.newSchema (charStreamSource (createSchema ("doc"))).newValidator ();
    v.setErrorHandler (new DraconianErrorHandler ()
    {
      @Override
      public void error (final SAXParseException e) throws SAXException
      {
        Assert.assertEquals (e.getLineNumber (), 2);
        throw new MySAXException ();
      }
    });
    v.validate (charStreamSource ("<doc>\n<bad/></doc>"));
    throw new AssertionError ();
  }

  @Test
  public void testInstanceResourceResolver () throws SAXException, IOException
  {
    final SchemaFactory f = factory ();
    final Validator v = f.newSchema (charStreamSource (element ("doc", element ("inner")))).newValidator ();
    Assert.assertNull (v.getResourceResolver ());
    final LSResourceResolver rr = new LSResourceResolver ()
    {
      public LSInput resolveResource (final String type,
                                      final String namespaceURI,
                                      final String publicId,
                                      String systemId,
                                      final String baseURI)
      {
        // In Java 5 Xerces absolutized the systemId relative to the current
        // directory
        final int slashIndex = systemId.lastIndexOf ('/');
        if (slashIndex >= 0)
          systemId = systemId.substring (slashIndex + 1);
        Assert.assertEquals (systemId, "e.xml");
        Assert.assertEquals (type, "http://www.w3.org/TR/REC-xml");
        final LSInput in = new LSInputImpl ();
        in.setStringData ("<inner/>");
        return in;
      }
    };
    v.setResourceResolver (rr);
    Assert.assertSame (v.getResourceResolver (), rr);
    v.validate (charStreamSource ("<!DOCTYPE doc [ <!ENTITY e SYSTEM 'e.xml'> ]><doc>&e;</doc>"));
  }

  @Test
  public void testSchemaResourceResolver () throws SAXException, IOException
  {
    final SchemaFactory f = factory ();
    Assert.assertNull (f.getResourceResolver ());
    final LSResourceResolver rr = new LSResourceResolver ()
    {
      public LSInput resolveResource (final String type,
                                      final String namespaceURI,
                                      final String publicId,
                                      final String systemId,
                                      final String baseURI)
      {
        Assert.assertEquals (systemId, "myschema");
        Assert.assertEquals (type, getLSType ());
        Assert.assertNull (baseURI);
        Assert.assertNull (namespaceURI);
        Assert.assertNull (publicId);
        final LSInput in = new LSInputImpl ();
        in.setStringData (createSchema ("doc"));
        return in;
      }
    };
    f.setResourceResolver (rr);
    Assert.assertSame (f.getResourceResolver (), rr);
    final Validator v = f.newSchema (charStreamSource (externalRef ("myschema"))).newValidator ();
    v.validate (charStreamSource ("<doc/>"));
  }

  @Test (expectedExceptions = { UnsupportedOperationException.class })
  public void testNewSchemaNoArgs () throws SAXException
  {
    factory ().newSchema ();
  }

  @DataProvider (name = "supportedFeatures")
  Object [][] createSupportedFeatures ()
  {
    return new Object [] [] { { XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.FALSE } };
  }

  @Test (dataProvider = "supportedFeatures")
  public void testSupportedFeatures (final String feature, final Boolean defaultValueObj) throws SAXNotRecognizedException,
                                                                                         SAXNotSupportedException
  {
    final SchemaFactory f = factory ();
    final boolean defaultValue = defaultValueObj.booleanValue ();
    Assert.assertEquals (f.getFeature (XMLConstants.FEATURE_SECURE_PROCESSING), defaultValue);
    f.setFeature (XMLConstants.FEATURE_SECURE_PROCESSING, !defaultValue);
    Assert.assertEquals (f.getFeature (XMLConstants.FEATURE_SECURE_PROCESSING), !defaultValue);
  }

  @Test (dataProvider = "supportedFeatures")
  public void testFeatureInheritance (final String feature, final Boolean defaultValueObj) throws SAXException,
                                                                                          SAXNotRecognizedException,
                                                                                          SAXNotSupportedException
  {
    final SchemaFactory f = factory ();
    final boolean defaultValue = defaultValueObj.booleanValue ();
    Assert.assertEquals (f.getFeature (feature), defaultValue);
    f.setFeature (feature, !defaultValue);
    final ValidatorHandler vh = f.newSchema (charStreamSource (createSchema ("doc"))).newValidatorHandler ();
    // the docs say that only properties are inherited by the ValidatorHandler
    Assert.assertEquals (vh.getFeature (feature), defaultValue);
  }

  @Test (expectedExceptions = { SAXNotRecognizedException.class })
  public void testUnrecognizedGetFeature () throws SAXNotRecognizedException, SAXNotSupportedException
  {
    final SchemaFactory f = factory ();
    f.getFeature ("http://thaiopensource.com/features/no-such-feature");
    throw new AssertionError ();
  }

  @Test (expectedExceptions = { SAXNotRecognizedException.class })
  public void testUnrecognizedSetFeature () throws SAXNotRecognizedException, SAXNotSupportedException
  {
    final SchemaFactory f = factory ();
    f.setFeature ("http://thaiopensource.com/features/no-such-feature", false);
    throw new AssertionError ();
  }

  @Test (expectedExceptions = { NullPointerException.class })
  public void testNullGetFeature () throws SAXNotRecognizedException, SAXNotSupportedException
  {
    final SchemaFactory f = factory ();
    f.getFeature (null);
    throw new AssertionError ();
  }

  @Test (expectedExceptions = { NullPointerException.class })
  public void testNullSetFeature () throws SAXNotRecognizedException, SAXNotSupportedException
  {
    final SchemaFactory f = factory ();
    f.setFeature (null, true);
    throw new AssertionError ();
  }

  @Test (expectedExceptions = { SAXNotRecognizedException.class })
  public void testUnrecognizedSetProperty () throws SAXNotRecognizedException, SAXNotSupportedException
  {
    final SchemaFactory f = factory ();
    f.setProperty ("http://thaiopensource.com/properties-no-such-property", null);
    throw new AssertionError ();
  }

  @Test (expectedExceptions = { SAXNotRecognizedException.class })
  public void testUnrecognizedGetProperty () throws SAXNotRecognizedException, SAXNotSupportedException
  {
    final SchemaFactory f = factory ();
    f.getProperty ("http://thaiopensource.com/properties/no-such-property");
    throw new AssertionError ();
  }

  private String createSchema (final String rootElement)
  {
    return element (rootElement);
  }

  private String element (final String name)
  {
    return element (name, new String [] {});
  }

  private String element (final String name, final String contentPattern)
  {
    return element (name, new String [] { contentPattern });
  }

  abstract protected String element (String name, String [] contentPatterns);

  abstract protected String attribute (String name);

  abstract protected String externalRef (String uri);

  abstract protected String getLSType ();
}
