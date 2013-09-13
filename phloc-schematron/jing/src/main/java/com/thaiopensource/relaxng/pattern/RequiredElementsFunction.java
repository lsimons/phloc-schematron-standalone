package com.thaiopensource.relaxng.pattern;

import java.util.Set;

import com.thaiopensource.xml.util.Name;

/**
 * Implements a function on a pattern that returns the set of required elements.
 * The return value is a non-null Set each member of is a non-null Name. Note
 * that in the schema element foo|bar { text }, neither foo nor bar are required
 * elements.
 */
public class RequiredElementsFunction extends RequiredElementsOrAttributesFunction
{
  @Override
  public Set <Name> caseElement (final ElementPattern p)
  {
    return caseNamed (p.getNameClass ());
  }

  @Override
  public Set <Name> caseGroup (final GroupPattern p)
  {
    final Pattern p1 = p.getOperand1 ();
    if (!p1.isNullable ())
      return p1.apply (this);
    return p.getOperand2 ().apply (this);
  }
}
