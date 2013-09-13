package com.thaiopensource.relaxng.input.parse;

import java.io.IOException;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.thaiopensource.datatype.DatatypeLibraryLoader;
import com.thaiopensource.relaxng.edit.NameClass;
import com.thaiopensource.relaxng.edit.Pattern;
import com.thaiopensource.relaxng.edit.SchemaCollection;
import com.thaiopensource.relaxng.edit.SourceLocation;
import com.thaiopensource.relaxng.input.InputFailedException;
import com.thaiopensource.relaxng.input.InputFormat;
import com.thaiopensource.relaxng.parse.IllegalSchemaException;
import com.thaiopensource.relaxng.parse.Parseable;
import com.thaiopensource.relaxng.translate.util.EncodingParam;
import com.thaiopensource.relaxng.translate.util.InvalidParamsException;
import com.thaiopensource.relaxng.translate.util.ParamProcessor;
import com.thaiopensource.resolver.Resolver;
import com.thaiopensource.resolver.xml.sax.SAXResolver;

public abstract class ParseInputFormat implements InputFormat
{
  private final boolean commentsNeedTrimming;

  protected ParseInputFormat (final boolean commentsNeedTrimming)
  {
    this.commentsNeedTrimming = commentsNeedTrimming;
  }

  public SchemaCollection load (final String uri,
                                final String [] params,
                                final String outputFormat,
                                final ErrorHandler eh,
                                final Resolver resolver) throws InputFailedException,
                                                        InvalidParamsException,
                                                        IOException,
                                                        SAXException
  {
    final InputSource in = new InputSource (uri);
    final ParamProcessor pp = new ParamProcessor ();
    pp.declare ("encoding", new EncodingParam ()
    {
      @Override
      protected void setEncoding (final String encoding)
      {
        in.setEncoding (encoding);
      }
    });
    pp.process (params, eh);
    final Parseable <Pattern, NameClass, SourceLocation, ElementAnnotationBuilderImpl, CommentListImpl, AnnotationsImpl> parseable = makeParseable (in,
                                                                                                                                                    new SAXResolver (resolver),
                                                                                                                                                    eh);
    try
    {
      return SchemaBuilderImpl.parse (parseable, uri, eh, new DatatypeLibraryLoader (), commentsNeedTrimming);
    }
    catch (final IllegalSchemaException e)
    {
      throw new InputFailedException ();
    }
  }

  protected abstract Parseable <Pattern, NameClass, SourceLocation, ElementAnnotationBuilderImpl, CommentListImpl, AnnotationsImpl> makeParseable (InputSource in,
                                                                                                                                                   SAXResolver resolver,
                                                                                                                                                   ErrorHandler eh) throws SAXException;
}
