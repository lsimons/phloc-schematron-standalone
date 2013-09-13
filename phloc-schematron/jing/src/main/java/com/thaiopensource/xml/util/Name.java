package com.thaiopensource.xml.util;

public final class Name
{
  final private String namespaceUri;
  final private String localName;
  final private int hc;

  public Name (final String namespaceUri, final String localName)
  {
    this.namespaceUri = namespaceUri;
    this.localName = localName;
    this.hc = namespaceUri.hashCode () ^ localName.hashCode ();
  }

  public String getNamespaceUri ()
  {
    return namespaceUri;
  }

  public String getLocalName ()
  {
    return localName;
  }

  @Override
  public boolean equals (final Object obj)
  {
    if (!(obj instanceof Name))
      return false;
    final Name other = (Name) obj;
    return (this.hc == other.hc && this.namespaceUri.equals (other.namespaceUri) && this.localName.equals (other.localName));
  }

  @Override
  public int hashCode ()
  {
    return hc;
  }

  // We include this, but don't derive from Comparator<Name> to avoid a
  // dependency on Java 5.
  static public int compare (final Name n1, final Name n2)
  {
    final int ret = n1.namespaceUri.compareTo (n2.namespaceUri);
    if (ret != 0)
      return ret;
    return n1.localName.compareTo (n2.localName);
  }
}
