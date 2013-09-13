package com.thaiopensource.relaxng.pattern;

import org.xml.sax.Locator;
import org.xml.sax.SAXException;

class ElementPattern extends Pattern
{
  private Pattern p;
  private final NameClass origNameClass;
  private NameClass nameClass;
  private boolean expanded = false;
  private boolean checkedRestrictions = false;
  private final Locator loc;

  ElementPattern (final NameClass nameClass, final Pattern p, final Locator loc)
  {
    super (false, ELEMENT_CONTENT_TYPE, combineHashCode (ELEMENT_HASH_CODE, nameClass.hashCode (), p.hashCode ()));
    this.nameClass = nameClass;
    this.origNameClass = nameClass;
    this.p = p;
    this.loc = loc;
  }

  @Override
  void checkRestrictions (final int context, final DuplicateAttributeDetector dad, final Alphabet alpha) throws RestrictionViolationException
  {
    if (alpha != null)
      alpha.addElement (origNameClass);
    if (checkedRestrictions)
      return;
    switch (context)
    {
      case DATA_EXCEPT_CONTEXT:
        throw new RestrictionViolationException ("data_except_contains_element");
      case LIST_CONTEXT:
        throw new RestrictionViolationException ("list_contains_element");
      case ATTRIBUTE_CONTEXT:
        throw new RestrictionViolationException ("attribute_contains_element");
    }
    checkedRestrictions = true;
    try
    {
      p.checkRestrictions (ELEMENT_CONTEXT, new DuplicateAttributeDetector (), null);
    }
    catch (final RestrictionViolationException e)
    {
      checkedRestrictions = false;
      e.maybeSetLocator (loc);
      throw e;
    }
  }

  @Override
  Pattern expand (final SchemaPatternBuilder b)
  {
    if (!expanded)
    {
      expanded = true;
      p = p.expand (b);
      if (p.isNotAllowed ())
        nameClass = new NullNameClass ();
    }
    return this;
  }

  @Override
  boolean samePattern (final Pattern other)
  {
    if (!(other instanceof ElementPattern))
      return false;
    final ElementPattern ep = (ElementPattern) other;
    return nameClass.equals (ep.nameClass) && p == ep.p;
  }

  @Override
  void checkRecursion (final int depth) throws SAXException
  {
    p.checkRecursion (depth + 1);
  }

  @Override
  <T> T apply (final PatternFunction <T> f)
  {
    return f.caseElement (this);
  }

  void setContent (final Pattern p)
  {
    this.p = p;
  }

  Pattern getContent ()
  {
    return p;
  }

  NameClass getNameClass ()
  {
    return nameClass;
  }

  Locator getLocator ()
  {
    return loc;
  }
}
