package com.thaiopensource.relaxng.pattern;

class ErrorPattern extends Pattern
{
  ErrorPattern ()
  {
    super (false, EMPTY_CONTENT_TYPE, ERROR_HASH_CODE);
  }

  @Override
  boolean samePattern (final Pattern other)
  {
    return other instanceof ErrorPattern;
  }

  @Override
  <T> T apply (final PatternFunction <T> f)
  {
    return f.caseError (this);
  }
}
