package com.thaiopensource.validate.picl;

import java.io.IOException;
import java.net.URL;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.thaiopensource.util.PropertyMap;
import com.thaiopensource.util.SinglePropertyMap;
import com.thaiopensource.validate.IncorrectSchemaException;
import com.thaiopensource.validate.Schema;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.validate.auto.SchemaFuture;
import com.thaiopensource.validate.auto.SchemaReceiver;
import com.thaiopensource.validate.rng.CompactSchemaReader;

class SchemaReceiverImpl implements SchemaReceiver
{
  private final String PICL_SCHEMA = "picl.rnc";
  private Schema piclSchema = null;
  private final PropertyMap properties;

  SchemaReceiverImpl (final PropertyMap properties)
  {
    this.properties = SinglePropertyMap.newInstance (ValidateProperty.ERROR_HANDLER,
                                                     properties.get (ValidateProperty.ERROR_HANDLER));
  }

  public SchemaFuture installHandlers (final XMLReader xr) throws SAXException
  {
    final SchemaParser parser = new SchemaParser (properties, getPiclSchema ());
    xr.setContentHandler (parser);
    return parser;
  }

  private Schema getPiclSchema () throws SAXException
  {
    if (piclSchema == null)
    {
      final String className = SchemaReceiverImpl.class.getName ();
      final String resourceName = className.substring (0, className.lastIndexOf ('.')).replace ('.', '/') +
                                  "/resources/" +
                                  PICL_SCHEMA;
      final URL nrlSchemaUrl = getResource (resourceName);
      try
      {
        piclSchema = CompactSchemaReader.getInstance ().createSchema (new InputSource (nrlSchemaUrl.toString ()),
                                                                      properties);
      }
      catch (final IncorrectSchemaException e)
      {
        throw new SAXException ("unexpected internal error in RNC schema for picl");
      }
      catch (final IOException e)
      {
        throw new SAXException (e);
      }
    }
    return piclSchema;
  }

  private static URL getResource (final String resourceName)
  {
    final ClassLoader cl = SchemaReceiverImpl.class.getClassLoader ();
    // XXX see if we should borrow 1.2 code from Service
    if (cl == null)
      return ClassLoader.getSystemResource (resourceName);
    else
      return cl.getResource (resourceName);
  }

}
