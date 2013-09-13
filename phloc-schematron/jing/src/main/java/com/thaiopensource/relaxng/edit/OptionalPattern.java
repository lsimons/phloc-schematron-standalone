package com.thaiopensource.relaxng.edit;

public class OptionalPattern extends UnaryPattern
{
  public OptionalPattern (final Pattern child)
  {
    super (child);
  }

  @Override
  public <T> T accept (final PatternVisitor <T> visitor)
  {
    return visitor.visitOptional (this);
  }
}
