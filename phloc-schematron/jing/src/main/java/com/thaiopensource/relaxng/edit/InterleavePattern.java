package com.thaiopensource.relaxng.edit;

public class InterleavePattern extends CompositePattern
{
  @Override
  public <T> T accept (final PatternVisitor <T> visitor)
  {
    return visitor.visitInterleave (this);
  }
}
