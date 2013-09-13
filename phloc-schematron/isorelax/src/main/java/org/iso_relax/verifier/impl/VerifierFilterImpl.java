package org.iso_relax.verifier.impl;

import org.iso_relax.verifier.Verifier;
import org.iso_relax.verifier.VerifierFilter;
import org.iso_relax.verifier.VerifierHandler;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * plain vanilla {@link VerifierFilter} implementation.
 * <p>
 * A verifier implementation can use this class to support VerifierFilter
 * functionality.
 * <p>
 * To use this class, implement the {@link Verifier#getVerifierFilter()} method
 * as follows:
 * 
 * <pre>
 * public VerifierFilter getVerifierFilter () throws SAXException
 * {
 *   return new VerifierFilterImpl (getVerifierHandler ());
 * }
 * </pre>
 * <p>
 * Also, usually you may want to override <code>setErrorHandler</code> method so
 * that your <code>VerifierHandler</code> will send errors to that handler.
 * 
 * @version $Id: VerifierFilterImpl.java,v 1.5 2003/05/30 23:46:33 kkawa Exp $
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class VerifierFilterImpl extends XMLFilterImpl implements VerifierFilter
{

  public VerifierFilterImpl (final Verifier _verifier) throws SAXException
  {
    this.verifier = _verifier;
    this.core = verifier.getVerifierHandler ();
  }

  private final Verifier verifier;
  private final VerifierHandler core;

  public boolean isValid ()
  {
    return core.isValid ();
  }

  @Override
  public void setErrorHandler (final ErrorHandler handler)
  {
    super.setErrorHandler (handler);
    // we need to call the setErrorHandler method of the verifier,
    // so that the verifier handler will use this error handler from now on.
    verifier.setErrorHandler (handler);
  }

  @Override
  public void setEntityResolver (final EntityResolver resolver)
  {
    super.setEntityResolver (resolver);
    verifier.setEntityResolver (resolver);
  }

  //
  //
  // ContentHandler events
  //
  //

  @Override
  public void setDocumentLocator (final Locator locator)
  {
    core.setDocumentLocator (locator);
    super.setDocumentLocator (locator);
  }

  @Override
  public void startDocument () throws SAXException
  {
    core.startDocument ();
    super.startDocument ();
  }

  @Override
  public void endDocument () throws SAXException
  {
    core.endDocument ();
    super.endDocument ();
  }

  @Override
  public void startPrefixMapping (final String prefix, final String uri) throws SAXException
  {
    core.startPrefixMapping (prefix, uri);
    super.startPrefixMapping (prefix, uri);
  }

  @Override
  public void endPrefixMapping (final String prefix) throws SAXException
  {
    core.endPrefixMapping (prefix);
    super.endPrefixMapping (prefix);
  }

  @Override
  public void startElement (final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException
  {
    core.startElement (uri, localName, qName, attributes);
    super.startElement (uri, localName, qName, attributes);
  }

  @Override
  public void endElement (final String uri, final String localName, final String qName) throws SAXException
  {
    core.endElement (uri, localName, qName);
    super.endElement (uri, localName, qName);
  }

  @Override
  public void characters (final char ch[], final int start, final int length) throws SAXException
  {
    core.characters (ch, start, length);
    super.characters (ch, start, length);
  }

  @Override
  public void ignorableWhitespace (final char ch[], final int start, final int length) throws SAXException
  {
    core.ignorableWhitespace (ch, start, length);
    super.ignorableWhitespace (ch, start, length);
  }

  @Override
  public void processingInstruction (final String target, final String data) throws SAXException
  {
    core.processingInstruction (target, data);
    super.processingInstruction (target, data);
  }

  @Override
  public void skippedEntity (final String name) throws SAXException
  {
    core.skippedEntity (name);
    super.skippedEntity (name);
  }
}
