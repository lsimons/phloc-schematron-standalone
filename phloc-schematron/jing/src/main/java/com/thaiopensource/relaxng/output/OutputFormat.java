package com.thaiopensource.relaxng.output;

import java.io.IOException;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import com.thaiopensource.relaxng.edit.SchemaCollection;
import com.thaiopensource.relaxng.translate.util.InvalidParamsException;

public interface OutputFormat
{
  void output (SchemaCollection sc, OutputDirectory od, String [] params, String inputFormat, ErrorHandler eh) throws SAXException,
                                                                                                              IOException,
                                                                                                              OutputFailedException,
                                                                                                              InvalidParamsException;
}
