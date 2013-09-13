package com.thaiopensource.relaxng.output.xsd;

import java.io.IOException;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import com.thaiopensource.relaxng.edit.SchemaCollection;
import com.thaiopensource.relaxng.output.OutputDirectory;
import com.thaiopensource.relaxng.output.OutputDirectoryParamProcessor;
import com.thaiopensource.relaxng.output.OutputFailedException;
import com.thaiopensource.relaxng.output.OutputFormat;
import com.thaiopensource.relaxng.output.common.ErrorReporter;
import com.thaiopensource.relaxng.output.xsd.basic.Schema;
import com.thaiopensource.relaxng.translate.util.AbstractParam;
import com.thaiopensource.relaxng.translate.util.EnumParam;
import com.thaiopensource.relaxng.translate.util.InvalidParamsException;
import com.thaiopensource.relaxng.translate.util.ParamProcessor;

public class XsdOutputFormat implements OutputFormat
{
  static private final boolean DEFAULT_ENABLE_ABSTRACT_ELEMENT = true;
  static private final String [] processContents = { "skip", "lax", "strict" };

  public void output (final SchemaCollection sc,
                      final OutputDirectory od,
                      final String [] params,
                      final String inputFormat,
                      final ErrorHandler eh) throws SAXException,
                                            IOException,
                                            OutputFailedException,
                                            InvalidParamsException
  {
    final Guide guide = new Guide (DEFAULT_ENABLE_ABSTRACT_ELEMENT);
    final BasicOutput.Options outputOptions = new BasicOutput.Options ();
    if ("dtd".equals (inputFormat))
      outputOptions.anyProcessContents = "strict";
    final ParamProcessor pp = new OutputDirectoryParamProcessor (od);
    pp.declare ("disable-abstract-elements", new AbstractParam ()
    {
      @Override
      public void set (final boolean value)
      {
        guide.setDefaultGroupEnableAbstractElements (!value);
      }
    });
    pp.declare ("any-process-contents", new EnumParam (processContents)
    {
      @Override
      protected void setEnum (final int i)
      {
        outputOptions.anyProcessContents = getValues ()[i];
      }
    });
    pp.declare ("any-attribute-process-contents", new EnumParam (processContents)
    {
      @Override
      protected void setEnum (final int i)
      {
        outputOptions.anyAttributeProcessContents = getValues ()[i];
      }
    });
    pp.process (params, eh);
    try
    {
      final ErrorReporter er = new ErrorReporter (eh, XsdOutputFormat.class);
      final SchemaInfo si = new SchemaInfo (sc, er);
      if (!er.getHadError ())
      {
        RefChecker.check (si, er);
        if (!er.getHadError ())
        {
          RestrictionsChecker.check (si, er);
          if (!er.getHadError ())
          {
            final Schema schema = BasicBuilder.buildBasicSchema (si, guide, er);
            if (!er.getHadError ())
            {
              new Transformer (schema, er).transform ();
              if (!er.getHadError ())
                BasicOutput.output (schema, guide, new PrefixManager (si), od, outputOptions, er);
            }
          }
        }
      }
      if (er.getHadError ())
        throw new OutputFailedException ();
    }
    catch (final ErrorReporter.WrappedSAXException e)
    {
      throw e.getException ();
    }
  }
}
