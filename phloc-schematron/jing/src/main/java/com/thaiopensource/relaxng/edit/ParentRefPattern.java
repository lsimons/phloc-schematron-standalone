package com.thaiopensource.relaxng.edit;

public class ParentRefPattern extends AbstractRefPattern
{
  public ParentRefPattern (final String name)
  {
    super (name);
  }

  @Override
  public <T> T accept (final PatternVisitor <T> visitor)
  {
    return visitor.visitParentRef (this);
  }
}
