package com.thaiopensource.relaxng.output.rnc;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.thaiopensource.relaxng.parse.SchemaBuilder;
import com.thaiopensource.xml.util.WellKnownNamespaces;

class NamespaceManager
{

  static class NamespaceBindings
  {
    /**
     * maps prefixes to namespaces
     */
    private final Map <String, String> prefixMap;
    /**
     * maps namespaces to preferred non-empty prefix
     */
    private final Map <String, String> nsMap;

    private NamespaceBindings (final Map <String, String> prefixMap, final Map <String, String> nsMap)
    {
      this.prefixMap = prefixMap;
      this.nsMap = nsMap;
    }

    /**
     * Must return non-empty prefix. ns may be empty string
     */
    String getNonEmptyPrefix (final String ns)
    {
      return nsMap.get (ns);
    }

    /**
     * prefix of empty string refers to default namespace if no binding, return
     * null
     */
    String getNamespaceUri (final String prefix)
    {
      return prefixMap.get (prefix);
    }

    Set <String> getPrefixes ()
    {
      return prefixMap.keySet ();
    }

  }

  /**
   * maps a namespaceUri to Boolean; true means empty prefix is OK
   */
  private final Map <String, Boolean> requiredNamespaces = new HashMap <String, Boolean> ();

  static private final String [] conventionalBindings = {
                                                         // prefix, namespaceUri
                                                         "",
                                                         SchemaBuilder.INHERIT_NS,
                                                         "inherit",
                                                         SchemaBuilder.INHERIT_NS,
                                                         "",
                                                         "",
                                                         "local",
                                                         "",
                                                         "rng",
                                                         WellKnownNamespaces.RELAX_NG,
                                                         "a",
                                                         WellKnownNamespaces.RELAX_NG_COMPATIBILITY_ANNOTATIONS };

  static class Binding
  {
    private final String prefix;
    private final String namespaceUri;

    Binding (final String prefix, final String namespaceUri)
    {
      this.prefix = prefix;
      this.namespaceUri = namespaceUri;
    }

    String getPrefix ()
    {
      return prefix;
    }

    String getNamespaceUri ()
    {
      return namespaceUri;
    }

    @Override
    public int hashCode ()
    {
      return prefix.hashCode () ^ namespaceUri.hashCode ();
    }

    @Override
    public boolean equals (final Object obj)
    {
      if (!(obj instanceof Binding))
        return false;
      final Binding other = (Binding) obj;
      return (this.prefix.equals (other.prefix) && this.namespaceUri.equals (other.namespaceUri));
    }
  }

  static class BindingUsage
  {
    boolean required;
    int usageCount;
  }

  /**
   * map Binding to BindingUsage
   */
  private final Map <Binding, BindingUsage> bindingUsageMap = new HashMap <Binding, BindingUsage> ();

  void requireNamespace (final String ns, final boolean prefixMaybeEmpty)
  {
    Boolean b = requiredNamespaces.get (ns);
    if (b == null || (b.booleanValue () && !prefixMaybeEmpty))
    {
      b = prefixMaybeEmpty ? Boolean.TRUE : Boolean.FALSE;
      requiredNamespaces.put (ns, b);
    }
  }

  /**
   * prefix may be empty string
   */
  void requireBinding (final String prefix, final String ns)
  {
    noteBinding (prefix, ns, true);
  }

  /**
   * prefix may be empty string
   */
  void preferBinding (final String prefix, final String ns)
  {
    noteBinding (prefix, ns, false);
  }

  private void noteBinding (final String prefix, final String ns, final boolean required)
  {
    if (ns.equals (WellKnownNamespaces.XML))
      return;
    final Binding b = new Binding (prefix, ns);
    BindingUsage bu = bindingUsageMap.get (b);
    if (bu == null)
    {
      bu = new BindingUsage ();
      bindingUsageMap.put (b, bu);
    }
    if (required)
      bu.required = true;
    bu.usageCount++;
  }

  private class BindingComparator implements Comparator <Binding>
  {
    public int compare (final Binding b1, final Binding b2)
    {
      final BindingUsage bu1 = bindingUsageMap.get (b1);
      final BindingUsage bu2 = bindingUsageMap.get (b2);
      // required precedes not required
      if (bu1.required != bu2.required)
        return bu1.required ? -1 : 1;
      // more usage precedes less usage
      if (bu1.usageCount != bu2.usageCount)
        return bu2.usageCount - bu1.usageCount;
      // Make it a total order to avoid depending on order of
      // iteration over HashSet.
      // prefer shorter prefix
      if (b1.prefix.length () != b2.prefix.length ())
        return b1.prefix.length () - b2.prefix.length ();
      final int ret = b1.prefix.compareTo (b2.prefix);
      if (ret != 0)
        return ret;
      return b1.namespaceUri.compareTo (b2.namespaceUri);
    }
  }

  NamespaceBindings createBindings ()
  {
    // maps prefix representing a string to a namespaceUri
    final Map <String, String> prefixMap = new HashMap <String, String> ();
    // maps namespace to preferred non-empty prefix
    final Map <String, String> nsMap = new HashMap <String, String> ();
    prefixMap.put ("xml", WellKnownNamespaces.XML);
    nsMap.put (WellKnownNamespaces.XML, "xml");
    requiredNamespaces.remove (WellKnownNamespaces.XML);
    final List <Binding> bindingList = new Vector <Binding> ();
    bindingList.addAll (bindingUsageMap.keySet ());
    Collections.sort (bindingList, new BindingComparator ());
    for (final Iterator <Binding> iter = bindingList.iterator (); iter.hasNext ();)
    {
      final Binding binding = iter.next ();
      if (prefixMap.get (binding.prefix) == null)
      {
        final Boolean defaultOK = requiredNamespaces.get (binding.namespaceUri);
        final boolean satisfiesRequirement = defaultOK != null &&
                                             (binding.prefix.length () > 0 || defaultOK.booleanValue ());
        if ((bindingUsageMap.get (binding)).required || satisfiesRequirement)
        {
          prefixMap.put (binding.prefix, binding.namespaceUri);
          iter.remove ();
          if (satisfiesRequirement)
            requiredNamespaces.remove (binding.namespaceUri);
          if (binding.prefix.length () > 0)
            nsMap.put (binding.namespaceUri, binding.prefix);
        }
      }
    }
    // use any of the bindings that we haven't yet used that don't conflict
    for (final Binding binding : bindingList)
    {
      if (prefixMap.get (binding.prefix) == null)
      {
        prefixMap.put (binding.prefix, binding.namespaceUri);
      }
    }
    for (int i = 0; i < conventionalBindings.length; i += 2)
    {
      final String prefix = conventionalBindings[i];
      if (prefixMap.get (prefix) == null)
      {
        final String ns = conventionalBindings[i + 1];
        final Boolean defaultOK = requiredNamespaces.get (ns);
        if (defaultOK != null && (prefix.length () > 0 || defaultOK.booleanValue ()))
        {
          prefixMap.put (prefix, ns);
          requiredNamespaces.remove (ns);
          if (prefix.length () > 0)
            nsMap.put (ns, prefix);
        }
      }
    }
    final Iterator <String> iter = requiredNamespaces.keySet ().iterator ();
    for (int i = 1; iter.hasNext (); i++)
    {
      final String prefix = "ns" + Integer.toString (i);
      if (prefixMap.get (prefix) == null)
      {
        final String ns = iter.next ();
        prefixMap.put (prefix, ns);
        nsMap.put (ns, prefix);
      }
    }
    return new NamespaceBindings (prefixMap, nsMap);
  }

}
