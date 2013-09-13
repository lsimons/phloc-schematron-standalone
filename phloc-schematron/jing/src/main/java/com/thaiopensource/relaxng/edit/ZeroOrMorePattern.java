package com.thaiopensource.relaxng.edit;

public class ZeroOrMorePattern extends UnaryPattern
{
  public ZeroOrMorePattern (final Pattern child)
  {
    super (child);
  }

  @Override
  public <T> T accept (final PatternVisitor <T> visitor)
  {
    return visitor.visitZeroOrMore (this);
  }
}
