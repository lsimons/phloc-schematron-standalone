package com.thaiopensource.relaxng.pattern;

class NotAllowedPattern extends Pattern
{
  NotAllowedPattern ()
  {
    super (false, EMPTY_CONTENT_TYPE, NOT_ALLOWED_HASH_CODE);
  }

  @Override
  boolean isNotAllowed ()
  {
    return true;
  }

  @Override
  boolean samePattern (final Pattern other)
  {
    // needs to work for UnexpandedNotAllowedPattern
    return other.getClass () == this.getClass ();
  }

  @Override
  <T> T apply (final PatternFunction <T> f)
  {
    return f.caseNotAllowed (this);
  }
}
