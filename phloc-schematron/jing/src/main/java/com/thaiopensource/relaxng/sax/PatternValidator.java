package com.thaiopensource.relaxng.sax;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.thaiopensource.relaxng.match.Matcher;
import com.thaiopensource.relaxng.pattern.Pattern;
import com.thaiopensource.relaxng.pattern.PatternMatcher;
import com.thaiopensource.relaxng.pattern.ValidatorPatternBuilder;
import com.thaiopensource.xml.util.Name;

public class PatternValidator extends Context implements ContentHandler
{
  private Matcher matcher;
  private final ErrorHandler eh;
  private boolean bufferingCharacters = false;
  private final StringBuilder charBuf = new StringBuilder ();
  private Locator locator = null;

  public void startElement (final String namespaceURI, final String localName, final String qName, final Attributes atts) throws SAXException
  {
    if (bufferingCharacters)
    {
      bufferingCharacters = false;
      check (matcher.matchTextBeforeStartTag (charBuf.toString (), this));
    }
    final Name name = new Name (namespaceURI, localName);
    check (matcher.matchStartTagOpen (name, qName, this));
    final int len = atts.getLength ();
    for (int i = 0; i < len; i++)
    {
      final Name attName = new Name (atts.getURI (i), atts.getLocalName (i));
      final String attQName = atts.getQName (i);
      check (matcher.matchAttributeName (attName, attQName, this));
      check (matcher.matchAttributeValue (atts.getValue (i), attName, attQName, this));
    }
    check (matcher.matchStartTagClose (name, qName, this));
    if (matcher.isTextTyped ())
    {
      bufferingCharacters = true;
      charBuf.setLength (0);
    }
  }

  public void endElement (final String namespaceURI, final String localName, final String qName) throws SAXException
  {
    if (bufferingCharacters)
    {
      bufferingCharacters = false;
      if (charBuf.length () > 0)
        check (matcher.matchTextBeforeEndTag (charBuf.toString (), new Name (namespaceURI, localName), qName, this));
    }
    check (matcher.matchEndTag (new Name (namespaceURI, localName), qName, this));
  }

  public void characters (final char ch[], final int start, final int length) throws SAXException
  {
    if (bufferingCharacters)
    {
      charBuf.append (ch, start, length);
      return;
    }
    for (int i = 0; i < length; i++)
    {
      switch (ch[start + i])
      {
        case ' ':
        case '\r':
        case '\t':
        case '\n':
          break;
        default:
          check (matcher.matchUntypedText (this));
          return;
      }
    }
  }

  public void endDocument () throws SAXException
  {
    check (matcher.matchEndDocument ());
  }

  public void setDocumentLocator (final Locator locator)
  {
    this.locator = locator;
  }

  public void startDocument () throws SAXException
  {
    check (matcher.matchStartDocument ());
  }

  public void processingInstruction (final String target, final String date)
  {}

  public void skippedEntity (final String name)
  {}

  public void ignorableWhitespace (final char [] ch, final int start, final int len)
  {}

  @Override
  public void startPrefixMapping (final String prefix, final String uri) throws SAXException
  {
    if (bufferingCharacters)
    {
      bufferingCharacters = false;
      check (matcher.matchTextBeforeStartTag (charBuf.toString (), this));
    }
    super.startPrefixMapping (prefix, uri);
  }

  public PatternValidator (final Pattern pattern, final ValidatorPatternBuilder builder, final ErrorHandler eh)
  {
    this.matcher = new PatternMatcher (pattern, builder);
    this.eh = eh;
  }

  @Override
  public void reset ()
  {
    super.reset ();
    bufferingCharacters = false;
    locator = null;
    matcher = matcher.start ();
  }

  private void check (final boolean ok) throws SAXException
  {
    if (!ok)
      eh.error (new SAXParseException (matcher.getErrorMessage (), locator));
  }
}
