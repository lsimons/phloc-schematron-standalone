package com.thaiopensource.validate.nrl;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.thaiopensource.validate.Schema;

interface SectionState
{
  /**
   * @param modeUsage
   * @param handler
   *        may be null
   */
  void addChildMode (ModeUsage modeUsage, ContentHandler handler);

  void addValidator (Schema schema, ModeUsage modeUsage);

  /**
   * @param handler
   *        must not be null
   */
  void addActiveHandler (ContentHandler handler, ModeUsage attributeModeUsage);

  void addAttributeValidationModeUsage (ModeUsage modeUsage);

  void reject () throws SAXException;
}
