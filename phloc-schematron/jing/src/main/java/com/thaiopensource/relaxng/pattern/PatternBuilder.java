package com.thaiopensource.relaxng.pattern;

public class PatternBuilder
{
  private final EmptyPattern empty;
  protected final NotAllowedPattern notAllowed;
  protected final PatternInterner interner;

  public PatternBuilder ()
  {
    empty = new EmptyPattern ();
    notAllowed = new NotAllowedPattern ();
    interner = new PatternInterner ();
  }

  public PatternBuilder (final PatternBuilder parent)
  {
    empty = parent.empty;
    notAllowed = parent.notAllowed;
    interner = new PatternInterner (parent.interner);
  }

  Pattern makeEmpty ()
  {
    return empty;
  }

  Pattern makeNotAllowed ()
  {
    return notAllowed;
  }

  Pattern makeGroup (final Pattern p1, final Pattern p2)
  {
    if (p1 == empty)
      return p2;
    if (p2 == empty)
      return p1;
    if (p1 == notAllowed || p2 == notAllowed)
      return notAllowed;
    if (false && p1 instanceof GroupPattern)
    {
      final GroupPattern sp = (GroupPattern) p1;
      return makeGroup (sp.p1, makeGroup (sp.p2, p2));
    }
    final Pattern p = new GroupPattern (p1, p2);
    return interner.intern (p);
  }

  Pattern makeInterleave (final Pattern p1, final Pattern p2)
  {
    if (p1 == empty)
      return p2;
    if (p2 == empty)
      return p1;
    if (p1 == notAllowed || p2 == notAllowed)
      return notAllowed;
    if (false && p1 instanceof InterleavePattern)
    {
      final InterleavePattern ip = (InterleavePattern) p1;
      return makeInterleave (ip.p1, makeInterleave (ip.p2, p2));
    }
    if (false)
    {
      if (p2 instanceof InterleavePattern)
      {
        final InterleavePattern ip = (InterleavePattern) p2;
        if (p1.hashCode () > ip.p1.hashCode ())
          return makeInterleave (ip.p1, makeInterleave (p1, ip.p2));
      }
      else
        if (p1.hashCode () > p2.hashCode ())
          return makeInterleave (p2, p1);
    }
    final Pattern p = new InterleavePattern (p1, p2);
    return interner.intern (p);
  }

  Pattern makeChoice (final Pattern p1, final Pattern p2)
  {
    if (p1 == empty && p2.isNullable ())
      return p2;
    if (p2 == empty && p1.isNullable ())
      return p1;
    final Pattern p = new ChoicePattern (p1, p2);
    return interner.intern (p);
  }

  Pattern makeOneOrMore (final Pattern p)
  {
    if (p == empty || p == notAllowed || p instanceof OneOrMorePattern)
      return p;
    final Pattern p1 = new OneOrMorePattern (p);
    return interner.intern (p1);
  }

  Pattern makeOptional (final Pattern p)
  {
    return makeChoice (p, empty);
  }

  Pattern makeZeroOrMore (final Pattern p)
  {
    return makeOptional (makeOneOrMore (p));
  }
}
