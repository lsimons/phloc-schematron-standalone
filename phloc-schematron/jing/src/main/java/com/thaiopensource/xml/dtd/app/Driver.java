package com.thaiopensource.xml.dtd.app;

import java.io.IOException;

import com.thaiopensource.util.Localizer;
import com.thaiopensource.util.UriOrFile;
import com.thaiopensource.util.Version;
import com.thaiopensource.xml.dtd.om.Dtd;
import com.thaiopensource.xml.dtd.parse.DtdParserImpl;
import com.thaiopensource.xml.em.UriEntityManager;
import com.thaiopensource.xml.out.XmlWriter;

public class Driver
{
  private static final int FAILURE_EXIT_CODE = 1;
  private static final Localizer localizer = new Localizer (Driver.class);

  public static void main (final String [] args)
  {
    try
    {
      if (doMain (args))
        return;
    }
    catch (final IOException e)
    {
      error (e.getMessage ());
    }
    System.exit (FAILURE_EXIT_CODE);
  }

  public static boolean doMain (final String args[]) throws IOException
  {
    if (args.length == 0)
    {
      error (localizer.message ("MISSING_ARGUMENT"));
      usage ();
      return false;
    }
    if (args.length > 1)
    {
      error (localizer.message ("TOO_MANY_ARGUMENTS"));
      usage ();
      return false;
    }
    final String uri = UriOrFile.toUri (args[0]);
    final Dtd dtd = new DtdParserImpl ().parse (uri, new UriEntityManager ());
    final XmlWriter w = new XmlOutputStreamWriter (System.out, dtd.getEncoding ());
    new SchemaWriter (w).writeDtd (dtd);
    w.close ();
    return true;
  }

  private static void usage ()
  {
    print (localizer.message ("USAGE", Version.getVersion (Driver.class)));
  }

  private static void error (final String str)
  {
    print (localizer.message ("ERROR", str));
  }

  private static void print (final String str)
  {
    System.err.println (str);
  }

}
