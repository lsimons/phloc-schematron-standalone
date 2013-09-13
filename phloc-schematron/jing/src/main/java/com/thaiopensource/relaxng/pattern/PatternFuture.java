package com.thaiopensource.relaxng.pattern;

import java.io.IOException;

import org.xml.sax.SAXException;

import com.thaiopensource.relaxng.parse.IllegalSchemaException;

public interface PatternFuture
{
  Pattern getPattern (boolean isAttributesPattern) throws IllegalSchemaException, SAXException, IOException;
}
