package com.thaiopensource.relaxng.pattern;

import com.thaiopensource.xml.util.Name;

/**
 * Normalizes a name classes.
 */
public class NameClassNormalizer extends AbstractNameClassNormalizer
{
  private NameClass nameClass;

  public NameClassNormalizer (final NameClass nameClass)
  {
    this.nameClass = nameClass;
  }

  @Override
  protected boolean contains (final Name name)
  {
    return nameClass.contains (name);
  }

  @Override
  protected void accept (final NameClassVisitor visitor)
  {
    nameClass.accept (visitor);
  }

  public NameClass getNameClass ()
  {
    return nameClass;
  }

  public void setNameClass (final NameClass nameClass)
  {
    this.nameClass = nameClass;
  }
}
