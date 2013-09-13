package com.thaiopensource.resolver.xml.sax;

import org.xml.sax.XMLReader;

import com.thaiopensource.resolver.Input;

/**
 *
 */
public class SAXInput extends Input
{
  private XMLReader reader;

  public XMLReader getXMLReader ()
  {
    return reader;
  }

  public void setXMLReader (final XMLReader reader)
  {
    this.reader = reader;
  }
}
