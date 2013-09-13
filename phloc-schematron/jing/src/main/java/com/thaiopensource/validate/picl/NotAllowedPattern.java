package com.thaiopensource.validate.picl;

class NotAllowedPattern extends Pattern
{
  @Override
  boolean matches (final Path path, final int rootDepth)
  {
    return false;
  }

  @Override
  public String toString ()
  {
    return "(notAllowed)";
  }
}
