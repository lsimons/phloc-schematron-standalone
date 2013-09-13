package com.thaiopensource.relaxng.edit;

public class AttributePattern extends NameClassedPattern
{
  public AttributePattern (final NameClass nameClass, final Pattern child)
  {
    super (nameClass, child);
  }

  @Override
  public <T> T accept (final PatternVisitor <T> visitor)
  {
    return visitor.visitAttribute (this);
  }
}
