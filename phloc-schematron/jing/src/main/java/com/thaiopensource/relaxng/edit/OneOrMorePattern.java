package com.thaiopensource.relaxng.edit;

public class OneOrMorePattern extends UnaryPattern
{
  public OneOrMorePattern (final Pattern child)
  {
    super (child);
  }

  @Override
  public <T> T accept (final PatternVisitor <T> visitor)
  {
    return visitor.visitOneOrMore (this);
  }
}
