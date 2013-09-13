package com.thaiopensource.validate.auto;

import java.io.IOException;

import javax.xml.transform.sax.SAXSource;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.thaiopensource.util.PropertyMap;
import com.thaiopensource.validate.IncorrectSchemaException;
import com.thaiopensource.validate.Schema;
import com.thaiopensource.validate.SchemaReader;

public class SchemaReaderSchemaReceiver implements SchemaReceiver
{
  private final SchemaReader schemaLanguage;
  private final PropertyMap properties;

  public SchemaReaderSchemaReceiver (final SchemaReader schemaLanguage, final PropertyMap properties)
  {
    this.schemaLanguage = schemaLanguage;
    this.properties = properties;
  }

  public SchemaFuture installHandlers (final XMLReader xr) throws SAXException
  {
    throw new ReparseException ()
    {
      @Override
      public Schema reparse (final SAXSource source) throws IncorrectSchemaException, SAXException, IOException
      {
        return schemaLanguage.createSchema (source, properties);
      }
    };
  }
}
