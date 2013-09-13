package com.thaiopensource.relaxng.edit;

public class Param extends Annotated
{
  private String name;
  private String value;

  public Param (final String name, final String value)
  {
    this.name = name;
    this.value = value;
  }

  public String getName ()
  {
    return name;
  }

  public void setName (final String name)
  {
    this.name = name;
  }

  public String getValue ()
  {
    return value;
  }

  public void setValue (final String value)
  {
    this.value = value;
  }

  @Override
  public boolean mayContainText ()
  {
    return true;
  }
}
