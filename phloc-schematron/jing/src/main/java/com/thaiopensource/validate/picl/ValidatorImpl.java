package com.thaiopensource.validate.picl;

import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.LocatorImpl;

import com.thaiopensource.util.Localizer;
import com.thaiopensource.util.PropertyMap;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.validate.Validator;

class ValidatorImpl extends DefaultHandler implements Validator, Path, PatternManager, ErrorContext
{
  private final Constraint constraint;
  private final Stack <OpenElement> openElements = new Stack <OpenElement> ();
  private final Stack <ValueHandler> valueHandlers = new Stack <ValueHandler> ();
  private final Stack <ActivePattern> activePatterns = new Stack <ActivePattern> ();
  private final AttributePath attributePath = new AttributePath ();
  private Locator locator;
  private final ErrorHandler eh;
  private final Localizer localizer = new Localizer (ValidatorImpl.class);

  private static class WrappedSAXException extends RuntimeException
  {
    final SAXException exception;

    WrappedSAXException (final SAXException exception)
    {
      this.exception = exception;
    }
  }

  static class ActivePattern
  {
    final int rootDepth;
    final Pattern pattern;
    final SelectionHandler handler;

    ActivePattern (final int rootDepth, final Pattern pattern, final SelectionHandler handler)
    {
      this.rootDepth = rootDepth;
      this.pattern = pattern;
      this.handler = handler;
    }
  }

  static class OpenElement
  {
    final String namespaceUri;
    final String localName;
    int nActivePatterns;
    int nValueHandlers;

    OpenElement (final String namespaceUri, final String localName)
    {
      this.namespaceUri = namespaceUri;
      this.localName = localName;
    }
  }

  class AttributePath implements Path
  {
    private Attributes atts;
    private int attIndex;

    void set (final Attributes atts, final int attIndex)
    {
      this.atts = atts;
      this.attIndex = attIndex;
    }

    public boolean isAttribute ()
    {
      return true;
    }

    public int length ()
    {
      return ValidatorImpl.this.length () + 1;
    }

    public String getLocalName (final int i)
    {
      if (i == openElements.size ())
        return atts.getLocalName (attIndex);
      return ValidatorImpl.this.getLocalName (i);
    }

    public String getNamespaceUri (final int i)
    {
      if (i == openElements.size ())
        return atts.getURI (attIndex);
      return ValidatorImpl.this.getNamespaceUri (i);
    }
  }

  ValidatorImpl (final Constraint constraint, final PropertyMap properties)
  {
    this.constraint = constraint;
    this.eh = properties.get (ValidateProperty.ERROR_HANDLER);
  }

  public ContentHandler getContentHandler ()
  {
    return this;
  }

  public DTDHandler getDTDHandler ()
  {
    return null;
  }

  public void reset ()
  {
    openElements.setSize (0);
    valueHandlers.setSize (0);
    activePatterns.setSize (0);
    locator = null;
  }

  public int length ()
  {
    return openElements.size ();
  }

  public String getLocalName (final int i)
  {
    return openElements.elementAt (i).localName;
  }

  public String getNamespaceUri (final int i)
  {
    return openElements.elementAt (i).namespaceUri;
  }

  public boolean isAttribute ()
  {
    return false;
  }

  public void registerPattern (final Pattern pattern, final SelectionHandler handler)
  {
    // XXX what about case where it matches dot?
    activePatterns.push (new ActivePattern (openElements.size (), pattern, handler));
    openElements.peek ().nActivePatterns += 1;
  }

  public void registerValueHandler (final ValueHandler handler)
  {
    valueHandlers.push (handler);
    openElements.peek ().nValueHandlers += 1;
  }

  @Override
  public void setDocumentLocator (final Locator locator)
  {
    this.locator = locator;
  }

  @Override
  public void startDocument () throws SAXException
  {
    if (locator == null)
    {
      final LocatorImpl tem = new LocatorImpl ();
      tem.setLineNumber (-1);
      tem.setColumnNumber (-1);
      locator = tem;
    }
    openElements.push (new OpenElement ("", "#root"));
    try
    {
      constraint.activate (this);
    }
    catch (final WrappedSAXException e)
    {
      throw e.exception;
    }
  }

  @Override
  public void endDocument () throws SAXException
  {
    try
    {
      popOpenElement ();
    }
    catch (final WrappedSAXException e)
    {
      throw e.exception;
    }
  }

  @Override
  public void startElement (final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException
  {
    try
    {
      openElements.push (new OpenElement (uri, localName));
      for (int i = 0, len = valueHandlers.size (); i < len; i++)
        valueHandlers.elementAt (i).tag (this);
      for (int i = 0, len = activePatterns.size (); i < len; i++)
      {
        final ActivePattern ap = activePatterns.elementAt (i);
        if (ap.pattern.matches (this, ap.rootDepth))
          ap.handler.selectElement (this, this, this);
      }
      final int nActivePatterns = activePatterns.size ();
      for (int i = 0, len = attributes.getLength (); i < len; i++)
      {
        attributePath.set (attributes, i);
        for (int j = 0; j < nActivePatterns; j++)
        {
          final ActivePattern ap = activePatterns.elementAt (j);
          if (ap.pattern.matches (attributePath, ap.rootDepth))
            ap.handler.selectAttribute (this, attributePath, attributes.getValue (i));
        }
      }
    }
    catch (final WrappedSAXException e)
    {
      throw e.exception;
    }
  }

  @Override
  public void endElement (final String uri, final String localName, final String qName) throws SAXException
  {
    try
    {
      popOpenElement ();
    }
    catch (final WrappedSAXException e)
    {
      throw e.exception;
    }
  }

  @Override
  public void characters (final char ch[], final int start, final int length) throws SAXException
  {
    try
    {
      for (int i = 0, len = valueHandlers.size (); i < len; i++)
        valueHandlers.elementAt (i).characters (this, ch, start, length);
    }
    catch (final WrappedSAXException e)
    {
      throw e.exception;
    }
  }

  @Override
  public void ignorableWhitespace (final char ch[], final int start, final int length) throws SAXException
  {
    characters (ch, start, length);
  }

  private void popOpenElement ()
  {
    final OpenElement top = openElements.pop ();
    for (int i = 0; i < top.nValueHandlers; i++)
    {
      final ValueHandler h = valueHandlers.pop ();
      h.valueComplete (this);
    }
    for (int i = 0; i < top.nActivePatterns; i++)
    {
      final ActivePattern ap = activePatterns.pop ();
      ap.handler.selectComplete (this);
    }
  }

  public void error (Locator locator, final String key)
  {
    if (locator == null)
      locator = this.locator;
    try
    {
      eh.error (new SAXParseException (localizer.message (key), locator));
    }
    catch (final SAXException e)
    {
      throw new WrappedSAXException (e);
    }
  }

  public void error (Locator locator, final String key, final String arg)
  {
    if (locator == null)
      locator = this.locator;
    try
    {
      eh.error (new SAXParseException (localizer.message (key, arg), locator));
    }
    catch (final SAXException e)
    {
      throw new WrappedSAXException (e);
    }
  }

  public Locator saveLocator ()
  {
    return new LocatorImpl (locator);
  }
}
