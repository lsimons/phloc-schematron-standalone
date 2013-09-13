package com.thaiopensource.xml.infer;

import java.io.IOException;
import java.util.Map;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.thaiopensource.datatype.DatatypeLibraryLoader;
import com.thaiopensource.resolver.xml.sax.SAXResolver;
import com.thaiopensource.util.UriOrFile;
import com.thaiopensource.xml.util.Name;

public class TestDriver
{
  static public void main (final String [] args) throws SAXException, IOException
  {
    final InferHandler handler = new InferHandler (new DatatypeLibraryLoader ());
    final SAXResolver resolver = new SAXResolver ();
    final XMLReader xr = resolver.createXMLReader ();
    xr.setContentHandler (handler);
    for (final String arg : args)
      xr.parse (new InputSource (UriOrFile.toUri (arg)));
    final Schema schema = handler.getSchema ();
    for (final Map.Entry <Name, ElementDecl> entry : schema.getElementDecls ().entrySet ())
    {
      final Name name = entry.getKey ();
      String ns = name.getNamespaceUri ();
      if (!ns.equals (""))
        System.out.print ("{" + ns + "}");
      System.out.print (name.getLocalName ());
      System.out.print (" = ");
      final ElementDecl elementDecl = entry.getValue ();
      final Particle particle = elementDecl.getContentModel ();
      if (particle != null)
        System.out.println (ParticleDumper.toString (particle, ns));
      else
        System.out.println ("xsd:" + elementDecl.getDatatype ().getLocalName ());
      for (final Map.Entry <Name, AttributeDecl> attEntry : elementDecl.getAttributeDecls ().entrySet ())
      {
        System.out.print ("  @");
        final AttributeDecl att = attEntry.getValue ();
        final Name attName = attEntry.getKey ();
        ns = attName.getNamespaceUri ();
        if (!ns.equals (""))
          System.out.print ("{" + ns + "}");
        System.out.print (attName.getLocalName ());
        final Name typeName = att.getDatatype ();
        if (typeName == null)
          System.out.print (" string");
        else
          System.out.print (" xsd:" + typeName.getLocalName ());
        if (att.isOptional ())
          System.out.println (" optional");
        else
          System.out.println (" required");
      }
    }
  }
}
