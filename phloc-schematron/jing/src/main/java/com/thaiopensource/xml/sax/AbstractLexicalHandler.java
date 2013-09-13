package com.thaiopensource.xml.sax;

import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

public class AbstractLexicalHandler implements LexicalHandler
{
  public void startDTD (final String s, final String s1, final String s2) throws SAXException
  {}

  public void endDTD () throws SAXException
  {}

  public void startEntity (final String s) throws SAXException
  {}

  public void endEntity (final String s) throws SAXException
  {}

  public void startCDATA () throws SAXException
  {}

  public void endCDATA () throws SAXException
  {}

  public void comment (final char [] chars, final int start, final int length) throws SAXException
  {}
}
