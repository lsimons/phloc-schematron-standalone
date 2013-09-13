package com.thaiopensource.relaxng.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.xml.sax.SAXException;

import com.thaiopensource.datatype.DatatypeLibraryLoader;
import com.thaiopensource.util.Localizer;
import com.thaiopensource.util.OptionParser;
import com.thaiopensource.util.PropertyMapBuilder;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.validate.ValidationDriver;
import com.thaiopensource.validate.prop.rng.RngProperty;
import com.thaiopensource.xml.sax.ErrorHandlerImpl;

class TestDriver
{
  static public void main (final String [] args) throws IOException
  {
    System.exit (new TestDriver ().doMain (args));
  }

  private ValidationDriver driver;
  private ErrorHandlerImpl eh;
  private final Localizer localizer = new Localizer (TestDriver.class);
  private int nTests = 0;

  public int doMain (String [] args) throws IOException
  {
    final long startTime = System.currentTimeMillis ();
    eh = new ErrorHandlerImpl (System.out);
    final OptionParser op = new OptionParser ("i", args);
    final PropertyMapBuilder properties = new PropertyMapBuilder ();
    // This is an optimization. It ensures that all SchemaReaders share a
    // single DatatypeLibraryLoader.
    properties.put (RngProperty.DATATYPE_LIBRARY_FACTORY, new DatatypeLibraryLoader ());
    try
    {
      while (op.moveToNextOption ())
      {
        switch (op.getOptionChar ())
        {
          case 'i':
            RngProperty.CHECK_ID_IDREF.add (properties);
            break;
        }
      }
    }
    catch (final OptionParser.InvalidOptionException e)
    {
      eh.print (localizer.message ("invalid_option", op.getOptionCharString ()));
      return 2;
    }
    catch (final OptionParser.MissingArgumentException e)
    {
      eh.print (localizer.message ("option_missing_argument", op.getOptionCharString ()));
      return 2;
    }
    args = op.getRemainingArgs ();
    eh = new ErrorHandlerImpl (new BufferedWriter (new OutputStreamWriter (new FileOutputStream (args[0]))));
    properties.put (ValidateProperty.ERROR_HANDLER, eh);
    driver = new ValidationDriver (properties.toPropertyMap ());
    int result = 0;
    for (int i = 1; i < args.length; i++)
    {
      final int n = runTestSuite (new File (args[i]));
      if (n > result)
        result = n;
    }
    System.err.println ("Number of tests: " + nTests);
    System.err.println ("Elapsed time: " + (System.currentTimeMillis () - startTime));
    eh.close ();
    return result;
  }

  private static final String CORRECT_SCHEMA_NAME = "c.rng";
  private static final String INCORRECT_SCHEMA_NAME = "i.rng";
  private static final String VALID_INSTANCE_SUFFIX = ".v.xml";
  private static final String INVALID_INSTANCE_SUFFIX = ".i.xml";

  public int runTestSuite (final File dir) throws IOException
  {
    int result = 0;
    final String [] subdirs = dir.list ();
    for (final String subdir2 : subdirs)
    {
      final File subdir = new File (dir, subdir2);
      if (subdir.isDirectory ())
      {
        final int n = runTestCase (subdir);
        if (n > result)
          result = n;
      }
    }
    return result;
  }

  private int runTestCase (final File dir) throws IOException
  {
    File f = new File (dir, INCORRECT_SCHEMA_NAME);
    if (f.exists ())
    {
      if (loadSchema (f))
      {
        failed (f);
        return 1;
      }
      return 0;
    }
    f = new File (dir, CORRECT_SCHEMA_NAME);
    if (!f.exists ())
      return 0;
    if (!loadSchema (f))
    {
      failed (f);
      return 1;
    }
    final String [] files = dir.list ();
    int result = 0;
    for (final String file : files)
    {
      if (file.endsWith (VALID_INSTANCE_SUFFIX))
      {
        f = new File (dir, file);
        if (!validateInstance (f))
        {
          failed (f);
          result = 1;
        }
      }
      else
        if (file.endsWith (INVALID_INSTANCE_SUFFIX))
        {
          f = new File (dir, file);
          if (validateInstance (f))
          {
            failed (f);
            result = 1;
          }
        }
    }
    return result;
  }

  private static void failed (final File f)
  {
    System.err.println ("Failed: " + f.toString ());
  }

  private boolean loadSchema (final File schema) throws IOException
  {
    nTests++;
    try
    {
      if (driver.loadSchema (ValidationDriver.fileInputSource (schema)))
        return true;
    }
    catch (final SAXException e)
    {
      eh.printException (e);
    }
    return false;
  }

  private boolean validateInstance (final File instance) throws IOException
  {
    nTests++;
    try
    {
      if (driver.validate (ValidationDriver.fileInputSource (instance)))
        return true;
    }
    catch (final SAXException e)
    {
      eh.printException (e);
    }
    return false;
  }
}
