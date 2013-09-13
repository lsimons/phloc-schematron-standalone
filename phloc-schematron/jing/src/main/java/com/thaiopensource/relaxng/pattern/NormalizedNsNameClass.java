package com.thaiopensource.relaxng.pattern;

import java.util.Map;
import java.util.Set;

import com.thaiopensource.xml.util.Name;

/**
 * A NormalizedNsNameClass that contains one or more namespace wildcards.
 */
public class NormalizedNsNameClass extends NormalizedNameClass
{
  private final Map <String, ? extends Set <String>> nsMap;
  private final Set <String> includedNamespaces;

  public NormalizedNsNameClass (final Set <Name> includedNames, final Map <String, ? extends Set <String>> nsMap)
  {
    super (includedNames);
    this.nsMap = nsMap;
    includedNamespaces = immutable (nsMap.keySet ());
  }

  @Override
  public boolean isEmpty ()
  {
    return super.isEmpty () && nsMap.isEmpty ();
  }

  @Override
  public boolean contains (final Name name)
  {
    final Set <String> excludedLocalNames = nsMap.get (name.getNamespaceUri ());
    if (excludedLocalNames == null)
      return super.contains (name);
    else
      return !excludedLocalNames.contains (name.getLocalName ());
  }

  @Override
  public Set <String> getIncludedNamespaces ()
  {
    return includedNamespaces;
  }

  @Override
  public Set <String> getExcludedLocalNames (final String ns)
  {
    return nsMap.get (ns);
  }

  @Override
  public int hashCode ()
  {
    return super.hashCode () ^ nsMap.hashCode ();
  }

  @Override
  public boolean equals (final Object obj)
  {
    if (!(obj instanceof NormalizedNsNameClass))
      return false;
    final NormalizedNsNameClass other = (NormalizedNsNameClass) obj;
    if (!nsMap.equals (other.nsMap))
      return false;
    return equal (this, other);
  }

  @Override
  boolean includesNamespace (final String ns)
  {
    return getIncludedNamespaces ().contains (ns);
  }
}
