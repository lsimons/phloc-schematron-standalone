package com.thaiopensource.relaxng.edit;

public abstract class UnaryPattern extends Pattern
{
  private Pattern child;

  public UnaryPattern (final Pattern child)
  {
    this.child = child;
  }

  public Pattern getChild ()
  {
    return child;
  }

  public void setChild (final Pattern child)
  {
    this.child = child;
  }
}
