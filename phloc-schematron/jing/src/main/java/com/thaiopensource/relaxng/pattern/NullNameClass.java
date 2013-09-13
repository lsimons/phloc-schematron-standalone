package com.thaiopensource.relaxng.pattern;

import com.thaiopensource.xml.util.Name;

/**
 * This is used for the name class of an element pattern when the content
 * expands to notAllowed.
 */
class NullNameClass implements NameClass
{
  public boolean contains (final Name name)
  {
    return false;
  }

  public int containsSpecificity (final Name name)
  {
    return SPECIFICITY_NONE;
  }

  @Override
  public int hashCode ()
  {
    return NullNameClass.class.hashCode ();
  }

  @Override
  public boolean equals (final Object obj)
  {
    if (obj == null || !(obj instanceof NullNameClass))
      return false;
    return true;
  }

  public void accept (final NameClassVisitor visitor)
  {
    visitor.visitNull ();
  }

  public boolean isOpen ()
  {
    return false;
  }
}
