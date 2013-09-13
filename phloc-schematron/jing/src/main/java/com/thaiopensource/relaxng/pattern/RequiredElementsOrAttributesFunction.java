package com.thaiopensource.relaxng.pattern;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.thaiopensource.xml.util.Name;

/**
 * Common functionality between RequiredAttributesFunction and
 * RequiredElementsFunction
 */
abstract class RequiredElementsOrAttributesFunction extends AbstractPatternFunction <Set <Name>>
{
  @Override
  public Set <Name> caseOther (final Pattern p)
  {
    return Collections.emptySet ();
  }

  @Override
  public Set <Name> caseChoice (final ChoicePattern p)
  {
    final Set <Name> s1 = p.getOperand1 ().apply (this);
    final Set <Name> s2 = p.getOperand2 ().apply (this);
    if (s1.isEmpty ())
      return s1;
    if (s2.isEmpty ())
      return s2;
    s1.retainAll (s2);
    return s1;
  }

  protected Set <Name> caseNamed (final NameClass nc)
  {
    if (!(nc instanceof SimpleNameClass))
      return Collections.emptySet ();
    final Set <Name> s = new HashSet <Name> ();
    s.add (((SimpleNameClass) nc).getName ());
    return s;
  }

  protected Set <Name> union (final BinaryPattern p)
  {
    final Set <Name> s1 = p.getOperand1 ().apply (this);
    final Set <Name> s2 = p.getOperand2 ().apply (this);
    if (s1.isEmpty ())
      return s2;
    if (s2.isEmpty ())
      return s1;
    s1.addAll (s2);
    return s1;
  }

  @Override
  public Set <Name> caseInterleave (final InterleavePattern p)
  {
    return union (p);
  }

  @Override
  public Set <Name> caseAfter (final AfterPattern p)
  {
    return p.getOperand1 ().apply (this);
  }

  @Override
  public Set <Name> caseOneOrMore (final OneOrMorePattern p)
  {
    return p.getOperand ().apply (this);
  }
}
