package com.thaiopensource.relaxng.output.rng;

import java.io.IOException;
import java.util.Map;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import com.thaiopensource.relaxng.edit.SchemaCollection;
import com.thaiopensource.relaxng.edit.SchemaDocument;
import com.thaiopensource.relaxng.output.OutputDirectory;
import com.thaiopensource.relaxng.output.OutputDirectoryParamProcessor;
import com.thaiopensource.relaxng.output.OutputFormat;
import com.thaiopensource.relaxng.translate.util.InvalidParamsException;

public class RngOutputFormat implements OutputFormat
{
  public void output (final SchemaCollection sc,
                      final OutputDirectory od,
                      final String [] params,
                      final String inputFormat,
                      final ErrorHandler eh) throws IOException, InvalidParamsException, SAXException
  {
    new OutputDirectoryParamProcessor (od).process (params, eh);
    for (final Map.Entry <String, SchemaDocument> entry : sc.getSchemaDocumentMap ().entrySet ())
    {
      outputPattern (entry.getValue (), entry.getKey (), od);
    }
  }

  private static void outputPattern (final SchemaDocument sd, final String sourceUri, final OutputDirectory od) throws IOException
  {
    final Analyzer analyzer = new Analyzer ();
    sd.getPattern ().accept (analyzer);
    Output.output (sd.getPattern (),
                   sd.getEncoding (),
                   sourceUri,
                   od,
                   analyzer.getDatatypeLibrary (),
                   analyzer.getPrefixMap ());
  }
}
