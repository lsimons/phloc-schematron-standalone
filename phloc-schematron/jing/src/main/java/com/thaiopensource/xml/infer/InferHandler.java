package com.thaiopensource.xml.infer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.relaxng.datatype.DatatypeLibraryFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.thaiopensource.xml.util.Name;

public class InferHandler extends DefaultHandler
{
  private final Map <Name, ElementDeclInferrer> inferrerMap = new HashMap <Name, ElementDeclInferrer> ();
  private OpenElement openElement = null;
  private final Set <Name> startSet = new HashSet <Name> ();
  private final List <Name> attributeNames = new Vector <Name> ();
  private final DatatypeRepertoire datatypes;
  private final StringBuffer textBuffer = new StringBuffer ();
  private final Set <String> usedNamespaceUris = new HashSet <String> ();
  private final Schema schema = new Schema ();
  private final Set <String> assignedPrefixes = new HashSet <String> ();

  private static class OpenElement
  {
    final OpenElement parent;
    final ElementDeclInferrer inferrer;

    public OpenElement (final OpenElement parent, final ElementDeclInferrer inferrer)
    {
      this.parent = parent;
      this.inferrer = inferrer;
    }
  }

  @Override
  public void startElement (final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException
  {
    final Name name = makeName (uri, localName);
    if (openElement == null)
      startSet.add (name);
    else
    {
      if (textBuffer.length () > 0)
      {
        if (!DatatypeInferrer.isWhiteSpace (textBuffer.toString ()))
          openElement.inferrer.addText ();
        textBuffer.setLength (0);
      }
      openElement.inferrer.addElement (name);
    }
    for (int i = 0, len = attributes.getLength (); i < len; i++)
      attributeNames.add (makeName (attributes.getURI (i), attributes.getLocalName (i)));
    ElementDeclInferrer inferrer = inferrerMap.get (name);
    if (inferrer == null)
    {
      inferrer = new ElementDeclInferrer (datatypes, attributeNames);
      inferrerMap.put (name, inferrer);
    }
    else
      inferrer.addAttributeNames (attributeNames);
    for (int i = 0, len = attributes.getLength (); i < len; i++)
      inferrer.addAttributeValue (attributeNames.get (i), attributes.getValue (i));
    attributeNames.clear ();
    openElement = new OpenElement (openElement, inferrer);
  }

  @Override
  public void startPrefixMapping (final String prefix, final String uri) throws SAXException
  {
    if (prefix != null &&
        !prefix.equals ("") &&
        schema.getPrefixMap ().get (uri) == null &&
        !assignedPrefixes.contains (prefix))
    {
      assignedPrefixes.add (prefix);
      schema.getPrefixMap ().put (uri, prefix);
    }
  }

  private Name makeName (final String uri, final String localName)
  {
    if (!uri.equals (""))
      usedNamespaceUris.add (uri);
    return new Name (uri, localName);
  }

  @Override
  public void characters (final char ch[], final int start, final int length) throws SAXException
  {
    if (openElement.inferrer.wantValue ())
      textBuffer.append (ch, start, length);
    else
    {
      for (int i = 0; i < length; i++)
        switch (ch[start + i])
        {
          case ' ':
          case '\t':
          case '\n':
          case '\r':
            break;
          default:
            openElement.inferrer.addText ();
            return;
        }
    }
  }

  @Override
  public void endElement (final String uri, final String localName, final String qName) throws SAXException
  {
    if (openElement.inferrer.wantValue ())
    {
      openElement.inferrer.addValue (textBuffer.toString ());
      textBuffer.setLength (0);
    }
    else
      openElement.inferrer.endSequence ();
    openElement = openElement.parent;
  }

  public Schema getSchema ()
  {
    for (final Map.Entry <Name, ElementDeclInferrer> entry : inferrerMap.entrySet ())
    {
      final ElementDecl decl = (entry.getValue ()).infer ();
      final Name name = entry.getKey ();
      schema.getElementDecls ().put (name, decl);
    }
    schema.setStart (makeStart ());
    schema.getPrefixMap ().keySet ().retainAll (usedNamespaceUris);
    return schema;
  }

  private Particle makeStart ()
  {
    Particle start = null;
    for (final Name name : startSet)
    {
      final Particle tem = new ElementParticle (name);
      if (start == null)
        start = tem;
      else
        start = new ChoiceParticle (start, tem);
    }
    return start;
  }

  public InferHandler (final DatatypeLibraryFactory factory)
  {
    this.datatypes = new DatatypeRepertoire (factory);
  }
}
