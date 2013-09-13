package com.thaiopensource.relaxng.edit;

public class MixedPattern extends UnaryPattern
{
  public MixedPattern (final Pattern child)
  {
    super (child);
  }

  @Override
  public <T> T accept (final PatternVisitor <T> visitor)
  {
    return visitor.visitMixed (this);
  }
}
