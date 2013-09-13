package com.thaiopensource.relaxng.pattern;

class Alphabet
{
  private NameClass nameClass;

  boolean isEmpty ()
  {
    return nameClass == null;
  }

  void addElement (final NameClass nc)
  {
    if (nameClass == null)
      nameClass = nc;
    else
      if (nc != null)
        nameClass = new ChoiceNameClass (nameClass, nc);
  }

  void addAlphabet (final Alphabet a)
  {
    addElement (a.nameClass);
  }

  void checkOverlap (final Alphabet a) throws RestrictionViolationException
  {
    if (nameClass != null && a.nameClass != null)
      OverlapDetector.checkOverlap (nameClass,
                                    a.nameClass,
                                    "interleave_element_overlap_name",
                                    "interleave_element_overlap_ns",
                                    "interleave_element_overlap");
  }
}
