package com.thaiopensource.validate.auto;

import com.thaiopensource.validate.SchemaReaderLoader;

public class SchemaReaderLoaderSchemaReceiverFactory extends SchemaReaderFactorySchemaReceiverFactory
{
  public SchemaReaderLoaderSchemaReceiverFactory ()
  {
    super (new SchemaReaderLoader ());
  }
}
