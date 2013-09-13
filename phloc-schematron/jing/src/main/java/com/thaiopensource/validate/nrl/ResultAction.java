package com.thaiopensource.validate.nrl;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

abstract class ResultAction extends Action
{
  ResultAction (final ModeUsage modeUsage)
  {
    super (modeUsage);
  }

  abstract void perform (ContentHandler handler, SectionState state) throws SAXException;

  abstract ResultAction changeCurrentMode (Mode mode);
}
