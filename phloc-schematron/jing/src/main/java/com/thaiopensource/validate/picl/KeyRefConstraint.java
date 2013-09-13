package com.thaiopensource.validate.picl;

import java.util.Enumeration;
import java.util.Vector;

import org.xml.sax.Locator;

class KeyRefConstraint extends KeyConstraint
{
  private final Pattern ref;

  static class RefSelectionHandler extends SelectedValueHandler
  {
    private final KeyIndex index;

    RefSelectionHandler (final KeyConstraint.KeyIndex index)
    {
      this.index = index;
    }

    @Override
    void select (final ErrorContext ec, Locator locator, final Object value, final String representation)
    {
      final KeyInfo info = index.lookupCreate (value);
      if (info.firstKeyLocator == null)
      {
        if (info.pendingRefLocators == null)
          info.pendingRefLocators = new Vector <Locator> ();
        if (locator == null)
          locator = ec.saveLocator ();
        info.pendingRefLocators.addElement (locator);
      }
      if (info.representation == null)
        info.representation = representation;
    }

    @Override
    public void selectComplete (final ErrorContext ec)
    {
      for (final Enumeration e = index.keys (); e.hasMoreElements ();)
      {
        final Object key = e.nextElement ();
        final KeyInfo info = index.lookupCreate (key);
        if (info.pendingRefLocators == null)
          continue;
        for (int i = 0, len = info.pendingRefLocators.size (); i < len; i++)
        {
          final Locator loc = info.pendingRefLocators.elementAt (i);
          ec.error (loc, "undefined_key", info.representation);
        }
      }
    }
  }

  KeyRefConstraint (final Pattern key, final Pattern ref)
  {
    super (key);
    this.ref = ref;
  }

  @Override
  void activate (final PatternManager pm, final KeyIndex index)
  {
    super.activate (pm, index);
    pm.registerPattern (ref, new ValueSelectionHandler (new RefSelectionHandler (index)));
  }
}
