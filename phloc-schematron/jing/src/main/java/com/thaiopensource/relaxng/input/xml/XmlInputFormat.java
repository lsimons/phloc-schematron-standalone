package com.thaiopensource.relaxng.input.xml;

import java.io.IOException;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import com.thaiopensource.relaxng.edit.SchemaCollection;
import com.thaiopensource.relaxng.input.AbstractMultiInputFormat;
import com.thaiopensource.relaxng.input.InputFailedException;
import com.thaiopensource.relaxng.translate.util.EncodingParam;
import com.thaiopensource.relaxng.translate.util.InvalidParamsException;
import com.thaiopensource.relaxng.translate.util.ParamProcessor;
import com.thaiopensource.resolver.Resolver;

public class XmlInputFormat extends AbstractMultiInputFormat
{
  public SchemaCollection load (final String [] uris,
                                final String [] params,
                                final String outputFormat,
                                final ErrorHandler eh,
                                final Resolver resolver) throws InputFailedException,
                                                        InvalidParamsException,
                                                        IOException,
                                                        SAXException
  {
    final ParamProcessor pp = new ParamProcessor ();
    final Inferrer.Options options = new Inferrer.Options ();
    options.resolver = resolver;
    pp.declare ("encoding", new EncodingParam ()
    {
      @Override
      protected void setEncoding (final String encoding)
      {
        options.encoding = encoding;
      }
    });
    pp.process (params, eh);
    return Inferrer.infer (uris, options, eh);
  }
}
