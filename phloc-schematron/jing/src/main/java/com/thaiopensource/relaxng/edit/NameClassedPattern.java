package com.thaiopensource.relaxng.edit;

public abstract class NameClassedPattern extends UnaryPattern
{
  private NameClass nameClass;

  public NameClassedPattern (final NameClass nameClass, final Pattern child)
  {
    super (child);
    this.nameClass = nameClass;
  }

  public NameClass getNameClass ()
  {
    return nameClass;
  }

  public void setNameClass (final NameClass nameClass)
  {
    this.nameClass = nameClass;
  }
}
