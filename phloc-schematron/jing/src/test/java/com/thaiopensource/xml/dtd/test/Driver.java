package com.thaiopensource.xml.dtd.test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

import com.thaiopensource.xml.dtd.app.SchemaWriter;
import com.thaiopensource.xml.dtd.app.XmlOutputStreamWriter;
import com.thaiopensource.xml.dtd.om.Dtd;
import com.thaiopensource.xml.dtd.om.DtdParser;
import com.thaiopensource.xml.dtd.parse.DtdParserImpl;
import com.thaiopensource.xml.em.FileEntityManager;
import com.thaiopensource.xml.out.XmlWriter;

public class Driver
{
  public static void main (final String args[]) throws IOException, TestFailException
  {
    final String dir = args[0];
    final String failDir = args[1];
    final String [] files = new File (dir).list ();
    final Hashtable fileTable = new Hashtable ();
    for (final String file : files)
      fileTable.put (file, file);
    String failures = null;
    for (final String inFile : files)
      if (inFile.endsWith (".dtd"))
      {
        final String outFile = inFile.substring (0, inFile.length () - 4) + ".xml";
        if (fileTable.get (outFile) != null)
        {
          try
          {
            System.err.println ("Running test " + inFile);
            runCompareTest (new File (dir, inFile), new File (dir, outFile));
          }
          catch (final CompareFailException e)
          {
            System.err.println (inFile + " failed at byte " + e.getByteIndex ());
            if (failures == null)
              failures = inFile;
            else
              failures += " " + inFile;
            runOutputTest (new File (dir, inFile), new File (failDir, outFile));
          }
        }
      }
    if (failures != null)
      throw new TestFailException (failures);
  }

  public static void runCompareTest (final File inFile, final File outFile) throws IOException
  {
    runTest (inFile, new CompareOutputStream (new BufferedInputStream (new FileInputStream (outFile))));

  }

  public static void runOutputTest (final File inFile, final File outFile) throws IOException
  {
    runTest (inFile, new FileOutputStream (outFile));
  }

  private static void runTest (final File inFile, final OutputStream out) throws IOException
  {
    final DtdParser dtdParser = new DtdParserImpl ();
    final Dtd dtd = dtdParser.parse (inFile.toString (), new FileEntityManager ());
    final XmlWriter w = new XmlOutputStreamWriter (out, dtd.getEncoding ());
    new SchemaWriter (w).writeDtd (dtd);
    w.close ();
  }
}
