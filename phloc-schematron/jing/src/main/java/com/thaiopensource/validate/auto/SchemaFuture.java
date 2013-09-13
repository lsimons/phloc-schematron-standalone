package com.thaiopensource.validate.auto;

import java.io.IOException;

import org.xml.sax.SAXException;

import com.thaiopensource.validate.IncorrectSchemaException;
import com.thaiopensource.validate.Schema;

public interface SchemaFuture
{
  Schema getSchema () throws IncorrectSchemaException, SAXException, IOException;

  RuntimeException unwrapException (RuntimeException e) throws SAXException, IOException, IncorrectSchemaException;
}
