package com.thaiopensource.relaxng.parse.sax;

import java.util.HashSet;
import java.util.Set;

import org.relaxng.datatype.ValidationContext;
import org.xml.sax.DTDHandler;
import org.xml.sax.SAXException;

public abstract class DtdContext implements DTDHandler, ValidationContext
{
  private final Set <String> notations;
  private final Set <String> unparsedEntities;

  public DtdContext ()
  {
    notations = new HashSet <String> ();
    unparsedEntities = new HashSet <String> ();
  }

  public DtdContext (final DtdContext dc)
  {
    notations = dc.notations;
    unparsedEntities = dc.unparsedEntities;
  }

  public void notationDecl (final String name, final String publicId, final String systemId) throws SAXException
  {
    notations.add (name);
  }

  public void unparsedEntityDecl (final String name,
                                  final String publicId,
                                  final String systemId,
                                  final String notationName) throws SAXException
  {
    unparsedEntities.add (name);
  }

  public boolean isNotation (final String notationName)
  {
    return notations.contains (notationName);
  }

  public boolean isUnparsedEntity (final String entityName)
  {
    return unparsedEntities.contains (entityName);
  }

  public void clearDtdContext ()
  {
    notations.clear ();
    unparsedEntities.clear ();
  }
}
