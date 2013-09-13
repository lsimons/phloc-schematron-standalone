package com.thaiopensource.datatype.xsd;

import java.util.Iterator;

import org.relaxng.datatype.DatatypeLibrary;
import org.relaxng.datatype.DatatypeLibraryFactory;

import com.thaiopensource.datatype.xsd.regex.RegexEngine;
import com.thaiopensource.util.Service;
import com.thaiopensource.xml.util.WellKnownNamespaces;

public class DatatypeLibraryFactoryImpl implements DatatypeLibraryFactory
{

  private DatatypeLibrary datatypeLibrary = null;
  private final RegexEngine regexEngine;
  private final boolean autoRegexEngine;

  public DatatypeLibraryFactoryImpl ()
  {
    this.regexEngine = null;
    this.autoRegexEngine = true;
  }

  public DatatypeLibraryFactoryImpl (final RegexEngine regexEngine)
  {
    this.regexEngine = regexEngine;
    this.autoRegexEngine = false;
  }

  public DatatypeLibrary createDatatypeLibrary (final String uri)
  {
    if (!WellKnownNamespaces.XML_SCHEMA_DATATYPES.equals (uri))
      return null;
    synchronized (this)
    {
      if (datatypeLibrary == null)
        datatypeLibrary = new DatatypeLibraryImpl (autoRegexEngine ? findRegexEngine () : regexEngine);
      return datatypeLibrary;
    }
  }

  private static RegexEngine findRegexEngine ()
  {
    final Iterator <RegexEngine> iter = Service.newInstance (RegexEngine.class).getProviders ();
    if (!iter.hasNext ())
      return null;
    return iter.next ();
  }

}
