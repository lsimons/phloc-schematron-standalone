package com.thaiopensource.relaxng.sax;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import com.thaiopensource.relaxng.pattern.IdSoundnessChecker;
import com.thaiopensource.relaxng.pattern.IdTypeMap;
import com.thaiopensource.xml.util.Name;

public class IdContentHandler implements ContentHandler
{
  private final IdSoundnessChecker checker;
  private Locator locator;

  public IdContentHandler (final IdTypeMap idTypeMap, final ErrorHandler eh)
  {
    this.checker = new IdSoundnessChecker (idTypeMap, eh);
  }

  public void reset ()
  {
    checker.reset ();
    locator = null;
  }

  public void setDocumentLocator (final Locator locator)
  {
    this.locator = locator;
  }

  public void startDocument () throws SAXException
  {}

  public void endDocument () throws SAXException
  {
    checker.endDocument ();
    setComplete ();
  }

  protected void setComplete ()
  {
    // / XXX what's the point of this?
  }

  public void startPrefixMapping (final String s, final String s1) throws SAXException
  {}

  public void endPrefixMapping (final String s) throws SAXException
  {}

  public void startElement (final String namespaceUri,
                            final String localName,
                            final String qName,
                            final Attributes attributes) throws SAXException
  {
    final Name elementName = new Name (namespaceUri, localName);
    final int len = attributes.getLength ();
    for (int i = 0; i < len; i++)
    {
      final Name attributeName = new Name (attributes.getURI (i), attributes.getLocalName (i));
      final String value = attributes.getValue (i);
      checker.attribute (elementName, attributeName, value, locator);
    }
  }

  public void endElement (final String s, final String s1, final String s2) throws SAXException
  {}

  public void characters (final char [] chars, final int i, final int i1) throws SAXException
  {}

  public void ignorableWhitespace (final char [] chars, final int i, final int i1) throws SAXException
  {}

  public void processingInstruction (final String s, final String s1) throws SAXException
  {}

  public void skippedEntity (final String s) throws SAXException
  {}

  public void notationDecl (final String name, final String publicId, final String systemId) throws SAXException
  {}

  public void unparsedEntityDecl (final String name,
                                  final String publicId,
                                  final String systemId,
                                  final String notationName) throws SAXException
  {}
}
