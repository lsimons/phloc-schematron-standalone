package com.thaiopensource.xml.sax;

import org.xml.sax.DTDHandler;
import org.xml.sax.SAXException;

public class ForkDTDHandler implements DTDHandler
{
  private final DTDHandler dh1;
  private final DTDHandler dh2;

  public ForkDTDHandler (final DTDHandler dh1, final DTDHandler dh2)
  {
    this.dh1 = dh1;
    this.dh2 = dh2;
  }

  public void notationDecl (final String name, final String publicId, final String systemId) throws SAXException
  {
    dh1.notationDecl (name, publicId, systemId);
    dh2.notationDecl (name, publicId, systemId);
  }

  public void unparsedEntityDecl (final String name,
                                  final String publicId,
                                  final String systemId,
                                  final String notationName) throws SAXException
  {
    dh1.unparsedEntityDecl (name, publicId, systemId, notationName);
    dh2.unparsedEntityDecl (name, publicId, systemId, notationName);
  }
}
