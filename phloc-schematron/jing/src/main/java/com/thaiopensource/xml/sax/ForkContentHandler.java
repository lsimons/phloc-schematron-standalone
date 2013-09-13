package com.thaiopensource.xml.sax;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class ForkContentHandler implements ContentHandler
{
  private final ContentHandler ch1;
  private final ContentHandler ch2;

  public ForkContentHandler (final ContentHandler ch1, final ContentHandler ch2)
  {
    this.ch1 = ch1;
    this.ch2 = ch2;
  }

  public void setDocumentLocator (final Locator locator)
  {
    ch1.setDocumentLocator (locator);
    ch2.setDocumentLocator (locator);
  }

  public void startDocument () throws SAXException
  {
    ch1.startDocument ();
    ch2.startDocument ();
  }

  public void endDocument () throws SAXException
  {
    ch1.endDocument ();
    ch2.endDocument ();
  }

  public void startPrefixMapping (final String s, final String s1) throws SAXException
  {
    ch1.startPrefixMapping (s, s1);
    ch2.startPrefixMapping (s, s1);
  }

  public void endPrefixMapping (final String s) throws SAXException
  {
    ch1.endPrefixMapping (s);
    ch2.endPrefixMapping (s);
  }

  public void startElement (final String s, final String s1, final String s2, final Attributes attributes) throws SAXException
  {
    ch1.startElement (s, s1, s2, attributes);
    ch2.startElement (s, s1, s2, attributes);
  }

  public void endElement (final String s, final String s1, final String s2) throws SAXException
  {
    ch1.endElement (s, s1, s2);
    ch2.endElement (s, s1, s2);
  }

  public void characters (final char [] chars, final int i, final int i1) throws SAXException
  {
    ch1.characters (chars, i, i1);
    ch2.characters (chars, i, i1);
  }

  public void ignorableWhitespace (final char [] chars, final int i, final int i1) throws SAXException
  {
    ch1.ignorableWhitespace (chars, i, i1);
    ch2.ignorableWhitespace (chars, i, i1);
  }

  public void processingInstruction (final String s, final String s1) throws SAXException
  {
    ch1.processingInstruction (s, s1);
    ch2.processingInstruction (s, s1);
  }

  public void skippedEntity (final String s) throws SAXException
  {
    ch1.skippedEntity (s);
    ch2.skippedEntity (s);
  }
}
