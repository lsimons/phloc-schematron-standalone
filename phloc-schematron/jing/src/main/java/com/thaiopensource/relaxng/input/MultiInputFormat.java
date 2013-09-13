package com.thaiopensource.relaxng.input;

import java.io.IOException;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import com.thaiopensource.relaxng.edit.SchemaCollection;
import com.thaiopensource.relaxng.translate.util.InvalidParamsException;
import com.thaiopensource.resolver.Resolver;

public interface MultiInputFormat extends InputFormat
{
  SchemaCollection load (String [] uris, String [] params, String outputFormat, ErrorHandler eh, Resolver resolver) throws InputFailedException,
                                                                                                                   InvalidParamsException,
                                                                                                                   IOException,
                                                                                                                   SAXException;
}
