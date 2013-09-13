package com.thaiopensource.validate.nrl;

import java.io.IOException;
import java.net.URL;

import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.thaiopensource.util.PropertyId;
import com.thaiopensource.util.PropertyMap;
import com.thaiopensource.util.PropertyMapBuilder;
import com.thaiopensource.validate.IncorrectSchemaException;
import com.thaiopensource.validate.Option;
import com.thaiopensource.validate.Schema;
import com.thaiopensource.validate.SchemaReader;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.validate.auto.AutoSchemaReader;
import com.thaiopensource.validate.auto.SchemaFuture;
import com.thaiopensource.validate.auto.SchemaReceiver;
import com.thaiopensource.validate.auto.SchemaReceiverFactory;
import com.thaiopensource.validate.prop.wrap.WrapProperty;
import com.thaiopensource.validate.rng.CompactSchemaReader;
import com.thaiopensource.validate.rng.SAXSchemaReader;
import com.thaiopensource.xml.util.Name;

class SchemaReceiverImpl implements SchemaReceiver
{
  private static final String NRL_SCHEMA = "nrl.rng";
  private static final String RNC_MEDIA_TYPE = "application/x-rnc";
  private final PropertyMap properties;
  private final Name attributeOwner;
  private final SchemaReader autoSchemaReader;
  private Schema nrlSchema = null;
  private static final PropertyId subSchemaProperties[] = { ValidateProperty.ERROR_HANDLER,
                                                           ValidateProperty.XML_READER_CREATOR,
                                                           ValidateProperty.ENTITY_RESOLVER,
                                                           SchemaReceiverFactory.PROPERTY, };

  public SchemaReceiverImpl (final PropertyMap properties)
  {
    this.attributeOwner = properties.get (WrapProperty.ATTRIBUTE_OWNER);
    final PropertyMapBuilder builder = new PropertyMapBuilder ();
    for (final PropertyId subSchemaPropertie : subSchemaProperties)
    {
      final Object value = properties.get (subSchemaPropertie);
      if (value != null)
        builder.put (subSchemaPropertie, value);
    }
    this.properties = builder.toPropertyMap ();
    this.autoSchemaReader = new AutoSchemaReader (properties.get (SchemaReceiverFactory.PROPERTY));
  }

  public SchemaFuture installHandlers (final XMLReader xr)
  {
    final PropertyMapBuilder builder = new PropertyMapBuilder (properties);
    if (attributeOwner != null)
      builder.put (WrapProperty.ATTRIBUTE_OWNER, attributeOwner);
    return new SchemaImpl (builder.toPropertyMap ()).installHandlers (xr, this);
  }

  Schema getNrlSchema () throws IOException, IncorrectSchemaException, SAXException
  {
    if (nrlSchema == null)
    {
      final String className = SchemaReceiverImpl.class.getName ();
      final String resourceName = className.substring (0, className.lastIndexOf ('.')).replace ('.', '/') +
                                  "/resources/" +
                                  NRL_SCHEMA;
      final URL nrlSchemaUrl = getResource (resourceName);
      nrlSchema = SAXSchemaReader.getInstance ()
                                 .createSchema (new InputSource (nrlSchemaUrl.openStream ()), properties);
    }
    return nrlSchema;
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

  PropertyMap getProperties ()
  {
    return properties;
  }

  Schema createChildSchema (final SAXSource source,
                            final String schemaType,
                            final PropertyMap options,
                            final boolean isAttributesSchema) throws IOException,
                                                             IncorrectSchemaException,
                                                             SAXException
  {
    final SchemaReader reader = isRnc (schemaType) ? CompactSchemaReader.getInstance () : autoSchemaReader;
    final PropertyMapBuilder builder = new PropertyMapBuilder (properties);
    if (isAttributesSchema)
      builder.put (WrapProperty.ATTRIBUTE_OWNER, ValidatorImpl.OWNER_NAME);
    builder.add (options);
    return reader.createSchema (source, builder.toPropertyMap ());
  }

  Option getOption (final String uri)
  {
    final Option option = autoSchemaReader.getOption (uri);
    if (option != null)
      return option;
    return CompactSchemaReader.getInstance ().getOption (uri);
  }

  private static boolean isRnc (String schemaType)
  {
    if (schemaType == null)
      return false;
    schemaType = schemaType.trim ();
    return schemaType.equals (RNC_MEDIA_TYPE);
  }
}
