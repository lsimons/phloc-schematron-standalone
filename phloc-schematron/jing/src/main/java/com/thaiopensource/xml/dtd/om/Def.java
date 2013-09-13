package com.thaiopensource.xml.dtd.om;

public abstract class Def extends TopLevel
{
  private final String name;

  protected Def (final String name)
  {
    this.name = name;
  }

  public String getName ()
  {
    return name;
  }
}
