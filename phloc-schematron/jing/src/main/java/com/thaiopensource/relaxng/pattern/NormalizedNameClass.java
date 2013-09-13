package com.thaiopensource.relaxng.pattern;

import java.util.Collections;
import java.util.Set;

import com.thaiopensource.xml.util.Name;

/**
 * Base class for all implementations of
 * com.thaiopensource.relaxng.match.NameClass.
 */
public abstract class NormalizedNameClass implements com.thaiopensource.relaxng.match.NameClass
{
  private final Set <Name> includedNames;

  /**
   * Create a NormalizedNameClass representing a name class without any
   * wildcards.
   * 
   * @param includedNames
   *        an immutable set of names
   */
  public NormalizedNameClass (final Set <Name> includedNames)
  {
    this.includedNames = immutable (includedNames);
  }

  public boolean isEmpty ()
  {
    return includedNames.isEmpty ();
  }

  public boolean contains (final Name name)
  {
    return includedNames.contains (name);
  }

  public boolean isAnyNameIncluded ()
  {
    return false;
  }

  public Set <String> getExcludedNamespaces ()
  {
    return null;
  }

  public Set <Name> getIncludedNames ()
  {
    return includedNames;
  }

  public Set <Name> getExcludedNames ()
  {
    return null;
  }

  public Set <String> getIncludedNamespaces ()
  {
    return Collections.emptySet ();
  }

  public Set <String> getExcludedLocalNames (final String ns)
  {
    return null;
  }

  @Override
  public abstract boolean equals (Object obj);

  boolean equal (final NormalizedNameClass nc1, final NormalizedNameClass nc2)
  {
    return nc1.includedNames.equals (nc2.includedNames);
  }

  @Override
  public int hashCode ()
  {
    return includedNames.hashCode ();
  }

  <T> Set <T> immutable (final Set <T> set)
  {
    if (set.isEmpty ())
      return Collections.emptySet ();
    return Collections.unmodifiableSet (set);
  }

  abstract boolean includesNamespace (String ns);
}
