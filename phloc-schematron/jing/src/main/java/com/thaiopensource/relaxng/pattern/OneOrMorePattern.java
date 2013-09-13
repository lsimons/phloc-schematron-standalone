package com.thaiopensource.relaxng.pattern;

import org.xml.sax.SAXException;

class OneOrMorePattern extends Pattern
{
  private final Pattern p;

  OneOrMorePattern (final Pattern p)
  {
    super (p.isNullable (), p.getContentType (), combineHashCode (ONE_OR_MORE_HASH_CODE, p.hashCode ()));
    this.p = p;
  }

  @Override
  Pattern expand (final SchemaPatternBuilder b)
  {
    final Pattern ep = p.expand (b);
    if (ep != p)
      return b.makeOneOrMore (ep);
    else
      return this;
  }

  @Override
  void checkRecursion (final int depth) throws SAXException
  {
    p.checkRecursion (depth);
  }

  @Override
  void checkRestrictions (final int context, final DuplicateAttributeDetector dad, final Alphabet alpha) throws RestrictionViolationException
  {
    switch (context)
    {
      case START_CONTEXT:
        throw new RestrictionViolationException ("start_contains_one_or_more");
      case DATA_EXCEPT_CONTEXT:
        throw new RestrictionViolationException ("data_except_contains_one_or_more");
    }

    p.checkRestrictions (context == ELEMENT_CONTEXT ? ELEMENT_REPEAT_CONTEXT : context, dad, alpha);
    if (context != LIST_CONTEXT && !contentTypeGroupable (p.getContentType (), p.getContentType ()))
      throw new RestrictionViolationException ("one_or_more_string");
  }

  @Override
  boolean samePattern (final Pattern other)
  {
    return (other instanceof OneOrMorePattern && p == ((OneOrMorePattern) other).p);
  }

  @Override
  <T> T apply (final PatternFunction <T> f)
  {
    return f.caseOneOrMore (this);
  }

  Pattern getOperand ()
  {
    return p;
  }
}
