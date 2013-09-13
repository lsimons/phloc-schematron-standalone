package com.thaiopensource.relaxng.edit;

public class GroupPattern extends CompositePattern
{
  @Override
  public <T> T accept (final PatternVisitor <T> visitor)
  {
    return visitor.visitGroup (this);
  }
}
