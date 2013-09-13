package com.thaiopensource.relaxng.pattern;

class GroupPattern extends BinaryPattern
{
  GroupPattern (final Pattern p1, final Pattern p2)
  {
    super (p1.isNullable () && p2.isNullable (),
           combineHashCode (GROUP_HASH_CODE, p1.hashCode (), p2.hashCode ()),
           p1,
           p2);
  }

  @Override
  Pattern expand (final SchemaPatternBuilder b)
  {
    final Pattern ep1 = p1.expand (b);
    final Pattern ep2 = p2.expand (b);
    if (ep1 != p1 || ep2 != p2)
      return b.makeGroup (ep1, ep2);
    else
      return this;
  }

  @Override
  void checkRestrictions (final int context, final DuplicateAttributeDetector dad, final Alphabet alpha) throws RestrictionViolationException
  {
    switch (context)
    {
      case START_CONTEXT:
        throw new RestrictionViolationException ("start_contains_group");
      case DATA_EXCEPT_CONTEXT:
        throw new RestrictionViolationException ("data_except_contains_group");
    }
    super.checkRestrictions (context == ELEMENT_REPEAT_CONTEXT ? ELEMENT_REPEAT_GROUP_CONTEXT : context, dad, alpha);
    if (context != LIST_CONTEXT && !contentTypeGroupable (p1.getContentType (), p2.getContentType ()))
      throw new RestrictionViolationException ("group_string");
  }

  @Override
  <T> T apply (final PatternFunction <T> f)
  {
    return f.caseGroup (this);
  }
}
