package com.thaiopensource.relaxng.edit;

import java.util.List;
import java.util.Vector;

public class GrammarPattern extends Pattern implements Container
{
  private final List <Component> components = new Vector <Component> ();

  public List <Component> getComponents ()
  {
    return components;
  }

  @Override
  public <T> T accept (final PatternVisitor <T> visitor)
  {
    return visitor.visitGrammar (this);
  }

  public void componentsAccept (final ComponentVisitor <?> visitor)
  {
    for (final Component c : components)
      c.accept (visitor);
  }
}
