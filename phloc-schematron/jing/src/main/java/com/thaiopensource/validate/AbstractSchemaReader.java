package com.thaiopensource.validate;

import java.io.IOException;

import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.thaiopensource.util.PropertyMap;

public abstract class AbstractSchemaReader implements SchemaReader
{
  public Schema createSchema (final InputSource in, final PropertyMap properties) throws IOException,
                                                                                 SAXException,
                                                                                 IncorrectSchemaException
  {
    return createSchema (new SAXSource (in), properties);
  }
}
