package com.thaiopensource.relaxng.edit;

public class ListPattern extends UnaryPattern
{
  public ListPattern (final Pattern child)
  {
    super (child);
  }

  @Override
  public <T> T accept (final PatternVisitor <T> visitor)
  {
    return visitor.visitList (this);
  }
}
