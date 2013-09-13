package com.thaiopensource.validate.picl;

import java.util.Vector;

class PatternBuilder
{
  static final byte CHILD = 0;
  static final byte ATTRIBUTE = 1;

  private boolean hadDescendantOrSelf = false;
  private final Vector <Pattern> choices = new Vector <Pattern> ();
  private final Vector <String> names = new Vector <String> ();
  private final Vector <Boolean> descendantsOrSelf = new Vector <Boolean> ();
  private static final int NO_ATTRIBUTE = 0;
  private static final int LAST_WAS_ATTRIBUTE = 1;
  private static final int NON_LEAF_ATTRIBUTE = 2;
  private int attributeType = NO_ATTRIBUTE;

  void addName (final byte type, final String namespaceUri, final String localName)
  {
    descendantsOrSelf.addElement (Boolean.valueOf (hadDescendantOrSelf));
    hadDescendantOrSelf = false;
    names.addElement (namespaceUri);
    names.addElement (localName);
    switch (attributeType)
    {
      case LAST_WAS_ATTRIBUTE:
        attributeType = NON_LEAF_ATTRIBUTE;
        break;
      case NO_ATTRIBUTE:
        if (type == ATTRIBUTE)
          attributeType = LAST_WAS_ATTRIBUTE;
        break;
    }
  }

  void addAnyName (final byte type)
  {
    addName (type, PathPattern.ANY, PathPattern.ANY);
  }

  void addNsName (final byte type, final String namespaceUri)
  {
    addName (type, namespaceUri, PathPattern.ANY);
  }

  void addDescendantsOrSelf ()
  {
    if (attributeType == NO_ATTRIBUTE)
      hadDescendantOrSelf = true;
  }

  private PathPattern wrapUpAlternative ()
  {
    PathPattern result;
    if (attributeType == NON_LEAF_ATTRIBUTE)
      result = null;
    else
    {
      final String [] namesArray = new String [names.size ()];
      for (int i = 0; i < namesArray.length; i++)
        namesArray[i] = names.elementAt (i);
      final boolean [] descendantsOrSelfArray = new boolean [descendantsOrSelf.size () + 1];
      for (int i = 0; i < descendantsOrSelfArray.length - 1; i++)
        descendantsOrSelfArray[i] = descendantsOrSelf.elementAt (i).booleanValue ();
      descendantsOrSelfArray[descendantsOrSelfArray.length - 1] = hadDescendantOrSelf;
      if (attributeType == NO_ATTRIBUTE)
        result = new ElementPathPattern (namesArray, descendantsOrSelfArray);
      else
        result = new AttributePathPattern (namesArray, descendantsOrSelfArray);
    }
    cleanupAlternative ();
    return result;
  }

  private void cleanupAlternative ()
  {
    attributeType = NO_ATTRIBUTE;
    hadDescendantOrSelf = false;
    names.setSize (0);
    descendantsOrSelf.setSize (0);
  }

  void cleanup ()
  {
    cleanupAlternative ();
    choices.setSize (0);
  }

  void alternative ()
  {
    final Pattern pattern = wrapUpAlternative ();
    if (pattern != null)
      choices.addElement (pattern);
  }

  Pattern createPattern ()
  {
    final Pattern pattern = wrapUpAlternative ();
    if (choices.size () == 0)
    {
      if (pattern == null)
        return new NotAllowedPattern ();
      return pattern;
    }
    else
    {
      if (pattern != null)
        choices.addElement (pattern);
      final Pattern [] patterns = new Pattern [choices.size ()];
      for (int i = 0; i < patterns.length; i++)
        patterns[i] = choices.elementAt (i);
      return new ChoicePattern (patterns);
    }
  }
}
