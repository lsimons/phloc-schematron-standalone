package com.thaiopensource.datatype.xsd;

import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;

import com.thaiopensource.xml.util.StringSplitter;

class ListDatatype extends DatatypeBase implements Measure
{
  private final DatatypeBase itemType;

  ListDatatype (final DatatypeBase itemType)
  {
    this.itemType = itemType;
  }

  @Override
  String getLexicalSpaceKey ()
  {
    return "list_" + itemType.getLexicalSpaceKey ();
  }

  // For a blank string, we want to say we must have
  // "a whitespace-delimited list with length greater than or equal to 1"
  // rather than
  // "a list of XML NMTOKENs with length greater than or equal to 1"
  @Override
  String getDescriptionForRestriction ()
  {
    return getLexicalSpaceDescription ("list");
  }

  @Override
  Object getValue (final String str, final ValidationContext vc) throws DatatypeException
  {
    final String [] tokens = StringSplitter.split (str);
    final Object [] items = new Object [tokens.length];
    for (int i = 0; i < items.length; i++)
      items[i] = itemType.getValue (tokens[i], vc);
    return items;
  }

  @Override
  boolean lexicallyAllows (final String str)
  {
    final String [] tokens = StringSplitter.split (str);
    for (int i = 0; i < tokens.length; i++)
      if (!itemType.lexicallyAllows (tokens[i]))
        return false;
    return true;
  }

  @Override
  Measure getMeasure ()
  {
    return this;
  }

  public int getLength (final Object obj)
  {
    return ((Object []) obj).length;
  }

  @Override
  public boolean isContextDependent ()
  {
    return itemType.isContextDependent ();
  }

  @Override
  public int getIdType ()
  {
    if (itemType.getIdType () == ID_TYPE_IDREF)
      return ID_TYPE_IDREFS;
    else
      return ID_TYPE_NULL;
  }

  @Override
  public int valueHashCode (final Object obj)
  {
    final Object [] items = (Object []) obj;
    int hc = 0;
    for (final Object item : items)
      hc ^= itemType.valueHashCode (item);
    return hc;
  }

  @Override
  public boolean sameValue (final Object obj1, final Object obj2)
  {
    final Object [] items1 = (Object []) obj1;
    final Object [] items2 = (Object []) obj2;
    if (items1.length != items2.length)
      return false;
    for (int i = 0; i < items1.length; i++)
      if (!itemType.sameValue (items1[i], items2[i]))
        return false;
    return true;
  }
}
