package com.thaiopensource.relaxng.pattern;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.thaiopensource.xml.util.Name;

/**
 * Computes the normalized intersection of zero or more name classes.
 */
public class IntersectionNameClassNormalizer extends AbstractNameClassNormalizer
{
  private final List <NameClass> nameClasses = new ArrayList <NameClass> ();

  public void add (final NameClass nc)
  {
    nameClasses.add (nc);
  }

  @Override
  protected void accept (final NameClassVisitor visitor)
  {
    for (final NameClass nameClass : nameClasses)
      (nameClass).accept (visitor);
  }

  @Override
  protected boolean contains (final Name name)
  {
    final Iterator <NameClass> iter = nameClasses.iterator ();
    if (!iter.hasNext ())
      return false;
    for (;;)
    {
      if (!(iter.next ()).contains (name))
        return false;
      if (!iter.hasNext ())
        break;
    }
    return true;
  }
}
