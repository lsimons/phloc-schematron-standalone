package com.thaiopensource.relaxng.pattern;

class UnexpandedNotAllowedPattern extends NotAllowedPattern
{
  UnexpandedNotAllowedPattern ()
  {}

  @Override
  boolean isNotAllowed ()
  {
    return false;
  }

  @Override
  Pattern expand (final SchemaPatternBuilder b)
  {
    return b.makeNotAllowed ();
  }
}
