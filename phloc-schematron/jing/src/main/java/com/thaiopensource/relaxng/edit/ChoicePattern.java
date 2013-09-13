package com.thaiopensource.relaxng.edit;

public class ChoicePattern extends CompositePattern
{
  @Override
  public <T> T accept (final PatternVisitor <T> visitor)
  {
    return visitor.visitChoice (this);
  }
}
