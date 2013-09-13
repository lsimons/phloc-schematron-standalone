package com.thaiopensource.validate.nrl;

import org.xml.sax.ContentHandler;

class AttachAction extends ResultAction
{
  AttachAction (final ModeUsage modeUsage)
  {
    super (modeUsage);
  }

  @Override
  void perform (final ContentHandler handler, final SectionState state)
  {
    final ModeUsage modeUsage = getModeUsage ();
    if (handler != null)
      state.addActiveHandler (handler, modeUsage);
    else
      state.addAttributeValidationModeUsage (modeUsage);
    state.addChildMode (modeUsage, handler);
  }

  @Override
  ResultAction changeCurrentMode (final Mode mode)
  {
    return new AttachAction (getModeUsage ().changeCurrentMode (mode));
  }
}
