package com.thaiopensource.validate.nvdl;

import java.io.IOException;
import java.io.InputStream;
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

/**
 * Schema receiver implementation for NVDL scripts.
 */
class SchemaReceiverImpl implements SchemaReceiver
{
  /**
   * Relax NG schema for NVDL scripts.
   */
  private static final String NVDL_SCHEMA = "nvdl.rng";
  /**
   * The type used for specifying RNC schemas.
   */
  private static final String RNC_MEDIA_TYPE = "application/relax-ng-compact-syntax";

  /**
   * Legacy type used for specifying RNC schemas.
   */
  static final String LEGACY_RNC_MEDIA_TYPE = "application/x-rnc";

  /**
   * Properties.
   */
  private final PropertyMap properties;

  /**
   * Property indicating if we need to check only attributes, that means the
   * root element is just a placeholder for the attributes.
   */
  private final Name attributeOwner;

  /**
   * The schema reader capable of parsing the input schema file. It will be an
   * auto schema reader as NVDL is XML.
   */
  private final SchemaReader autoSchemaReader;

  /**
   * Schema object created by this schema receiver.
   */
  private Schema nvdlSchema = null;

  /**
   * Properties that will be passed to sub-schemas.
   */
  private static final PropertyId subSchemaProperties[] = { ValidateProperty.ERROR_HANDLER,
                                                           ValidateProperty.XML_READER_CREATOR,
                                                           ValidateProperty.ENTITY_RESOLVER,
                                                           ValidateProperty.URI_RESOLVER,
                                                           ValidateProperty.RESOLVER,
                                                           SchemaReceiverFactory.PROPERTY, };

  /**
   * Creates a schema receiver for NVDL schemas.
   * 
   * @param properties
   *        Properties.
   */
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

  Schema getNvdlSchema () throws IOException, IncorrectSchemaException, SAXException
  {
    if (nvdlSchema == null)
    {
      final String className = SchemaReceiverImpl.class.getName ();
      final String resourceName = className.substring (0, className.lastIndexOf ('.')).replace ('.', '/') +
                                  "/resources/" +
                                  NVDL_SCHEMA;
      final URL nvdlSchemaUrl = getResource (resourceName);
      final InputStream stream = nvdlSchemaUrl.openStream ();
      // this is just to ensure that there aren't any problems with the parser
      // opening the schema resource
      final InputSource inputSource = new InputSource (nvdlSchemaUrl.toString ());
      inputSource.setByteStream (stream);
      nvdlSchema = SAXSchemaReader.getInstance ().createSchema (inputSource, properties);
    }
    return nvdlSchema;
  }

  /**
   * Get a resource using this class class loader.
   * 
   * @param resourceName
   *        the resource.
   * @return An URL pointing to the resource.
   */
  private static URL getResource (final String resourceName)
  {
    final ClassLoader cl = SchemaReceiverImpl.class.getClassLoader ();
    // XXX see if we should borrow 1.2 code from Service
    if (cl == null)
      return ClassLoader.getSystemResource (resourceName);
    else
      return cl.getResource (resourceName);
  }

  /**
   * Get the properties.
   * 
   * @return a PropertyMap.
   */
  PropertyMap getProperties ()
  {
    return properties;
  }

  /**
   * Creates a child schema. This schema is referred in a validate action.
   * 
   * @param source
   *        the SAXSource for the schema.
   * @param schemaType
   *        the schema type.
   * @param options
   *        options specified for this schema in the NVDL script.
   * @param isAttributesSchema
   *        flag indicating if the schema should be modified to check attributes
   *        only.
   * @return
   * @throws IOException
   *         In case of IO problems.
   * @throws IncorrectSchemaException
   *         In case of invalid schema.
   * @throws SAXException
   *         In case if XML problems while creating the schema.
   */
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

  /**
   * Get an option for the given URI.
   * 
   * @param uri
   *        The URI for an option.
   * @return Either the option from the auto schema reader or from the compact
   *         schema reader.
   */
  Option getOption (final String uri)
  {
    final Option option = autoSchemaReader.getOption (uri);
    if (option != null)
      return option;
    return CompactSchemaReader.getInstance ().getOption (uri);
  }

  /**
   * Checks is a schema type is RNC.
   * 
   * @param schemaType
   *        The schema type specification.
   * @return true if the schema type refers to a RNC schema.
   */
  private static boolean isRnc (String schemaType)
  {
    if (schemaType == null)
      return false;
    schemaType = schemaType.trim ();
    return schemaType.equals (RNC_MEDIA_TYPE) || schemaType.equals (LEGACY_RNC_MEDIA_TYPE);
  }
}
