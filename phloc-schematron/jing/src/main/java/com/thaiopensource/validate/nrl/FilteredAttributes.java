package com.thaiopensource.validate.nrl;

import org.xml.sax.Attributes;

class FilteredAttributes implements Attributes
{
  private final Attributes attributes;
  private final IntSet indexSet;
  private int [] reverseIndexMap;

  public FilteredAttributes (final IntSet indexSet, final Attributes attributes)
  {
    this.indexSet = indexSet;
    this.attributes = attributes;
  }

  private int reverseIndex (final int k)
  {
    if (reverseIndexMap == null)
    {
      reverseIndexMap = new int [attributes.getLength ()];
      for (int i = 0, len = indexSet.size (); i < len; i++)
        reverseIndexMap[indexSet.get (i)] = i + 1;
    }
    return reverseIndexMap[k] - 1;
  }

  public int getLength ()
  {
    return indexSet.size ();
  }

  public String getURI (final int index)
  {
    if (index < 0 || index >= indexSet.size ())
      return null;
    return attributes.getURI (indexSet.get (index));
  }

  public String getLocalName (final int index)
  {
    if (index < 0 || index >= indexSet.size ())
      return null;
    return attributes.getLocalName (indexSet.get (index));
  }

  public String getQName (final int index)
  {
    if (index < 0 || index >= indexSet.size ())
      return null;
    return attributes.getQName (indexSet.get (index));
  }

  public String getType (final int index)
  {
    if (index < 0 || index >= indexSet.size ())
      return null;
    return attributes.getType (indexSet.get (index));
  }

  public String getValue (final int index)
  {
    if (index < 0 || index >= indexSet.size ())
      return null;
    return attributes.getValue (indexSet.get (index));
  }

  public int getIndex (final String uri, final String localName)
  {
    final int n = attributes.getIndex (uri, localName);
    if (n < 0)
      return n;
    return reverseIndex (n);
  }

  public int getIndex (final String qName)
  {
    final int n = attributes.getIndex (qName);
    if (n < 0)
      return n;
    return reverseIndex (n);
  }

  private int getRealIndex (final String uri, final String localName)
  {
    final int index = attributes.getIndex (uri, localName);
    if (index < 0 || reverseIndex (index) < 0)
      return -1;
    return index;
  }

  private int getRealIndex (final String qName)
  {
    final int index = attributes.getIndex (qName);
    if (index < 0 || reverseIndex (index) < 0)
      return -1;
    return index;
  }

  public String getType (final String uri, final String localName)
  {
    return attributes.getType (getRealIndex (uri, localName));
  }

  public String getValue (final String uri, final String localName)
  {
    return attributes.getValue (getRealIndex (uri, localName));
  }

  public String getType (final String qName)
  {
    return attributes.getType (getRealIndex (qName));
  }

  public String getValue (final String qName)
  {
    return attributes.getValue (getRealIndex (qName));
  }

}
