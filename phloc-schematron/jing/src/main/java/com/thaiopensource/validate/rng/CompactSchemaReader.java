package com.thaiopensource.validate.rng;

import javax.xml.transform.sax.SAXSource;

import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;

import com.thaiopensource.relaxng.parse.Parseable;
import com.thaiopensource.relaxng.parse.compact.CompactParseable;
import com.thaiopensource.relaxng.pattern.AnnotationsImpl;
import com.thaiopensource.relaxng.pattern.CommentListImpl;
import com.thaiopensource.relaxng.pattern.NameClass;
import com.thaiopensource.relaxng.pattern.Pattern;
import com.thaiopensource.resolver.xml.sax.SAX;
import com.thaiopensource.resolver.xml.sax.SAXResolver;
import com.thaiopensource.util.PropertyMap;
import com.thaiopensource.util.VoidValue;
import com.thaiopensource.validate.SchemaReader;
import com.thaiopensource.validate.rng.impl.SchemaReaderImpl;

public class CompactSchemaReader extends SchemaReaderImpl
{
  private static final SchemaReader theInstance = new CompactSchemaReader ();

  private CompactSchemaReader ()
  {}

  public static SchemaReader getInstance ()
  {
    return theInstance;
  }

  @Override
  protected Parseable <Pattern, NameClass, Locator, VoidValue, CommentListImpl, AnnotationsImpl> createParseable (final SAXSource source,
                                                                                                                  final SAXResolver saxResolver,
                                                                                                                  final ErrorHandler eh,
                                                                                                                  final PropertyMap properties)
  {
    return new CompactParseable <Pattern, NameClass, Locator, VoidValue, CommentListImpl, AnnotationsImpl> (SAX.createInput (source.getInputSource ()),
                                                                                                            saxResolver.getResolver (),
                                                                                                            eh);
  }
}
