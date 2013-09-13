package com.thaiopensource.relaxng.edit;

import java.util.List;
import java.util.Vector;

public class DivComponent extends Component implements Container
{
  private final List <Component> components = new Vector <Component> ();

  public List <Component> getComponents ()
  {
    return components;
  }

  @Override
  public <T> T accept (final ComponentVisitor <T> visitor)
  {
    return visitor.visitDiv (this);
  }

  public void componentsAccept (final ComponentVisitor <?> visitor)
  {
    for (final Component c : components)
      c.accept (visitor);
  }
}
