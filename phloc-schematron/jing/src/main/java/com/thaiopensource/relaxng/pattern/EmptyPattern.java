package com.thaiopensource.relaxng.pattern;

class EmptyPattern extends Pattern
{
  EmptyPattern ()
  {
    super (true, EMPTY_CONTENT_TYPE, EMPTY_HASH_CODE);
  }

  @Override
  boolean samePattern (final Pattern other)
  {
    return other instanceof EmptyPattern;
  }

  @Override
  <T> T apply (final PatternFunction <T> f)
  {
    return f.caseEmpty (this);
  }

  @Override
  void checkRestrictions (final int context, final DuplicateAttributeDetector dad, final Alphabet alpha) throws RestrictionViolationException
  {
    switch (context)
    {
      case DATA_EXCEPT_CONTEXT:
        throw new RestrictionViolationException ("data_except_contains_empty");
      case START_CONTEXT:
        throw new RestrictionViolationException ("start_contains_empty");
    }
  }
}
