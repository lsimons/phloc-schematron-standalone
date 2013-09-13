package com.thaiopensource.validate.picl;

class AttributePathPattern extends PathPattern
{
  AttributePathPattern (final String [] names, final boolean [] descendantsOrSelf)
  {
    super (names, descendantsOrSelf);
  }

  @Override
  boolean isAttribute ()
  {
    return true;
  }

}
