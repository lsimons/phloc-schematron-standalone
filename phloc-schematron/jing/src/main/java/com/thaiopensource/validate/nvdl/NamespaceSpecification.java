package com.thaiopensource.validate.nvdl;

import java.util.StringTokenizer;

/**
 * Stores information about a namespace specification. A namespace is specified
 * with a namespace pattern and a wildcard. The wildcard can be present in
 * multiple places in the namespace specification and each occurence of the
 * wildcard can be replaced with an arbitrary sequence of characters.
 * 
 * @author george
 */
class NamespaceSpecification
{
  /**
   * Default value for wildcard.
   */
  public static String DEFAULT_WILDCARD = "*";

  /**
   * Constant for any namespace.
   */
  static final String ANY_NAMESPACE = "##any";

  /**
   * The namespace pattern, may contain one or more occurances of the wildcard.
   */
  String ns = "\0";

  /**
   * The wildcard character, by default it is *.
   */
  String wildcard = DEFAULT_WILDCARD;

  /**
   * Creates a namespace specification from a namespace pattern using the
   * default wildcard, that is *.
   * 
   * @param ns
   *        The namespace pattern
   */
  public NamespaceSpecification (final String ns)
  {
    this (ns, DEFAULT_WILDCARD);
  }

  /**
   * Creates a namespace specification from a namespace pattern and a given
   * wildcard.
   * 
   * @param ns
   *        The namespace pattern
   * @param wildcard
   *        The given wildcard character.
   */
  public NamespaceSpecification (final String ns, final String wildcard)
  {
    this.ns = ns;
    this.wildcard = wildcard;
  }

  /**
   * Check if this namespace specification competes with another namespace
   * specification.
   * 
   * @param other
   *        The namespace specification we need to check if it competes with
   *        this namespace specification.
   * @return true if the namespace specifications compete.
   */
  public boolean compete (final NamespaceSpecification other)
  {
    // if no wildcard for other then we check coverage
    if ("".equals (other.wildcard))
    {
      return covers (other.ns);
    }
    // split the namespaces at wildcards
    final String [] otherParts = split (other.ns, other.wildcard);

    // if the given namepsace specification does not use its wildcard
    // then we just look if the current namespace specification covers it
    if (otherParts.length == 1)
    {
      return covers (other.ns);
    }
    // if no wildcard for the current namespace specification
    if ("".equals (wildcard))
    {
      return other.covers (ns);
    }
    // also for the current namespace specification
    final String [] parts = split (ns, wildcard);
    // now check if the current namespace specification is just an URI
    if (parts.length == 1)
    {
      return other.covers (ns);
    }
    // now each namespace specification contains wildcards
    // suppose we have
    // ns = a1*a2*...*an
    // and
    // other.ns = b1*b2*...*bm
    // then we only need to check matchPrefix(a1, b1) and matchPrefix(an, bn)
    // where
    // matchPrefix(a, b) means a starts with b or b starts with a.
    return matchPrefix (parts[0], otherParts[0]) &&
           matchPrefix (parts[parts.length - 1], otherParts[otherParts.length - 1]);
  }

  /**
   * Checks with either of the strings starts with the other.
   * 
   * @param s1
   *        a String
   * @param s2
   *        a String
   * @return true if s1 starts with s2 or s2 starts with s1, false otherwise
   */
  static private boolean matchPrefix (final String s1, final String s2)
  {
    return s1.startsWith (s2) || s2.startsWith (s1);
  }

  private String [] split (final String value, final String wildcard)
  {
    final StringTokenizer st = new StringTokenizer (value, wildcard, true);
    int index = st.countTokens ();
    if (index == 0)
      return new String [] { value };
    final String [] parts = new String [index];
    index = 0;
    while (st.hasMoreTokens ())
    {
      final String token = st.nextToken ();
      parts[index++] = token.equals (wildcard) ? "" : token;
    }
    return parts;
  }

  /**
   * Checks if a namespace specification covers a specified URI. any namespace
   * pattern covers only the any namespace uri.
   * 
   * @param uri
   *        The uri to be checked.
   * @return true if the namespace pattern covers the specified uri.
   */
  public boolean covers (final String uri)
  {
    // any namspace covers only the any namespace uri
    // no wildcard ("") requires equality between namespaces.
    if (ANY_NAMESPACE.equals (ns) || "".equals (wildcard))
    {
      return ns.equals (uri);
    }
    final String [] parts = split (ns, wildcard);
    // no wildcard
    if (parts.length == 1)
    {
      return ns.equals (uri);
    }
    // at least one wildcard, we need to check that the start and end are the
    // same
    // then we get to match a string against a pattern like *p1*...*pn*
    if (!uri.startsWith (parts[0]))
    {
      return false;
    }
    if (!uri.endsWith (parts[parts.length - 1]))
    {
      return false;
    }
    // Check that all remaining parts match the remaining URI.
    int start = parts[0].length ();
    final int end = uri.length () - parts[parts.length - 1].length ();
    for (int i = 1; i < parts.length - 1; i++)
    {
      if (start > end)
      {
        return false;
      }
      final int match = uri.indexOf (parts[i], start);
      if (match == -1 || match + parts[i].length () > end)
      {
        return false;
      }
      start = match + parts[i].length ();
    }
    return true;
  }

  /**
   * Checks for equality with another Namespace specification.
   */
  @Override
  public boolean equals (final Object obj)
  {
    if (obj instanceof NamespaceSpecification)
    {
      final NamespaceSpecification other = (NamespaceSpecification) obj;
      return ns.equals (other.ns) && wildcard.equals (other.wildcard);
    }
    return false;
  }

  /**
   * Get a hashcode for this namespace specification.
   */
  @Override
  public int hashCode ()
  {
    return (wildcard + "|" + ns).hashCode ();
  }
}
