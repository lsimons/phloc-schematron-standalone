package com.thaiopensource.relaxng.jaxp;

import javax.xml.XMLConstants;
import javax.xml.validation.TypeInfoProvider;

import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;

import com.thaiopensource.relaxng.match.Matcher;
import com.thaiopensource.relaxng.pattern.Pattern;
import com.thaiopensource.relaxng.pattern.PatternMatcher;
import com.thaiopensource.relaxng.pattern.ValidatorPatternBuilder;
import com.thaiopensource.relaxng.sax.Context;
import com.thaiopensource.validation.ValidatorHandler2;
import com.thaiopensource.xml.sax.DraconianErrorHandler;
import com.thaiopensource.xml.util.Name;

class ValidatorHandlerImpl extends ValidatorHandler2
{
  private Matcher matcher;
  static private final ErrorHandler defaultErrorHandler = new DraconianErrorHandler ();
  private ErrorHandler specifiedErrorHandler = null;
  private ErrorHandler actualErrorHandler = defaultErrorHandler;

  private boolean bufferingCharacters = false;
  private final StringBuffer charBuf = new StringBuffer ();
  private Locator locator = null;
  private final Context context;
  private ContentHandler contentHandler = null;
  private DTDHandler dtdHandler;
  private LSResourceResolver resourceResolver = null;
  private boolean secureProcessing;

  ValidatorHandlerImpl (final SchemaFactoryImpl factory, final Pattern pattern, final ValidatorPatternBuilder builder)
  {
    matcher = new PatternMatcher (pattern, builder);
    context = new Context ();
    // the docs say it gets the properties of its factory, not the features
    secureProcessing = false;
  }

  @Override
  public void reset ()
  {
    bufferingCharacters = false;
    locator = null;
    matcher = matcher.start ();
    context.reset ();
  }

  public void startElement (final String namespaceURI, final String localName, final String qName, final Attributes atts) throws SAXException
  {
    if (bufferingCharacters)
    {
      bufferingCharacters = false;
      check (matcher.matchTextBeforeStartTag (charBuf.toString (), context));
    }
    final Name name = new Name (namespaceURI, localName);
    check (matcher.matchStartTagOpen (name, qName, context));
    final int len = atts.getLength ();
    for (int i = 0; i < len; i++)
    {
      final Name attName = new Name (atts.getURI (i), atts.getLocalName (i));
      final String attQName = atts.getQName (i);
      check (matcher.matchAttributeName (attName, attQName, context));
      check (matcher.matchAttributeValue (atts.getValue (i), attName, attQName, context));
    }
    check (matcher.matchStartTagClose (name, qName, context));
    if (matcher.isTextTyped ())
    {
      bufferingCharacters = true;
      charBuf.setLength (0);
    }
    if (contentHandler != null)
      contentHandler.startElement (namespaceURI, localName, qName, atts);
  }

  public void endElement (final String namespaceURI, final String localName, final String qName) throws SAXException
  {
    if (bufferingCharacters)
    {
      bufferingCharacters = false;
      if (charBuf.length () > 0)
        check (matcher.matchTextBeforeEndTag (charBuf.toString (), new Name (namespaceURI, localName), qName, context));
    }
    check (matcher.matchEndTag (new Name (namespaceURI, localName), qName, context));
    if (contentHandler != null)
      contentHandler.endElement (namespaceURI, localName, qName);
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
          check (matcher.matchUntypedText (context));
          return;
      }
    }
  }

  public void endDocument () throws SAXException
  {
    check (matcher.matchEndDocument ());
    if (contentHandler != null)
      contentHandler.endDocument ();
  }

  public void setDocumentLocator (final Locator locator)
  {
    this.locator = locator;
    if (contentHandler != null)
      contentHandler.setDocumentLocator (locator);
  }

  public void startDocument () throws SAXException
  {
    check (matcher.matchStartDocument ());
    if (contentHandler != null)
      contentHandler.startDocument ();
  }

  public void processingInstruction (final String target, final String data) throws SAXException
  {
    if (contentHandler != null)
      contentHandler.processingInstruction (target, data);
  }

  public void skippedEntity (final String name) throws SAXException
  {
    if (contentHandler != null)
      contentHandler.skippedEntity (name);
  }

  public void ignorableWhitespace (final char [] ch, final int start, final int len) throws SAXException
  {
    if (contentHandler != null)
      contentHandler.ignorableWhitespace (ch, start, len);
  }

  private void check (final boolean ok) throws SAXException
  {
    if (!ok)
      actualErrorHandler.error (new SAXParseException (matcher.getErrorMessage (), locator));
  }

  @Override
  public void setContentHandler (final ContentHandler delegate)
  {
    this.contentHandler = delegate;
  }

  @Override
  public ContentHandler getContentHandler ()
  {
    return contentHandler;
  }

  @Override
  public void setDTDHandler (final DTDHandler dtdHandler)
  {
    this.dtdHandler = dtdHandler;
  }

  @Override
  public DTDHandler getDTDHandler ()
  {
    return dtdHandler;
  }

  @Override
  public TypeInfoProvider getTypeInfoProvider ()
  {
    return null;
  }

  @Override
  public void setErrorHandler (final ErrorHandler errorHandler)
  {
    this.specifiedErrorHandler = errorHandler;
    this.actualErrorHandler = errorHandler == null ? defaultErrorHandler : errorHandler;
  }

  @Override
  public ErrorHandler getErrorHandler ()
  {
    return specifiedErrorHandler;
  }

  @Override
  public void setResourceResolver (final LSResourceResolver resourceResolver)
  {
    this.resourceResolver = resourceResolver;
  }

  @Override
  public LSResourceResolver getResourceResolver ()
  {
    return resourceResolver;
  }

  public void startPrefixMapping (final String prefix, final String uri) throws SAXException
  {
    // namespace declarations on the start-tag shouldn't apply to the characters
    // before the start-tag
    if (bufferingCharacters)
    {
      bufferingCharacters = false;
      check (matcher.matchTextBeforeStartTag (charBuf.toString (), context));
    }
    context.startPrefixMapping (prefix, uri);
    if (contentHandler != null)
      contentHandler.startPrefixMapping (prefix, uri);
  }

  public void endPrefixMapping (final String prefix) throws SAXException
  {
    context.endPrefixMapping (prefix);
    if (contentHandler != null)
      contentHandler.endPrefixMapping (prefix);
  }

  public void notationDecl (final String name, final String publicId, final String systemId) throws SAXException
  {
    context.notationDecl (name, publicId, systemId);
    if (dtdHandler != null)
      dtdHandler.notationDecl (name, publicId, systemId);
  }

  public void unparsedEntityDecl (final String name,
                                  final String publicId,
                                  final String systemId,
                                  final String notationName) throws SAXException
  {
    context.unparsedEntityDecl (name, publicId, systemId, notationName);
    if (dtdHandler != null)
      dtdHandler.unparsedEntityDecl (name, publicId, systemId, notationName);
  }

  @Override
  public void setFeature (final String name, final boolean value) throws SAXNotRecognizedException,
                                                                 SAXNotSupportedException
  {
    if (XMLConstants.FEATURE_SECURE_PROCESSING.equals (name))
      secureProcessing = value;
    else
      super.setFeature (name, value);
  }

  @Override
  public boolean getFeature (final String name) throws SAXNotRecognizedException, SAXNotSupportedException
  {
    if (XMLConstants.FEATURE_SECURE_PROCESSING.equals (name))
      return secureProcessing;
    return super.getFeature (name);
  }
}
