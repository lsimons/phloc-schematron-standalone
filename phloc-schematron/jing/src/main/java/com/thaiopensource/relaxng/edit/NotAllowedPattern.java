package com.thaiopensource.relaxng.edit;

public class NotAllowedPattern extends Pattern
{
  public NotAllowedPattern ()
  {}

  @Override
  public <T> T accept (final PatternVisitor <T> visitor)
  {
    return visitor.visitNotAllowed (this);
  }
}
