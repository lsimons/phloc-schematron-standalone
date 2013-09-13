package com.thaiopensource.relaxng.output.dtd;

import java.io.IOException;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import com.thaiopensource.relaxng.edit.SchemaCollection;
import com.thaiopensource.relaxng.output.OutputDirectory;
import com.thaiopensource.relaxng.output.OutputDirectoryParamProcessor;
import com.thaiopensource.relaxng.output.OutputFailedException;
import com.thaiopensource.relaxng.output.OutputFormat;
import com.thaiopensource.relaxng.output.common.ErrorReporter;
import com.thaiopensource.relaxng.translate.util.InvalidParamsException;

public class DtdOutputFormat implements OutputFormat
{
  public void output (final SchemaCollection sc,
                      final OutputDirectory od,
                      final String [] params,
                      final String inputFormat,
                      final ErrorHandler eh) throws SAXException,
                                            IOException,
                                            OutputFailedException,
                                            InvalidParamsException
  {
    new OutputDirectoryParamProcessor (od).process (params, eh);
    Simplifier.simplify (sc);
    try
    {
      final ErrorReporter er = new ErrorReporter (eh, DtdOutputFormat.class);
      final Analysis analysis = new Analysis (sc, er);
      if (!er.getHadError ())
        DtdOutput.output (!inputFormat.equals ("xml"), analysis, od, er);
      if (er.getHadError ())
        throw new OutputFailedException ();
    }
    catch (final ErrorReporter.WrappedSAXException e)
    {
      throw e.getException ();
    }
  }
}
