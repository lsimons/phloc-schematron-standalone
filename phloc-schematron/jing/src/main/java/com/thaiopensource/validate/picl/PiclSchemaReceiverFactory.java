package com.thaiopensource.validate.picl;

import com.thaiopensource.util.PropertyMap;
import com.thaiopensource.validate.Option;
import com.thaiopensource.validate.SchemaReader;
import com.thaiopensource.validate.auto.SchemaReceiver;
import com.thaiopensource.validate.auto.SchemaReceiverFactory;

public class PiclSchemaReceiverFactory implements SchemaReceiverFactory
{
  private static final String PICL_URI = SchemaReader.BASE_URI + "picl";

  public SchemaReceiver createSchemaReceiver (final String namespaceUri, final PropertyMap properties)
  {
    if (!PICL_URI.equals (namespaceUri))
      return null;
    return new SchemaReceiverImpl (properties);
  }

  public Option getOption (final String uri)
  {
    return null;
  }

}
