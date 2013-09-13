package com.thaiopensource.validate.auto;

import java.io.IOException;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.thaiopensource.util.Localizer;
import com.thaiopensource.util.PropertyMap;
import com.thaiopensource.validate.IncorrectSchemaException;
import com.thaiopensource.validate.Schema;

public class AutoSchemaReceiver implements SchemaReceiver
{
  private final PropertyMap properties;
  private final Rewindable rewindable;

  private class Handler extends DefaultHandler implements SchemaFuture
  {
    private final XMLReader xr;
    private SchemaFuture sf = null;
    private Locator locator = null;
    private final Vector <String> prefixMappings = new Vector <String> ();

    private Handler (final XMLReader xr)
    {
      this.xr = xr;
    }

    @Override
    public void setDocumentLocator (final Locator locator)
    {
      this.locator = locator;
    }

    @Override
    public void startPrefixMapping (final String prefix, final String uri)
    {
      prefixMappings.addElement (prefix);
      prefixMappings.addElement (uri);
    }

    @Override
    public void startElement (final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException
    {
      final SchemaReceiverFactory srf = properties.get (SchemaReceiverFactory.PROPERTY);
      final SchemaReceiver sr = srf.createSchemaReceiver (uri, properties);
      if (sr == null)
      {
        final Localizer localizer = new Localizer (AutoSchemaReceiver.class);
        final String detail = ("".equals (uri) ? localizer.message ("no_namespace")
                                              : localizer.message ("unknown_namespace", uri));
        throw new SAXParseException (detail, locator);
      }
      sf = sr.installHandlers (xr);
      rewindable.willNotRewind ();
      ContentHandler contentHandler = xr.getContentHandler ();
      if (contentHandler == null)
        return;
      if (locator != null)
      {
        contentHandler.setDocumentLocator (locator);
        contentHandler = xr.getContentHandler ();
      }
      contentHandler.startDocument ();
      contentHandler = xr.getContentHandler ();
      for (int i = 0, len = prefixMappings.size (); i < len; i += 2)
      {
        contentHandler.startPrefixMapping (prefixMappings.elementAt (i),
                                           prefixMappings.elementAt (i + 1));
        contentHandler = xr.getContentHandler ();
      }
      contentHandler.startElement (uri, localName, qName, attributes);
    }

    public Schema getSchema () throws IncorrectSchemaException, SAXException, IOException
    {
      if (sf == null)
        throw new IncorrectSchemaException ();
      return sf.getSchema ();
    }

    public RuntimeException unwrapException (final RuntimeException e) throws SAXException,
                                                                      IOException,
                                                                      IncorrectSchemaException
    {
      if (sf == null)
        return e;
      return sf.unwrapException (e);
    }
  }

  public AutoSchemaReceiver (final PropertyMap properties, final Rewindable rewindable)
  {
    this.properties = properties;
    this.rewindable = rewindable;
  }

  public SchemaFuture installHandlers (final XMLReader xr)
  {
    final Handler h = new Handler (xr);
    xr.setContentHandler (h);
    return h;
  }
}
