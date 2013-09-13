package com.thaiopensource.relaxng.pattern;

import com.thaiopensource.xml.util.Name;

class SimpleNameClass implements NameClass
{

  private final Name name;

  SimpleNameClass (final Name name)
  {
    this.name = name;
  }

  public boolean contains (final Name name)
  {
    return this.name.equals (name);
  }

  public int containsSpecificity (final Name name)
  {
    return contains (name) ? SPECIFICITY_NAME : SPECIFICITY_NONE;
  }

  @Override
  public int hashCode ()
  {
    return name.hashCode ();
  }

  @Override
  public boolean equals (final Object obj)
  {
    if (obj == null || !(obj instanceof SimpleNameClass))
      return false;
    final SimpleNameClass other = (SimpleNameClass) obj;
    return name.equals (other.name);
  }

  Name getName ()
  {
    return name;
  }

  public void accept (final NameClassVisitor visitor)
  {
    visitor.visitName (name);
  }

  public boolean isOpen ()
  {
    return false;
  }
}
