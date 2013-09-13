package com.thaiopensource.validate.nvdl;

/**
 * Base action class.
 */
abstract class Action
{
  /**
   * Use mode when performing this action.
   */
  private final ModeUsage modeUsage;

  /**
   * Creates an action with a given mode usage.
   * 
   * @param modeUsage
   *        The mode usage.
   */
  Action (final ModeUsage modeUsage)
  {
    this.modeUsage = modeUsage;
  }

  /**
   * Getter for the mode usage.
   * 
   * @return The mode usage for this action.
   */
  ModeUsage getModeUsage ()
  {
    return modeUsage;
  }

  /**
   * Checks for equality, we need to have the same action class with the same
   * modeUsage.
   */
  @Override
  public boolean equals (final Object obj)
  {
    return obj != null && obj.getClass () == getClass () && ((Action) obj).modeUsage.equals (modeUsage);
  }

  /**
   * Computes a hashCode for this action.
   */
  @Override
  public int hashCode ()
  {
    return getClass ().hashCode () ^ modeUsage.hashCode ();
  }
}
