package com.thaiopensource.relaxng.pattern;

import java.util.Set;

import com.thaiopensource.xml.util.Name;

/**
 * A NormalizedNameClass that includes an any name wildcard.
 */
public class NormalizedAnyNameClass extends NormalizedNameClass
{
  private final Set <String> excludedNamespaces;
  private final Set <Name> excludedNames;

  public NormalizedAnyNameClass (final Set <Name> includedNames,
                                 final Set <String> excludedNamespaces,
                                 final Set <Name> excludedNames)
  {
    super (includedNames);
    this.excludedNamespaces = immutable (excludedNamespaces);
    this.excludedNames = immutable (excludedNames);
  }

  @Override
  public boolean isAnyNameIncluded ()
  {
    return true;
  }

  @Override
  public boolean contains (final Name name)
  {
    if (excludedNamespaces.contains (name.getNamespaceUri ()))
      return super.contains (name);
    else
      return !excludedNames.contains (name);
  }

  @Override
  public boolean isEmpty ()
  {
    return false;
  }

  @Override
  public Set <String> getExcludedNamespaces ()
  {
    return excludedNamespaces;
  }

  @Override
  public Set <Name> getExcludedNames ()
  {
    return excludedNames;
  }

  @Override
  public int hashCode ()
  {
    return super.hashCode () ^ excludedNamespaces.hashCode () ^ excludedNames.hashCode ();
  }

  @Override
  public boolean equals (final Object obj)
  {
    if (!(obj instanceof NormalizedAnyNameClass))
      return false;
    final NormalizedAnyNameClass other = (NormalizedAnyNameClass) obj;
    if (!(excludedNamespaces.equals (other.excludedNamespaces)))
      return false;
    if (!(excludedNames.equals (other.excludedNames)))
      return false;
    return equal (this, other);
  }

  @Override
  boolean includesNamespace (final String ns)
  {
    return !getExcludedNamespaces ().contains (ns);
  }
}
