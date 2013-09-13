package com.thaiopensource.relaxng.pattern;

class AfterPattern extends BinaryPattern
{
  AfterPattern (final Pattern p1, final Pattern p2)
  {
    super (false, combineHashCode (AFTER_HASH_CODE, p1.hashCode (), p2.hashCode ()), p1, p2);
  }

  @Override
  boolean isNotAllowed ()
  {
    return p1.isNotAllowed ();
  }

  @Override
  <T> T apply (final PatternFunction <T> f)
  {
    return f.caseAfter (this);
  }
}
