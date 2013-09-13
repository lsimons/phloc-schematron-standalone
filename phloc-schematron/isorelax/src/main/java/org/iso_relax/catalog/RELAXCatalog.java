package org.iso_relax.catalog;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * RELAXCatalog
 * 
 * @since Feb. 23, 2001
 * @version Feb. 23, 2001
 * @author ASAMI, Tomoharu (asami@zeomtech.com)
 */
public class RELAXCatalog
{
  private final Map <String, String> grammars_ = new HashMap <String, String> ();

  public RELAXCatalog () throws ParserConfigurationException, SAXException, IOException
  {
    this ("http://www.iso-relax.org/catalog");
  }

  public RELAXCatalog (final String rootURI) throws ParserConfigurationException, SAXException, IOException
  {

    final String catalogFile = rootURI + "/catalog.xml";
    final SAXParserFactory factory = SAXParserFactory.newInstance ();
    final SAXParser saxParser = factory.newSAXParser ();
    saxParser.parse (catalogFile, new CatalogHandler ());
  }

  public InputSource getGrammar (final String uri)
  {
    final String location = grammars_.get (uri);
    if (location == null)
    {
      return (null);
    }
    return (new InputSource (location));
  }

  class CatalogHandler extends DefaultHandler
  {
    @Override
    public void startElement (final String namespaceURI,
                              final String localName,
                              final String qName,
                              final Attributes atts)
    {
      final String uri = atts.getValue ("uri");
      final String grammar = atts.getValue ("grammar");
      grammars_.put (uri, grammar);
    }
  }
}
