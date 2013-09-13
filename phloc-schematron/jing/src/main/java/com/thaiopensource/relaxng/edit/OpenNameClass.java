package com.thaiopensource.relaxng.edit;

public abstract class OpenNameClass extends NameClass
{
  private NameClass except;

  public OpenNameClass ()
  {}

  public OpenNameClass (final NameClass except)
  {
    this.except = except;
  }

  public NameClass getExcept ()
  {
    return except;
  }

  public void setExcept (final NameClass except)
  {
    this.except = except;
  }
}
