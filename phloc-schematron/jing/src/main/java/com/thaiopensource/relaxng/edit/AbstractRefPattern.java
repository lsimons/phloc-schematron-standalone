package com.thaiopensource.relaxng.edit;

public abstract class AbstractRefPattern extends Pattern
{
  private String name;

  public AbstractRefPattern (final String name)
  {
    this.name = name;
  }

  public String getName ()
  {
    return name;
  }

  public void setName (final String name)
  {
    this.name = name;
  }
}
