package com.thaiopensource.validate.picl;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.xml.sax.Locator;

class KeyConstraint implements Constraint
{
  private final Pattern key;

  KeyConstraint (final Pattern key)
  {
    this.key = key;
  }

  static class KeyIndex
  {
    private final Hashtable <Object, KeyInfo> table;

    KeyIndex ()
    {
      table = new Hashtable <Object, KeyInfo> ();
    }

    KeyInfo lookupCreate (final Object key)
    {
      KeyInfo info = table.get (key);
      if (info == null)
      {
        info = new KeyInfo ();
        table.put (key, info);
      }
      return info;
    }

    Enumeration <Object> keys ()
    {
      return table.keys ();
    }
  }

  static class KeyInfo
  {
    String representation;
    Locator firstKeyLocator;
    Vector <Locator> pendingRefLocators;
  }

  static class KeySelectionHandler extends SelectedValueHandler
  {
    private final KeyIndex index;

    KeySelectionHandler (final KeyIndex index)
    {
      this.index = index;
    }

    @Override
    void select (final ErrorContext ec, Locator locator, final Object value, final String representation)
    {
      final KeyInfo info = index.lookupCreate (value);
      if (info.firstKeyLocator == null)
      {
        if (locator == null)
          locator = ec.saveLocator ();
        info.firstKeyLocator = locator;
        info.pendingRefLocators = null;
        info.representation = representation;
      }
      else
        ec.error (locator, "duplicate_key", representation);
    }
  }

  public void activate (final PatternManager pm)
  {
    activate (pm, new KeyIndex ());
  }

  void activate (final PatternManager pm, final KeyIndex index)
  {
    pm.registerPattern (key, new ValueSelectionHandler (new KeySelectionHandler (index)));
  }
}
