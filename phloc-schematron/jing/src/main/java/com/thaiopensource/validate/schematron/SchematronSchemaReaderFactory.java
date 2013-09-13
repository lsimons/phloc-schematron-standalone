package com.thaiopensource.validate.schematron;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXTransformerFactory;

import com.thaiopensource.validate.IncorrectSchemaException;
import com.thaiopensource.validate.Option;
import com.thaiopensource.validate.SchemaReader;
import com.thaiopensource.validate.SchemaReaderFactory;
import com.thaiopensource.validate.prop.schematron.SchematronProperty;

public class SchematronSchemaReaderFactory implements SchemaReaderFactory, TransformerFactoryInitializer
{
  public SchemaReader createSchemaReader (final String namespaceUri)
  {
    if (namespaceUri.equals (ISOSchemaReaderImpl.SCHEMATRON_URI))
    {
      try
      {
        return new ISOSchemaReaderImpl (newTransformerFactory (), this);
      }
      catch (final TransformerFactoryConfigurationError e)
      {}
      catch (final IncorrectSchemaException e)
      {}
      catch (final TransformerConfigurationException e)
      {}
    }
    else
      if (namespaceUri.equals (SchemaReaderImpl.SCHEMATRON_URI))
      {
        try
        {
          return new SchemaReaderImpl (newTransformerFactory (), this);
        }
        catch (final TransformerFactoryConfigurationError e)
        {}
        catch (final IncorrectSchemaException e)
        {}
        catch (final TransformerConfigurationException e)
        {}
      }
    return null;
  }

  public Option getOption (final String uri)
  {
    return SchematronProperty.getOption (uri);
  }

  public SAXTransformerFactory newTransformerFactory ()
  {
    final TransformerFactory factory = TransformerFactory.newInstance ();
    if (factory.getFeature (SAXTransformerFactory.FEATURE))
      return (SAXTransformerFactory) factory;
    throw new TransformerFactoryConfigurationError ("JAXP TransformerFactory must support SAXTransformerFactory feature");
  }

  public void initTransformerFactory (final TransformerFactory factory)
  {}
}
