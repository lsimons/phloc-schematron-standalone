package com.thaiopensource.relaxng.input.parse.sax;

import javax.xml.transform.sax.SAXSource;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.thaiopensource.relaxng.edit.NameClass;
import com.thaiopensource.relaxng.edit.Pattern;
import com.thaiopensource.relaxng.edit.SourceLocation;
import com.thaiopensource.relaxng.input.parse.AnnotationsImpl;
import com.thaiopensource.relaxng.input.parse.CommentListImpl;
import com.thaiopensource.relaxng.input.parse.ElementAnnotationBuilderImpl;
import com.thaiopensource.relaxng.input.parse.ParseInputFormat;
import com.thaiopensource.relaxng.parse.Parseable;
import com.thaiopensource.relaxng.parse.sax.SAXParseable;
import com.thaiopensource.resolver.xml.sax.SAXResolver;

public class SAXParseInputFormat extends ParseInputFormat
{
  public SAXParseInputFormat ()
  {
    super (true);
  }

  @Override
  public Parseable <Pattern, NameClass, SourceLocation, ElementAnnotationBuilderImpl, CommentListImpl, AnnotationsImpl> makeParseable (final InputSource in,
                                                                                                                                       final SAXResolver resolver,
                                                                                                                                       final ErrorHandler eh) throws SAXException
  {
    return new SAXParseable <Pattern, NameClass, SourceLocation, ElementAnnotationBuilderImpl, CommentListImpl, AnnotationsImpl> (new SAXSource (resolver.createXMLReader (),
                                                                                                                                                 in),
                                                                                                                                  resolver,
                                                                                                                                  eh);
  }
}
