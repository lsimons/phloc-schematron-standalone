package com.phloc.tools;

import java.io.File;
import java.io.IOException;

import com.thaiopensource.datatype.xsd.regex.java.gen.CategoriesGen;
import com.thaiopensource.datatype.xsd.regex.java.gen.NamingExceptionsGen;

public class MainCodeGen
{
  public static void main (final String [] args) throws IOException
  {
    final String sGenPath = new File ("src/main/java").getAbsolutePath ();
    System.out.println (sGenPath);
    NamingExceptionsGen.main (new String [] { "com.thaiopensource.datatype.xsd.regex.java.NamingExceptions", sGenPath });
    CategoriesGen.main (new String [] { "com.thaiopensource.datatype.xsd.regex.java.Categories",
                                       sGenPath,
                                       "src/test/resources/lib/UnicodeData-3.1.0.txt" });
    System.out.println ("Done");
  }
}
