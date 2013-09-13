package com.thaiopensource.validate.nrl;

import org.xml.sax.ContentHandler;

class UnwrapAction extends ResultAction
{
  UnwrapAction (final ModeUsage modeUsage)
  {
    super (modeUsage);
  }

  @Override
  void perform (final ContentHandler handler, final SectionState state)
  {
    state.addChildMode (getModeUsage (), handler);
  }

  @Override
  ResultAction changeCurrentMode (final Mode mode)
  {
    return new UnwrapAction (getModeUsage ().changeCurrentMode (mode));
  }
}
