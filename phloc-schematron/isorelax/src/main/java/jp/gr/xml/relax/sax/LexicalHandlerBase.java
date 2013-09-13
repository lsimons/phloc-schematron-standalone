package jp.gr.xml.relax.sax;

import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

/**
 * Base class of LexicalHandler
 * 
 * @since Feb. 18, 2001
 * @version Feb. 24, 2001
 * @author ASAMI, Tomoharu (asami@zeomtech.com)
 */
public class LexicalHandlerBase implements LexicalHandler
{
  public void startDTD (final String name, final String publidId, final String systemID) throws SAXException
  {}

  public void endDTD () throws SAXException
  {}

  public void startEntity (final String name) throws SAXException
  {}

  public void endEntity (final String name) throws SAXException
  {}

  public void startCDATA () throws SAXException
  {}

  public void endCDATA () throws SAXException
  {}

  public void comment (final char ch[], final int start, final int length)
  {}
}
