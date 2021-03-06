package com.thaiopensource.validate.mns;

import com.thaiopensource.util.PropertyMap;
import com.thaiopensource.validate.Option;
import com.thaiopensource.validate.auto.SchemaReceiver;
import com.thaiopensource.validate.auto.SchemaReceiverFactory;

public class MnsSchemaReceiverFactory implements SchemaReceiverFactory
{
  public SchemaReceiver createSchemaReceiver (final String namespaceUri, final PropertyMap properties)
  {
    if (!SchemaImpl.MNS_URI.equals (namespaceUri))
      return null;
    return new SchemaReceiverImpl (properties);
  }

  public Option getOption (final String uri)
  {
    return null;
  }
}
