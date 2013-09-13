package com.thaiopensource.relaxng.pattern;

import com.thaiopensource.xml.util.Name;

class ChoiceNameClass implements NameClass
{

  private final NameClass nameClass1;
  private final NameClass nameClass2;

  ChoiceNameClass (final NameClass nameClass1, final NameClass nameClass2)
  {
    this.nameClass1 = nameClass1;
    this.nameClass2 = nameClass2;
  }

  public boolean contains (final Name name)
  {
    return (nameClass1.contains (name) || nameClass2.contains (name));
  }

  public int containsSpecificity (final Name name)
  {
    return Math.max (nameClass1.containsSpecificity (name), nameClass2.containsSpecificity (name));
  }

  @Override
  public int hashCode ()
  {
    return nameClass1.hashCode () ^ nameClass2.hashCode ();
  }

  @Override
  public boolean equals (final Object obj)
  {
    if (obj == null || !(obj instanceof ChoiceNameClass))
      return false;
    final ChoiceNameClass other = (ChoiceNameClass) obj;
    return (nameClass1.equals (other.nameClass1) && nameClass2.equals (other.nameClass2));
  }

  public void accept (final NameClassVisitor visitor)
  {
    visitor.visitChoice (nameClass1, nameClass2);
  }

  public boolean isOpen ()
  {
    return nameClass1.isOpen () || nameClass2.isOpen ();
  }
}
