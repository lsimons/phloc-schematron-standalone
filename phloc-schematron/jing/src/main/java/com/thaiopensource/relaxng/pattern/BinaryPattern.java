package com.thaiopensource.relaxng.pattern;

import org.xml.sax.SAXException;

abstract class BinaryPattern extends Pattern
{
  final Pattern p1;
  final Pattern p2;

  BinaryPattern (final boolean nullable, final int hc, final Pattern p1, final Pattern p2)
  {
    super (nullable, Math.max (p1.getContentType (), p2.getContentType ()), hc);
    this.p1 = p1;
    this.p2 = p2;
  }

  @Override
  void checkRecursion (final int depth) throws SAXException
  {
    p1.checkRecursion (depth);
    p2.checkRecursion (depth);
  }

  @Override
  void checkRestrictions (final int context, final DuplicateAttributeDetector dad, final Alphabet alpha) throws RestrictionViolationException
  {
    p1.checkRestrictions (context, dad, alpha);
    p2.checkRestrictions (context, dad, alpha);
  }

  @Override
  boolean samePattern (final Pattern other)
  {
    if (getClass () != other.getClass ())
      return false;
    final BinaryPattern b = (BinaryPattern) other;
    return p1 == b.p1 && p2 == b.p2;
  }

  Pattern getOperand1 ()
  {
    return p1;
  }

  Pattern getOperand2 ()
  {
    return p2;
  }
}
