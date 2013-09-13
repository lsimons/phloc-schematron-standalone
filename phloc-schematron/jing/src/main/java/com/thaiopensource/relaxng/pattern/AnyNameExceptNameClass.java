package com.thaiopensource.relaxng.pattern;

import com.thaiopensource.xml.util.Name;

class AnyNameExceptNameClass implements NameClass
{

  private final NameClass nameClass;

  AnyNameExceptNameClass (final NameClass nameClass)
  {
    this.nameClass = nameClass;
  }

  public boolean contains (final Name name)
  {
    return !nameClass.contains (name);
  }

  public int containsSpecificity (final Name name)
  {
    return contains (name) ? SPECIFICITY_ANY_NAME : SPECIFICITY_NONE;
  }

  @Override
  public boolean equals (final Object obj)
  {
    if (obj == null || !(obj instanceof AnyNameExceptNameClass))
      return false;
    return nameClass.equals (((AnyNameExceptNameClass) obj).nameClass);
  }

  @Override
  public int hashCode ()
  {
    return ~nameClass.hashCode ();
  }

  public void accept (final NameClassVisitor visitor)
  {
    visitor.visitAnyNameExcept (nameClass);
  }

  public boolean isOpen ()
  {
    return true;
  }
}
