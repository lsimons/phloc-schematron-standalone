package com.thaiopensource.validate.auto;

import com.thaiopensource.util.PropertyMap;
import com.thaiopensource.validate.Option;
import com.thaiopensource.validate.SchemaReader;
import com.thaiopensource.validate.SchemaReaderFactory;

public class SchemaReaderFactorySchemaReceiverFactory implements SchemaReceiverFactory
{
  private final SchemaReaderFactory srf;

  public SchemaReaderFactorySchemaReceiverFactory (final SchemaReaderFactory srf)
  {
    this.srf = srf;
  }

  public SchemaReceiver createSchemaReceiver (final String namespaceUri, final PropertyMap properties)
  {
    final SchemaReader sr = srf.createSchemaReader (namespaceUri);
    if (sr == null)
      return null;
    return new SchemaReaderSchemaReceiver (sr, properties);
  }

  public Option getOption (final String uri)
  {
    return srf.getOption (uri);
  }
}
