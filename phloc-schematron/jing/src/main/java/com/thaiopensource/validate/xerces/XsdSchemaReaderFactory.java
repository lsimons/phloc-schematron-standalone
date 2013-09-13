package com.thaiopensource.validate.xerces;

import org.apache.xerces.parsers.XMLGrammarPreparser;

import com.thaiopensource.validate.Option;
import com.thaiopensource.validate.SchemaReader;
import com.thaiopensource.validate.SchemaReaderFactory;
import com.thaiopensource.xml.util.WellKnownNamespaces;

public class XsdSchemaReaderFactory implements SchemaReaderFactory
{
  public XsdSchemaReaderFactory ()
  {
    // Force a linkage error if Xerces is not available
    new XMLGrammarPreparser ();
  }

  public SchemaReader createSchemaReader (final String namespaceUri)
  {
    if (WellKnownNamespaces.XML_SCHEMA.equals (namespaceUri))
      return new SchemaReaderImpl ();
    return null;
  }

  public Option getOption (final String uri)
  {
    return null;
  }
}
