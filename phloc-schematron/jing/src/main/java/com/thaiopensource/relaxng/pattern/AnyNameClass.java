package com.thaiopensource.relaxng.pattern;

import com.thaiopensource.xml.util.Name;

class AnyNameClass implements NameClass
{

  public boolean contains (final Name name)
  {
    return true;
  }

  public int containsSpecificity (final Name name)
  {
    return SPECIFICITY_ANY_NAME;
  }

  @Override
  public boolean equals (final Object obj)
  {
    return obj != null && obj instanceof AnyNameClass;
  }

  @Override
  public int hashCode ()
  {
    return AnyNameClass.class.hashCode ();
  }

  public void accept (final NameClassVisitor visitor)
  {
    visitor.visitAnyName ();
  }

  public boolean isOpen ()
  {
    return true;
  }
}
