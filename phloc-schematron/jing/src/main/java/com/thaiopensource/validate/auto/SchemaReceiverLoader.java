package com.thaiopensource.validate.auto;

import java.util.Iterator;

import com.thaiopensource.util.PropertyMap;
import com.thaiopensource.util.Service;
import com.thaiopensource.validate.Option;

public class SchemaReceiverLoader implements SchemaReceiverFactory
{
  private final Service <SchemaReceiverFactory> service = Service.newInstance (SchemaReceiverFactory.class);

  public SchemaReceiver createSchemaReceiver (final String namespaceUri, final PropertyMap properties)
  {
    for (final Iterator <SchemaReceiverFactory> iter = service.getProviders (); iter.hasNext ();)
    {
      final SchemaReceiverFactory srf = iter.next ();
      final SchemaReceiver sr = srf.createSchemaReceiver (namespaceUri, properties);
      if (sr != null)
        return sr;
    }
    return null;
  }

  public Option getOption (final String uri)
  {
    for (final Iterator <SchemaReceiverFactory> iter = service.getProviders (); iter.hasNext ();)
    {
      final SchemaReceiverFactory srf = iter.next ();
      final Option option = srf.getOption (uri);
      if (option != null)
        return option;
    }
    return null;
  }

}
