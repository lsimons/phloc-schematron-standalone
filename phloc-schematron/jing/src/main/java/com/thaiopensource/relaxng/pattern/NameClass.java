package com.thaiopensource.relaxng.pattern;

import com.thaiopensource.xml.util.Name;

public interface NameClass
{
  static final int SPECIFICITY_NONE = -1;
  static final int SPECIFICITY_ANY_NAME = 0;
  static final int SPECIFICITY_NS_NAME = 1;
  static final int SPECIFICITY_NAME = 2;

  boolean contains (Name name);

  int containsSpecificity (Name name);

  void accept (NameClassVisitor visitor);

  boolean isOpen ();
}
