package com.thaiopensource.validate.picl;

class ElementPathPattern extends PathPattern
{
  ElementPathPattern (final String [] names, final boolean [] descendantsOrSelf)
  {
    super (names, descendantsOrSelf);
  }

  @Override
  boolean isAttribute ()
  {
    return false;
  }
}
