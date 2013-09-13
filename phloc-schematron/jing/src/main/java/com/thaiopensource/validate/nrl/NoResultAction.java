package com.thaiopensource.validate.nrl;

import org.xml.sax.SAXException;

abstract class NoResultAction extends Action
{
  NoResultAction (final ModeUsage modeUsage)
  {
    super (modeUsage);
  }

  abstract void perform (SectionState state) throws SAXException;

  abstract NoResultAction changeCurrentMode (Mode mode);
}
