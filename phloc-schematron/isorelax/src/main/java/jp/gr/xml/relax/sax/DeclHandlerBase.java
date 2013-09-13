package jp.gr.xml.relax.sax;

import org.xml.sax.SAXException;
import org.xml.sax.ext.DeclHandler;

/**
 * Base class of DeclHandler
 * 
 * @since Feb. 18, 2001
 * @version Feb. 24, 2001
 * @author ASAMI, Tomoharu (asami@zeomtech.com)
 */
public class DeclHandlerBase implements DeclHandler
{
  public void elementDecl (final String name, final String model) throws SAXException
  {}

  public void attributeDecl (final String eName,
                             final String aName,
                             final String type,
                             final String valueDefault,
                             final String value) throws SAXException
  {}

  public void internalEntityDecl (final String name, final String value) throws SAXException
  {}

  public void externalEntityDecl (final String name, final String publicId, final String systemId) throws SAXException
  {}
}
