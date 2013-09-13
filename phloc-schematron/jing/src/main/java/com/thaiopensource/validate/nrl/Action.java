package com.thaiopensource.validate.nrl;

abstract class Action
{
  private final ModeUsage modeUsage;

  Action (final ModeUsage modeUsage)
  {
    this.modeUsage = modeUsage;
  }

  ModeUsage getModeUsage ()
  {
    return modeUsage;
  }

  @Override
  public boolean equals (final Object obj)
  {
    return obj != null && obj.getClass () == getClass () && ((Action) obj).modeUsage.equals (modeUsage);
  }

  @Override
  public int hashCode ()
  {
    return getClass ().hashCode () ^ modeUsage.hashCode ();
  }
}
