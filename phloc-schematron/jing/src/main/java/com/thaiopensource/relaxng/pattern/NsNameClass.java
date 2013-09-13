package com.thaiopensource.relaxng.pattern;

import com.thaiopensource.xml.util.Name;

class NsNameClass implements NameClass
{

  private final String namespaceUri;

  NsNameClass (final String namespaceUri)
  {
    this.namespaceUri = namespaceUri;
  }

  public boolean contains (final Name name)
  {
    return this.namespaceUri.equals (name.getNamespaceUri ());
  }

  public int containsSpecificity (final Name name)
  {
    return contains (name) ? SPECIFICITY_NS_NAME : SPECIFICITY_NONE;
  }

  @Override
  public int hashCode ()
  {
    return namespaceUri.hashCode ();
  }

  @Override
  public boolean equals (final Object obj)
  {
    if (obj == null || !(obj instanceof NsNameClass))
      return false;
    return namespaceUri.equals (((NsNameClass) obj).namespaceUri);
  }

  public void accept (final NameClassVisitor visitor)
  {
    visitor.visitNsName (namespaceUri);
  }

  public boolean isOpen ()
  {
    return true;
  }

  public String getNamespaceUri ()
  {
    return namespaceUri;
  }
}
