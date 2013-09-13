package com.thaiopensource.validate.nrl;

class AllowAction extends NoResultAction
{
  AllowAction (final ModeUsage modeUsage)
  {
    super (modeUsage);
  }

  @Override
  void perform (final SectionState state)
  {
    state.addChildMode (getModeUsage (), null);
    state.addAttributeValidationModeUsage (getModeUsage ());
  }

  @Override
  NoResultAction changeCurrentMode (final Mode mode)
  {
    return new AllowAction (getModeUsage ().changeCurrentMode (mode));
  }
}
