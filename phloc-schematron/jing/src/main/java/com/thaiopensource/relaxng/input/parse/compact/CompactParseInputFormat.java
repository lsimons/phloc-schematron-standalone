package com.thaiopensource.relaxng.input.parse.compact;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;

import com.thaiopensource.relaxng.edit.NameClass;
import com.thaiopensource.relaxng.edit.Pattern;
import com.thaiopensource.relaxng.edit.SourceLocation;
import com.thaiopensource.relaxng.input.parse.AnnotationsImpl;
import com.thaiopensource.relaxng.input.parse.CommentListImpl;
import com.thaiopensource.relaxng.input.parse.ElementAnnotationBuilderImpl;
import com.thaiopensource.relaxng.input.parse.ParseInputFormat;
import com.thaiopensource.relaxng.parse.Parseable;
import com.thaiopensource.relaxng.parse.compact.CompactParseable;
import com.thaiopensource.resolver.xml.sax.SAX;
import com.thaiopensource.resolver.xml.sax.SAXResolver;

public class CompactParseInputFormat extends ParseInputFormat
{
  public CompactParseInputFormat ()
  {
    super (false);
  }

  @Override
  public Parseable <Pattern, NameClass, SourceLocation, ElementAnnotationBuilderImpl, CommentListImpl, AnnotationsImpl> makeParseable (final InputSource inputSource,
                                                                                                                                       final SAXResolver saxResolver,
                                                                                                                                       final ErrorHandler eh)
  {
    return new CompactParseable <Pattern, NameClass, SourceLocation, ElementAnnotationBuilderImpl, CommentListImpl, AnnotationsImpl> (SAX.createInput (inputSource),
                                                                                                                                      saxResolver.getResolver (),
                                                                                                                                      eh);
  }
}
