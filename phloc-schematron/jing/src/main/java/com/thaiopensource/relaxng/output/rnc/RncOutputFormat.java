package com.thaiopensource.relaxng.output.rnc;

import java.io.IOException;
import java.util.Map;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import com.thaiopensource.relaxng.edit.SchemaCollection;
import com.thaiopensource.relaxng.edit.SchemaDocument;
import com.thaiopensource.relaxng.output.OutputDirectory;
import com.thaiopensource.relaxng.output.OutputDirectoryParamProcessor;
import com.thaiopensource.relaxng.output.OutputFailedException;
import com.thaiopensource.relaxng.output.OutputFormat;
import com.thaiopensource.relaxng.output.common.ErrorReporter;
import com.thaiopensource.relaxng.translate.util.InvalidParamsException;

public class RncOutputFormat implements OutputFormat
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
    try
    {
      final ErrorReporter er = new ErrorReporter (eh, RncOutputFormat.class);
      for (final Map.Entry <String, SchemaDocument> entry : sc.getSchemaDocumentMap ().entrySet ())
      {
        outputPattern (entry.getValue (), entry.getKey (), od, er);
      }
    }
    catch (final ErrorReporter.WrappedSAXException e)
    {
      throw e.getException ();
    }
  }

  private static void outputPattern (final SchemaDocument sd,
                                     final String sourceUri,
                                     final OutputDirectory od,
                                     final ErrorReporter er) throws IOException
  {
    Output.output (sd.getPattern (), sd.getEncoding (), sourceUri, od, er);
  }

}
