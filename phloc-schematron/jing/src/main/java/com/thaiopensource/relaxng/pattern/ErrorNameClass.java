package com.thaiopensource.relaxng.pattern;

import com.thaiopensource.xml.util.Name;

class ErrorNameClass implements NameClass
{
  public boolean contains (final Name name)
  {
    return false;
  }

  public int containsSpecificity (final Name name)
  {
    return SPECIFICITY_NONE;
  }

  public void accept (final NameClassVisitor visitor)
  {
    visitor.visitError ();
  }

  public boolean isOpen ()
  {
    return false;
  }
}
