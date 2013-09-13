package com.thaiopensource.relaxng.edit;

public class ElementPattern extends NameClassedPattern
{
  public ElementPattern (final NameClass nameClass, final Pattern child)
  {
    super (nameClass, child);
  }

  @Override
  public <T> T accept (final PatternVisitor <T> visitor)
  {
    return visitor.visitElement (this);
  }
}
