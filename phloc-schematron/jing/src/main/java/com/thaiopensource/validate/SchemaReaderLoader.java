package com.thaiopensource.validate;

import java.util.Iterator;

import com.thaiopensource.util.Service;

/**
 * A SchemaReaderFactory that automatically discovers SchemaReader
 * implementations. For a SchemeaReader implementation to be discoverable by
 * this class, it must have a factory class with a no-argument constructor
 * implementing SchemaReaderFactory, and the fully-qualified name of this
 * factory class must be listed in the file
 * <code>META-INF/services/com.thaiopensource.validate.SchemaReaderFactory</code>
 * .
 */
public class SchemaReaderLoader implements SchemaReaderFactory
{
  private final Service <SchemaReaderFactory> service = Service.newInstance (SchemaReaderFactory.class);

  public SchemaReader createSchemaReader (final String namespaceUri)
  {
    for (final Iterator <SchemaReaderFactory> iter = service.getProviders (); iter.hasNext ();)
    {
      final SchemaReaderFactory srf = iter.next ();
      final SchemaReader sr = srf.createSchemaReader (namespaceUri);
      if (sr != null)
        return sr;
    }
    return null;
  }

  public Option getOption (final String uri)
  {
    for (final Iterator <SchemaReaderFactory> iter = service.getProviders (); iter.hasNext ();)
    {
      final SchemaReaderFactory srf = iter.next ();
      final Option option = srf.getOption (uri);
      if (option != null)
        return option;
    }
    return null;
  }
}
