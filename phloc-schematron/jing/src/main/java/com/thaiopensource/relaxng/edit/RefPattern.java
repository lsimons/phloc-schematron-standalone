package com.thaiopensource.relaxng.edit;

public class RefPattern extends AbstractRefPattern
{
  public RefPattern (final String name)
  {
    super (name);
  }

  @Override
  public <T> T accept (final PatternVisitor <T> visitor)
  {
    return visitor.visitRef (this);
  }
}
