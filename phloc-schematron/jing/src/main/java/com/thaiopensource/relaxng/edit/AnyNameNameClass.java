package com.thaiopensource.relaxng.edit;

public class AnyNameNameClass extends OpenNameClass
{
  public AnyNameNameClass ()
  {}

  public AnyNameNameClass (final NameClass except)
  {
    super (except);
  }

  @Override
  public <T> T accept (final NameClassVisitor <T> visitor)
  {
    return visitor.visitAnyName (this);
  }
}
