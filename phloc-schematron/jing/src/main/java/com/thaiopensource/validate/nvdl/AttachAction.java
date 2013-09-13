package com.thaiopensource.validate.nvdl;

import org.xml.sax.ContentHandler;

/**
 * Result attach action. Attaches elements to the section.
 */
class AttachAction extends ResultAction
{
  /**
   * Creates an attach action with a given mode usage.
   * 
   * @param modeUsage
   *        The mode usage.
   */
  AttachAction (final ModeUsage modeUsage)
  {
    super (modeUsage);
  }

  /**
   * Performs this action on the section state.
   * 
   * @param handler
   *        ???
   * @param state
   *        The section state.
   */
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

  /**
   * Get a new attach action with a mode usage with the current mode changed.
   * This is useful when we have modes extending other modes as we need to get
   * the actions from the base mode as actions on the new mode.
   */
  @Override
  ResultAction changeCurrentMode (final Mode mode)
  {
    return new AttachAction (getModeUsage ().changeCurrentMode (mode));
  }
}
