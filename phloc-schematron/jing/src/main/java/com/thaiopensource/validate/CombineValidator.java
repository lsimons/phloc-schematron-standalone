package com.thaiopensource.validate;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;

import com.thaiopensource.xml.sax.ForkContentHandler;
import com.thaiopensource.xml.sax.ForkDTDHandler;

public class CombineValidator implements Validator
{
  private final Validator v1;
  private final Validator v2;
  private ContentHandler contentHandler;
  private DTDHandler dtdHandler;

  public CombineValidator (final Validator v1, final Validator v2)
  {
    this.v1 = v1;
    this.v2 = v2;
    createHandlers ();
  }

  public void reset ()
  {
    v1.reset ();
    v2.reset ();
    createHandlers ();
  }

  public ContentHandler getContentHandler ()
  {
    return contentHandler;
  }

  public DTDHandler getDTDHandler ()
  {
    return dtdHandler;
  }

  private void createHandlers ()
  {
    contentHandler = new ForkContentHandler (v1.getContentHandler (), v2.getContentHandler ());
    final DTDHandler d1 = v1.getDTDHandler ();
    final DTDHandler d2 = v2.getDTDHandler ();
    if (d1 != null && d2 != null)
      dtdHandler = new ForkDTDHandler (d1, d2);
    else
      if (d1 != null)
        dtdHandler = d1;
      else
        if (d2 != null)
          dtdHandler = d2;
        else
          dtdHandler = null;
  }
}
