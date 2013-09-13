package com.thaiopensource.relaxng.input;

import java.io.IOException;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import com.thaiopensource.relaxng.edit.SchemaCollection;
import com.thaiopensource.relaxng.translate.util.InvalidParamsException;
import com.thaiopensource.resolver.Resolver;

abstract public class AbstractMultiInputFormat implements MultiInputFormat
{
  public SchemaCollection load (final String uri,
                                final String [] params,
                                final String outputFormat,
                                final ErrorHandler eh,
                                final Resolver resolver) throws InputFailedException,
                                                        InvalidParamsException,
                                                        IOException,
                                                        SAXException
  {
    return load (new String [] { uri }, params, outputFormat, eh, resolver);
  }
}
