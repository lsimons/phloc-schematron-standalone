package com.thaiopensource.relaxng.edit;

public class EmptyPattern extends Pattern
{
  public EmptyPattern ()
  {}

  @Override
  public <T> T accept (final PatternVisitor <T> visitor)
  {
    return visitor.visitEmpty (this);
  }
}
