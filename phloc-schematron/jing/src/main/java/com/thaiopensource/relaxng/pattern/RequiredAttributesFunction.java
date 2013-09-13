package com.thaiopensource.relaxng.pattern;

import java.util.Set;

import com.thaiopensource.xml.util.Name;

/**
 * Implements a function on a pattern that returns the set of required
 * attributes. The return value is a non-null Set each member of is a non-null
 * Name. Note that in the schema attribute foo|bar { text }, neither foo nor bar
 * are required attributes.
 */
class RequiredAttributesFunction extends RequiredElementsOrAttributesFunction
{
  @Override
  public Set <Name> caseAttribute (final AttributePattern p)
  {
    return caseNamed (p.getNameClass ());
  }

  @Override
  public Set <Name> caseGroup (final GroupPattern p)
  {
    return union (p);
  }
}
