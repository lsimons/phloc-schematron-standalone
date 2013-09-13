package com.thaiopensource.xml.sax;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class DelegatingContentHandler implements ContentHandler
{
  private ContentHandler delegate;

  public DelegatingContentHandler ()
  {}

  public DelegatingContentHandler (final ContentHandler delegate)
  {
    this.delegate = delegate;
  }

  public ContentHandler getDelegate ()
  {
    return delegate;
  }

  public void setDelegate (final ContentHandler delegate)
  {
    this.delegate = delegate;
  }

  public void setDocumentLocator (final Locator locator)
  {
    if (delegate != null)
      delegate.setDocumentLocator (locator);
  }

  public void startDocument () throws SAXException
  {
    if (delegate != null)
      delegate.startDocument ();
  }

  public void endDocument () throws SAXException
  {
    if (delegate != null)
      delegate.endDocument ();
  }

  public void startPrefixMapping (final String prefix, final String uri) throws SAXException
  {
    if (delegate != null)
      delegate.startPrefixMapping (prefix, uri);
  }

  public void endPrefixMapping (final String prefix) throws SAXException
  {
    if (delegate != null)
      delegate.endPrefixMapping (prefix);
  }

  public void startElement (final String namespaceURI, final String localName, final String qName, final Attributes atts) throws SAXException
  {
    if (delegate != null)
      delegate.startElement (namespaceURI, localName, qName, atts);
  }

  public void endElement (final String namespaceURI, final String localName, final String qName) throws SAXException
  {
    if (delegate != null)
      delegate.endElement (namespaceURI, localName, qName);
  }

  public void characters (final char ch[], final int start, final int length) throws SAXException
  {
    if (delegate != null)
      delegate.characters (ch, start, length);
  }

  public void ignorableWhitespace (final char ch[], final int start, final int length) throws SAXException
  {
    if (delegate != null)
      delegate.ignorableWhitespace (ch, start, length);
  }

  public void processingInstruction (final String target, final String data) throws SAXException
  {
    if (delegate != null)
      delegate.processingInstruction (target, data);
  }

  public void skippedEntity (final String name) throws SAXException
  {
    if (delegate != null)
      delegate.skippedEntity (name);
  }
}
