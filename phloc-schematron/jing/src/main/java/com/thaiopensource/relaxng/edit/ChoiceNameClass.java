package com.thaiopensource.relaxng.edit;

import java.util.ArrayList;
import java.util.List;

public class ChoiceNameClass extends NameClass
{
  private final List <NameClass> children = new ArrayList <NameClass> ();

  public List <NameClass> getChildren ()
  {
    return children;
  }

  @Override
  public <T> T accept (final NameClassVisitor <T> visitor)
  {
    return visitor.visitChoice (this);
  }

  public void childrenAccept (final NameClassVisitor <?> visitor)
  {
    for (final NameClass nc : children)
      nc.accept (visitor);
  }
}
