package com.thaiopensource.datatype.xsd.regex.test;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.thaiopensource.datatype.xsd.regex.Regex;
import com.thaiopensource.datatype.xsd.regex.RegexEngine;
import com.thaiopensource.datatype.xsd.regex.RegexSyntaxException;
import com.thaiopensource.util.UriOrFile;
import com.thaiopensource.util.Utf16;

public class TestDriver extends DefaultHandler
{
  private final StringBuffer buf = new StringBuffer ();
  private Regex regex;
  private int nFail = 0;
  private int nTests = 0;
  private Locator loc;
  private final RegexEngine engine;

  static public void main (final String [] args) throws SAXException,
                                                IOException,
                                                ParserConfigurationException,
                                                ClassNotFoundException,
                                                IllegalAccessException,
                                                InstantiationException
  {
    if (args.length != 2)
    {
      System.err.println ("usage: TestDriver class testfile");
      System.exit (2);
    }
    final SAXParserFactory factory = SAXParserFactory.newInstance ();
    factory.setNamespaceAware (true);
    factory.setValidating (false);
    final XMLReader xr = factory.newSAXParser ().getXMLReader ();
    final Class <?> cls = TestDriver.class.getClassLoader ().loadClass (args[0]);
    final RegexEngine engine = (RegexEngine) cls.newInstance ();
    final TestDriver tester = new TestDriver (engine);
    xr.setContentHandler (tester);
    final InputSource in = new InputSource (UriOrFile.fileToUri (args[1]));
    xr.parse (in);
    System.err.println (tester.nTests + " tests performed");
    System.err.println (tester.nFail + " failures");
    if (tester.nFail > 0)
      System.exit (1);
  }

  public TestDriver (final RegexEngine engine)
  {
    this.engine = engine;
  }

  @Override
  public void setDocumentLocator (final Locator locator)
  {
    this.loc = locator;
  }

  @Override
  public void characters (final char ch[], final int start, final int length) throws SAXException
  {
    buf.append (ch, start, length);
  }

  @Override
  public void ignorableWhitespace (final char ch[], final int start, final int length) throws SAXException
  {
    buf.append (ch, start, length);
  }

  @Override
  public void startElement (final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException
  {
    buf.setLength (0);
  }

  @Override
  public void endElement (final String uri, final String localName, final String qName) throws SAXException
  {
    if (localName.equals ("valid"))
      valid (buf.toString ());
    else
      if (localName.equals ("invalid"))
        invalid (buf.toString ());
      else
        if (localName.equals ("correct"))
          correct (buf.toString ());
        else
          if (localName.equals ("incorrect"))
            incorrect (buf.toString ());
  }

  private void correct (final String str)
  {
    nTests++;
    regex = null;
    try
    {
      regex = engine.compile (str);
    }
    catch (final RegexSyntaxException e)
    {
      error ("unexpected error: " + e.getMessage () + ": " + display (str, e.getPosition ()));
    }
  }

  private void incorrect (final String str)
  {
    nTests++;
    regex = null;
    try
    {
      engine.compile (str);
      error ("failed to detect error in regex: " + display (str, -1));
    }
    catch (final RegexSyntaxException e)
    {}
  }

  private void valid (final String str)
  {
    if (regex == null)
      return;
    nTests++;
    if (!regex.matches (str))
      error ("match failed for string: " + display (str, -1));
  }

  private void invalid (final String str)
  {
    if (regex == null)
      return;
    nTests++;
    if (regex.matches (str))
      error ("match incorrectly succeeded for string: " + display (str, -1));
  }

  private void error (final String str)
  {
    int line = -1;
    if (loc != null)
      line = loc.getLineNumber ();
    if (line >= 0)
      System.err.print ("Line " + line + ": ");
    System.err.println (str);
    nFail++;
  }

  static final private String ERROR_MARKER = ">>>>";

  static String display (final String str, final int pos)
  {
    final StringBuffer buf = new StringBuffer ();
    for (int i = 0, len = str.length (); i < len; i++)
    {
      if (i == pos)
        buf.append (ERROR_MARKER);
      final char c = str.charAt (i);
      if (Utf16.isSurrogate1 (c))
        buf.append ("&#x" + Integer.toHexString (Utf16.scalarValue (c, str.charAt (++i))) + ";");
      else
        if (c < ' ' || c >= 0x7F)
          buf.append ("&#x" + Integer.toHexString (c) + ";");
        else
          if (c == '&')
            buf.append ("&amp;");
          else
            buf.append (c);
    }
    if (str.length () == pos)
      buf.append (ERROR_MARKER);
    return buf.toString ();
  }

}
