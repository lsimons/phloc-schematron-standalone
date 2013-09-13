package com.thaiopensource.validate.nrl;

import com.thaiopensource.util.PropertyMap;
import com.thaiopensource.validate.Option;
import com.thaiopensource.validate.auto.SchemaReceiver;
import com.thaiopensource.validate.auto.SchemaReceiverFactory;

public class NrlSchemaReceiverFactory implements SchemaReceiverFactory
{
  public SchemaReceiver createSchemaReceiver (final String namespaceUri, final PropertyMap properties)
  {
    if (!SchemaImpl.NRL_URI.equals (namespaceUri))
      return null;
    return new SchemaReceiverImpl (properties);
  }

  public Option getOption (final String uri)
  {
    return null;
  }
}
