package com.thaiopensource.validate.auto;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public interface SchemaReceiver
{
  SchemaFuture installHandlers (XMLReader xr) throws SAXException;
}
