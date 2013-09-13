package com.thaiopensource.validate.nrl;

import org.xml.sax.SAXException;

class RejectAction extends NoResultAction
{
  RejectAction (final ModeUsage modeUsage)
  {
    super (modeUsage);
  }

  @Override
  void perform (final SectionState state) throws SAXException
  {
    final ModeUsage modeUsage = getModeUsage ();
    state.reject ();
    state.addChildMode (modeUsage, null);
    state.addAttributeValidationModeUsage (modeUsage);
  }

  @Override
  NoResultAction changeCurrentMode (final Mode mode)
  {
    return new RejectAction (getModeUsage ().changeCurrentMode (mode));
  }
}
