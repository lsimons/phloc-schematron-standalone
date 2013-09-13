package com.thaiopensource.relaxng.edit;

public class TextPattern extends Pattern
{
  public TextPattern ()
  {}

  @Override
  public <T> T accept (final PatternVisitor <T> visitor)
  {
    return visitor.visitText (this);
  }
}
