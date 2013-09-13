package com.thaiopensource.util;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class UriOrFileTest
{
  @DataProvider (name = "files")
  public Object [][] createFiles ()
  {
    return new Object [] [] { { "foo" }, { "./foo:bar" }, { "foo\u0e01" },
                             // Uri and back on Win32: c:\\foo
                             // Only file on Win32: c:\\\\foo
                             // {"c:foo"},
                             { "c:\\foo" },
                             { "12:34" } };
  }

  @Test (dataProvider = "files")
  public void testFileRoundTrip (final String file)
  {
    Assert.assertEquals (UriOrFile.uriToUriOrFile (UriOrFile.toUri (file)), new File (file).getAbsolutePath ());
  }

  @DataProvider (name = "uris")
  public Object [][] createUris ()
  {
    return new Object [] [] { { "foo:bar" }, { "http://www.example.com" }, { "fo:o" } };
  }

  @Test (dataProvider = "uris")
  public void testUriRoundTrip (final String uri)
  {
    Assert.assertEquals (UriOrFile.toUri (uri), uri);
  }

  @Test (dataProvider = "files")
  public void testToUri (final String file)
  {
    Assert.assertEquals (UriOrFile.toUri (file), new File (file).getAbsoluteFile ().toURI ().toString ());
  }
}
