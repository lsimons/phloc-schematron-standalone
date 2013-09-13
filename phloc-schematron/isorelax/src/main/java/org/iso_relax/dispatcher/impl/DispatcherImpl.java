/*
 * @(#)$Id: DispatcherImpl.java,v 1.5 2003/05/30 23:46:32 kkawa Exp $
 *
 * Copyright 2001 Kohsuke KAWAGUCHI
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.iso_relax.dispatcher.impl;

import java.util.Enumeration;
import java.util.Vector;

import org.iso_relax.dispatcher.ElementDecl;
import org.iso_relax.dispatcher.IslandVerifier;
import org.iso_relax.dispatcher.SchemaProvider;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.NamespaceSupport;

/**
 * reference implementation of Dispatcher interface.
 * 
 * @author <a href="mailto:k-kawa@bigfoot.com">Kohsuke KAWAGUCHI</a>
 */
public class DispatcherImpl implements org.iso_relax.dispatcher.Dispatcher
{
  /**
   * depth of the nesting of elements from the start of the current
   * IslandVerifier. this value has to start with 1 to prevent initial
   * IslandVerifier from being cut in.
   */
  private int depth = 1;
  protected Locator documentLocator = null;

  protected final NamespaceSupport nsMap = new NamespaceSupport ();
  protected ErrorHandler errorHandler;

  /** current validating processor which processes this island. */
  private IslandVerifier currentHandler = null;

  /** Dispatcher will consult this object about schema information */
  protected final SchemaProvider schema;

  public SchemaProvider getSchemaProvider ()
  {
    return schema;
  }

  /** this object passes SAX events to IslandVerifier. */
  protected Transponder transponder;

  public DispatcherImpl (final SchemaProvider pschema)
  {
    this.schema = pschema;
    this.transponder = new Transponder ();
    this.currentHandler = pschema.createTopLevelVerifier ();
    this.currentHandler.setDispatcher (this);
  }

  protected static final class Context
  {
    public final IslandVerifier handler;
    public final int depth;
    public final Context previous;

    public Context (final IslandVerifier phandler, final int pdepth, final Context pprevious)
    {
      this.handler = phandler;
      this.depth = pdepth;
      this.previous = pprevious;
    }
  }

  protected Context contextStack = null;

  public void attachXMLReader (final XMLReader reader)
  {
    reader.setContentHandler (transponder);
  }

  public void switchVerifier (final IslandVerifier newVerifier) throws SAXException
  {
    // push context
    contextStack = new Context (currentHandler, depth, contextStack);

    currentHandler = newVerifier;
    currentHandler.setDispatcher (this);
    currentHandler.setDocumentLocator (documentLocator);
    depth = 0;

    // inform new IslandHandler about all prefix mappings
    final Enumeration <?> e = nsMap.getDeclaredPrefixes ();
    while (e.hasMoreElements ())
    {
      final String prefix = (String) e.nextElement ();
      currentHandler.startPrefixMapping (prefix, nsMap.getURI (prefix));
    }
  }

  public void setErrorHandler (final ErrorHandler handler)
  {
    this.errorHandler = handler;
  }

  public ErrorHandler getErrorHandler ()
  {
    return errorHandler;
  }

  protected final Vector <UnparsedEntityDecl> unparsedEntityDecls = new Vector <UnparsedEntityDecl> ();

  public int countUnparsedEntityDecls ()
  {
    return unparsedEntityDecls.size ();
  }

  public UnparsedEntityDecl getUnparsedEntityDecl (final int index)
  {
    return unparsedEntityDecls.get (index);
  }

  protected final Vector <NotationDecl> notationDecls = new Vector <NotationDecl> ();

  public int countNotationDecls ()
  {
    return notationDecls.size ();
  }

  public NotationDecl getNotationDecl (final int index)
  {
    return notationDecls.get (index);
  }

  /**
   * relays SAX events to IslandVerifiers. This class is kept separate to make
   * document of Dispatcher cleaner (by removing SAX events from Dispatcher).
   */
  private class Transponder implements ContentHandler, DTDHandler
  {
    public void unparsedEntityDecl (final String name,
                                    final String systemId,
                                    final String publicId,
                                    final String notation)
    {// memorize unparsedEntityDecl
      unparsedEntityDecls.add (new UnparsedEntityDecl (name, systemId, publicId, notation));
    }

    public void notationDecl (final String name, final String systemId, final String publicId)
    {// memorize notationDecl
      notationDecls.add (new NotationDecl (name, systemId, publicId));
    }

    public void setDocumentLocator (final Locator locator)
    {
      documentLocator = locator;
      currentHandler.setDocumentLocator (locator);
    }

    public void startElement (final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException
    {
      currentHandler.startElement (uri, localName, qName, attributes);
      depth++;
      nsMap.pushContext ();
    }

    public void endElement (final String uri, final String localName, final String qName) throws SAXException
    {
      nsMap.popContext ();
      currentHandler.endElement (uri, localName, qName);

      if (--depth == 0)
      {// cut in and restore the previos IslandVerifier.

        // call endPrefixMapping for all pre-declared prefixes.
        final Enumeration <?> e = nsMap.getDeclaredPrefixes ();
        while (e.hasMoreElements ())
          currentHandler.endPrefixMapping ((String) e.nextElement ());

        // gets labels which are actually verified.
        final ElementDecl [] results = currentHandler.endIsland ();

        // pop context
        depth = contextStack.depth;
        currentHandler = contextStack.handler;
        contextStack = contextStack.previous;

        // report assigned label to the parent
        currentHandler.endChildIsland (uri, results);
      }
    }

    public void characters (final char ch[], final int start, final int length) throws SAXException
    {
      currentHandler.characters (ch, start, length);
    }

    public void ignorableWhitespace (final char ch[], final int start, final int length) throws SAXException
    {
      currentHandler.ignorableWhitespace (ch, start, length);
    }

    public void processingInstruction (final String target, final String data) throws SAXException
    {
      currentHandler.processingInstruction (target, data);
    }

    public void skippedEntity (final String name) throws SAXException
    {
      currentHandler.skippedEntity (name);
    }

    // those events should not be reported to island verifier.
    public void startDocument ()
    {}

    public void endDocument ()
    {}

    public void startPrefixMapping (final String prefix, final String uri) throws SAXException
    {
      nsMap.declarePrefix (prefix, uri);
      currentHandler.startPrefixMapping (prefix, uri);
    }

    public void endPrefixMapping (final String prefix) throws SAXException
    {
      currentHandler.endPrefixMapping (prefix);
    }
  }
}
