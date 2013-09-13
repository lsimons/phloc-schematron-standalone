package com.thaiopensource.relaxng.pattern;

import org.xml.sax.Locator;
import org.xml.sax.SAXException;

class ListPattern extends Pattern
{
  private final Pattern p;
  private final Locator locator;

  ListPattern (final Pattern p, final Locator locator)
  {
    super (false, DATA_CONTENT_TYPE, combineHashCode (LIST_HASH_CODE, p.hashCode ()));
    this.p = p;
    this.locator = locator;
  }

  @Override
  Pattern expand (final SchemaPatternBuilder b)
  {
    final Pattern ep = p.expand (b);
    if (ep != p)
      return b.makeList (ep, locator);
    else
      return this;
  }

  @Override
  void checkRecursion (final int depth) throws SAXException
  {
    p.checkRecursion (depth);
  }

  @Override
  boolean samePattern (final Pattern other)
  {
    return (other instanceof ListPattern && p == ((ListPattern) other).p);
  }

  @Override
  <T> T apply (final PatternFunction <T> f)
  {
    return f.caseList (this);
  }

  @Override
  void checkRestrictions (final int context, final DuplicateAttributeDetector dad, final Alphabet alpha) throws RestrictionViolationException
  {
    switch (context)
    {
      case DATA_EXCEPT_CONTEXT:
        throw new RestrictionViolationException ("data_except_contains_list");
      case START_CONTEXT:
        throw new RestrictionViolationException ("start_contains_list");
      case LIST_CONTEXT:
        throw new RestrictionViolationException ("list_contains_list");
    }
    try
    {
      p.checkRestrictions (LIST_CONTEXT, dad, null);
    }
    catch (final RestrictionViolationException e)
    {
      e.maybeSetLocator (locator);
      throw e;
    }
  }

  Pattern getOperand ()
  {
    return p;
  }
}
