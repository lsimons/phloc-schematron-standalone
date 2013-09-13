package com.thaiopensource.relaxng.output.xsd;

import java.util.HashSet;
import java.util.Set;

class Guide
{
  private boolean defaultGroupEnableAbstractElements;
  private final Set <String> nonDefaultGroupSet = new HashSet <String> ();

  Guide (final boolean defaultGroupEnableAbstractElements)
  {
    this.defaultGroupEnableAbstractElements = defaultGroupEnableAbstractElements;
  }

  void setDefaultGroupEnableAbstractElements (final boolean defaultGroupEnableAbstractElements)
  {
    this.defaultGroupEnableAbstractElements = defaultGroupEnableAbstractElements;
  }

  void setGroupEnableAbstractElement (final String name, final boolean enable)
  {
    if (enable != defaultGroupEnableAbstractElements)
      nonDefaultGroupSet.add (name);
  }

  boolean getGroupEnableAbstractElement (final String name)
  {
    return nonDefaultGroupSet.contains (name) ? !defaultGroupEnableAbstractElements
                                             : defaultGroupEnableAbstractElements;
  }

  boolean getDefaultGroupEnableAbstractElements ()
  {
    return defaultGroupEnableAbstractElements;
  }
}
