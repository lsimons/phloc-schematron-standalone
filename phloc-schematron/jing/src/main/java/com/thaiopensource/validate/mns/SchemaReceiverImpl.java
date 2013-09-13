package com.thaiopensource.validate.mns;

import java.io.IOException;
import java.net.URL;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.thaiopensource.util.PropertyMap;
import com.thaiopensource.util.PropertyMapBuilder;
import com.thaiopensource.validate.IncorrectSchemaException;
import com.thaiopensource.validate.Schema;
import com.thaiopensource.validate.SchemaReader;
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
  private static final String MNS_SCHEMA = "mns.rng";
  private static final String RNC_MEDIA_TYPE = "application/x-rnc";
  private final PropertyMap properties;
  private final PropertyMap attributeSchemaProperties;
  private final boolean attributesSchema;
  private final SchemaReader autoSchemaLanguage;
  private Schema mnsSchema = null;

  public SchemaReceiverImpl (final PropertyMap properties)
  {
    final Name attributeOwner = properties.get (WrapProperty.ATTRIBUTE_OWNER);
    attributesSchema = (attributeOwner != null);
    final PropertyMapBuilder builder = new PropertyMapBuilder (properties);
    if (ValidatorImpl.OWNER_NAME.equals (attributeOwner))
    {
      attributeSchemaProperties = properties;
      builder.put (WrapProperty.ATTRIBUTE_OWNER, null);
      this.properties = builder.toPropertyMap ();
    }
    else
    {
      if (attributeOwner == null)
        this.properties = properties;
      else
      {
        builder.put (WrapProperty.ATTRIBUTE_OWNER, null);
        this.properties = builder.toPropertyMap ();
      }
      builder.put (WrapProperty.ATTRIBUTE_OWNER, ValidatorImpl.OWNER_NAME);
      attributeSchemaProperties = builder.toPropertyMap ();
    }
    this.autoSchemaLanguage = new AutoSchemaReader (properties.get (SchemaReceiverFactory.PROPERTY));
  }

  public SchemaFuture installHandlers (final XMLReader xr)
  {
    return new SchemaImpl (attributesSchema).installHandlers (xr, this);
  }

  Schema getMnsSchema () throws IOException, IncorrectSchemaException, SAXException
  {
    if (mnsSchema == null)
    {
      final String className = SchemaReceiverImpl.class.getName ();
      final String resourceName = className.substring (0, className.lastIndexOf ('.')).replace ('.', '/') +
                                  "/resources/" +
                                  MNS_SCHEMA;
      final URL mnsSchemaUrl = getResource (resourceName);
      mnsSchema = SAXSchemaReader.getInstance ().createSchema (new InputSource (mnsSchemaUrl.toString ()), properties);
    }
    return mnsSchema;
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

  Schema createChildSchema (final InputSource inputSource, final String schemaType, final boolean isAttributesSchema) throws IOException,
                                                                                                                     IncorrectSchemaException,
                                                                                                                     SAXException
  {
    final SchemaReader lang = isRnc (schemaType) ? CompactSchemaReader.getInstance () : autoSchemaLanguage;
    return lang.createSchema (inputSource, isAttributesSchema ? attributeSchemaProperties : properties);
  }

  private static boolean isRnc (String schemaType)
  {
    if (schemaType == null)
      return false;
    schemaType = schemaType.trim ();
    return schemaType.equals (RNC_MEDIA_TYPE);
  }
}
