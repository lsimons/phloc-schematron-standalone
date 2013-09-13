package com.thaiopensource.relaxng.pattern;

class ChoicePattern extends BinaryPattern
{
  ChoicePattern (final Pattern p1, final Pattern p2)
  {
    super (p1.isNullable () || p2.isNullable (),
           combineHashCode (CHOICE_HASH_CODE, p1.hashCode (), p2.hashCode ()),
           p1,
           p2);
  }

  @Override
  Pattern expand (final SchemaPatternBuilder b)
  {
    final Pattern ep1 = p1.expand (b);
    final Pattern ep2 = p2.expand (b);
    if (ep1 != p1 || ep2 != p2)
      return b.makeChoice (ep1, ep2);
    else
      return this;
  }

  @Override
  boolean containsChoice (final Pattern p)
  {
    return p1.containsChoice (p) || p2.containsChoice (p);
  }

  @Override
  <T> T apply (final PatternFunction <T> f)
  {
    return f.caseChoice (this);
  }

  @Override
  void checkRestrictions (final int context, final DuplicateAttributeDetector dad, final Alphabet alpha) throws RestrictionViolationException
  {
    if (dad != null)
      dad.startChoice ();
    p1.checkRestrictions (context, dad, alpha);
    if (dad != null)
      dad.alternative ();
    p2.checkRestrictions (context, dad, alpha);
    if (dad != null)
      dad.endChoice ();
  }

}
