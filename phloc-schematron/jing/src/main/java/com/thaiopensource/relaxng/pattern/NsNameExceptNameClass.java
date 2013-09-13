package com.thaiopensource.relaxng.pattern;

import com.thaiopensource.xml.util.Name;

class NsNameExceptNameClass implements NameClass
{

  private final NameClass nameClass;
  private final String namespaceURI;

  NsNameExceptNameClass (final String namespaceURI, final NameClass nameClass)
  {
    this.namespaceURI = namespaceURI;
    this.nameClass = nameClass;
  }

  public boolean contains (final Name name)
  {
    return (this.namespaceURI.equals (name.getNamespaceUri ()) && !nameClass.contains (name));
  }

  public int containsSpecificity (final Name name)
  {
    return contains (name) ? SPECIFICITY_NS_NAME : SPECIFICITY_NONE;
  }

  @Override
  public boolean equals (final Object obj)
  {
    if (obj == null || !(obj instanceof NsNameExceptNameClass))
      return false;
    final NsNameExceptNameClass other = (NsNameExceptNameClass) obj;
    return (namespaceURI.equals (other.namespaceURI) && nameClass.equals (other.nameClass));
  }

  @Override
  public int hashCode ()
  {
    return namespaceURI.hashCode () ^ nameClass.hashCode ();
  }

  public void accept (final NameClassVisitor visitor)
  {
    visitor.visitNsNameExcept (namespaceURI, nameClass);
  }

  public boolean isOpen ()
  {
    return true;
  }
}
