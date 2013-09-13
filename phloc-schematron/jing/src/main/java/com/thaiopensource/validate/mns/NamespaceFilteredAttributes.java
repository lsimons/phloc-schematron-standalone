package com.thaiopensource.validate.mns;

import org.xml.sax.Attributes;

class NamespaceFilteredAttributes implements Attributes
{
  private final String ns;
  private final boolean keepLocal;
  private final Attributes attributes;
  private final int [] indexMap;
  private final int [] reverseIndexMap;

  public NamespaceFilteredAttributes (final String ns, final boolean keepLocal, final Attributes attributes)
  {
    this.ns = ns;
    this.keepLocal = keepLocal;
    this.attributes = attributes;
    int n = 0;
    for (int i = 0, len = attributes.getLength (); i < len; i++)
      if (keepAttribute (attributes.getURI (i)))
        n++;
    indexMap = new int [n];
    reverseIndexMap = new int [attributes.getLength ()];
    n = 0;
    for (int i = 0, len = attributes.getLength (); i < len; i++)
    {
      if (keepAttribute (attributes.getURI (i)))
      {
        reverseIndexMap[i] = n;
        indexMap[n++] = i;
      }
      else
        reverseIndexMap[i] = -1;
    }
  }

  private boolean keepAttribute (final String uri)
  {
    return uri.equals (ns) || (keepLocal && uri.equals (""));
  }

  public int getLength ()
  {
    return indexMap.length;
  }

  public String getURI (final int index)
  {
    if (index < 0 || index >= indexMap.length)
      return null;
    return attributes.getURI (indexMap[index]);
  }

  public String getLocalName (final int index)
  {
    if (index < 0 || index >= indexMap.length)
      return null;
    return attributes.getLocalName (indexMap[index]);
  }

  public String getQName (final int index)
  {
    if (index < 0 || index >= indexMap.length)
      return null;
    return attributes.getQName (indexMap[index]);
  }

  public String getType (final int index)
  {
    if (index < 0 || index >= indexMap.length)
      return null;
    return attributes.getType (indexMap[index]);
  }

  public String getValue (final int index)
  {
    if (index < 0 || index >= indexMap.length)
      return null;
    return attributes.getValue (indexMap[index]);
  }

  public int getIndex (final String uri, final String localName)
  {
    final int n = attributes.getIndex (uri, localName);
    if (n < 0)
      return n;
    return reverseIndexMap[n];
  }

  public int getIndex (final String qName)
  {
    final int n = attributes.getIndex (qName);
    if (n < 0)
      return n;
    return reverseIndexMap[n];
  }

  public String getType (final String uri, final String localName)
  {
    if (keepAttribute (uri))
      return attributes.getType (uri, localName);
    return null;
  }

  public String getValue (final String uri, final String localName)
  {
    if (keepAttribute (uri))
      return attributes.getValue (uri, localName);
    return null;
  }

  public String getType (final String qName)
  {
    final int i = getIndex (qName);
    if (i < 0)
      return null;
    return getType (i);
  }

  public String getValue (final String qName)
  {
    final int i = getIndex (qName);
    if (i < 0)
      return null;
    return getValue (i);
  }
}
