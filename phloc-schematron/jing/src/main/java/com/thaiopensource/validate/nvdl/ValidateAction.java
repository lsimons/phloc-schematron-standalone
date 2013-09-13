package com.thaiopensource.validate.nvdl;

import org.xml.sax.SAXException;

import com.thaiopensource.validate.Schema;

/**
 * Validate no result action.
 */
class ValidateAction extends NoResultAction
{
  /**
   * The schema to validate with.
   */
  private final Schema schema;

  /**
   * Creates a validate action.
   * 
   * @param modeUsage
   *        The mode usage.
   * @param schema
   *        The schema.
   */
  ValidateAction (final ModeUsage modeUsage, final Schema schema)
  {
    super (modeUsage);
    this.schema = schema;
  }

  /**
   * Perform this action on the section state.
   * 
   * @param state
   *        the section state.
   */
  @Override
  void perform (final SectionState state) throws SAXException
  {
    state.addValidator (schema, getModeUsage ());
  }

  /**
   * Get a new validate action with a mode usage with the current mode changed.
   * This is useful when we have modes extending other modes as we need to get
   * the actions from the base mode as actions on the new mode.
   */
  @Override
  NoResultAction changeCurrentMode (final Mode mode)
  {
    return new ValidateAction (getModeUsage ().changeCurrentMode (mode), schema);
  }

  /**
   * Checks if this action is equal with a given action.
   */
  @Override
  public boolean equals (final Object obj)
  {
    return super.equals (obj) && schema.equals (((ValidateAction) obj).schema);
  }

  /**
   * Computes a hashCode.
   */
  @Override
  public int hashCode ()
  {
    return super.hashCode () ^ schema.hashCode ();
  }
}
