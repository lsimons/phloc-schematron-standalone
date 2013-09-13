package com.thaiopensource.validate.picl;

class ChoicePattern extends Pattern
{
  private final Pattern [] choices;

  ChoicePattern (final Pattern [] choices)
  {
    this.choices = choices;
  }

  @Override
  boolean matches (final Path path, final int rootDepth)
  {
    for (final Pattern choice : choices)
      if (choice.matches (path, rootDepth))
        return true;
    return false;
  }

  @Override
  public String toString ()
  {
    final StringBuffer buf = new StringBuffer ();
    for (int i = 0; i < choices.length; i++)
    {
      if (i != 0)
        buf.append ('|');
      buf.append (choices[i].toString ());
    }
    return buf.toString ();
  }
}
