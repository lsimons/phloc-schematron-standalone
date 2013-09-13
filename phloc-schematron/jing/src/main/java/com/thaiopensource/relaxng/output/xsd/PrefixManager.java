package com.thaiopensource.relaxng.output.xsd;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.thaiopensource.relaxng.edit.AbstractVisitor;
import com.thaiopensource.relaxng.edit.AttributePattern;
import com.thaiopensource.relaxng.edit.ChoiceNameClass;
import com.thaiopensource.relaxng.edit.CompositePattern;
import com.thaiopensource.relaxng.edit.DefineComponent;
import com.thaiopensource.relaxng.edit.DivComponent;
import com.thaiopensource.relaxng.edit.ElementPattern;
import com.thaiopensource.relaxng.edit.IncludeComponent;
import com.thaiopensource.relaxng.edit.NameClass;
import com.thaiopensource.relaxng.edit.NameNameClass;
import com.thaiopensource.relaxng.edit.NamespaceContext;
import com.thaiopensource.relaxng.edit.UnaryPattern;
import com.thaiopensource.relaxng.edit.ValuePattern;
import com.thaiopensource.util.VoidValue;
import com.thaiopensource.xml.util.Naming;
import com.thaiopensource.xml.util.WellKnownNamespaces;

public class PrefixManager implements SourceUriGenerator
{

  private final Map <String, String> prefixMap = new HashMap <String, String> ();
  private final Set <String> usedPrefixes = new HashSet <String> ();
  /**
   * Set of prefixes that cannot be used for schema namespace.
   */
  private final Set <String> reservedPrefixes = new HashSet <String> ();
  private int nextGenIndex = 1;
  static private final String [] xsdPrefixes = { "xs", "xsd" };
  static private final int MAX_PREFIX_LENGTH = 10;

  static class PrefixUsage
  {
    int count;
  }

  class PrefixSelector extends AbstractVisitor
  {
    private final SchemaInfo si;
    private String inheritedNamespace;
    private final Map <String, Map <String, PrefixUsage>> namespacePrefixUsageMap = new HashMap <String, Map <String, PrefixUsage>> ();

    PrefixSelector (final SchemaInfo si)
    {
      this.si = si;
      this.inheritedNamespace = "";
      si.getGrammar ().componentsAccept (this);
      final NamespaceContext context = si.getGrammar ().getContext ();
      if (context != null)
      {
        for (final String prefix : context.getPrefixes ())
        {
          if (!prefix.equals (""))
            notePrefix (prefix, resolveNamespace (context.getNamespace (prefix)));
        }
      }
    }

    @Override
    public VoidValue visitElement (final ElementPattern p)
    {
      p.getNameClass ().accept (this);
      p.getChild ().accept (this);
      return VoidValue.VOID;
    }

    @Override
    public VoidValue visitAttribute (final AttributePattern p)
    {
      return p.getNameClass ().accept (this);
    }

    @Override
    public VoidValue visitChoice (final ChoiceNameClass nc)
    {
      nc.childrenAccept (this);
      return VoidValue.VOID;
    }

    @Override
    public VoidValue visitName (final NameNameClass nc)
    {
      notePrefix (nc.getPrefix (), resolveNamespace (nc.getNamespaceUri ()));
      return VoidValue.VOID;
    }

    @Override
    public VoidValue visitValue (final ValuePattern p)
    {
      for (final Map.Entry <String, String> entry : p.getPrefixMap ().entrySet ())
      {
        final String prefix = entry.getKey ();
        if (prefix != null && !prefix.equals (""))
        {
          final String ns = resolveNamespace (entry.getValue ());
          notePrefix (prefix, ns);
          if (!ns.equals (WellKnownNamespaces.XML_SCHEMA))
            reservedPrefixes.add (prefix);
        }
      }
      return VoidValue.VOID;
    }

    private String resolveNamespace (final String ns)
    {
      return ns == NameClass.INHERIT_NS ? inheritedNamespace : ns;
    }

    private void notePrefix (final String prefix, final String ns)
    {
      if (prefix == null || ns == null || ns.equals (""))
        return;
      Map <String, PrefixUsage> prefixUsageMap = namespacePrefixUsageMap.get (ns);
      if (prefixUsageMap == null)
      {
        prefixUsageMap = new HashMap <String, PrefixUsage> ();
        namespacePrefixUsageMap.put (ns, prefixUsageMap);
      }
      PrefixUsage prefixUsage = prefixUsageMap.get (prefix);
      if (prefixUsage == null)
      {
        prefixUsage = new PrefixUsage ();
        prefixUsageMap.put (prefix, prefixUsage);
      }
      prefixUsage.count++;
    }

    @Override
    public VoidValue visitComposite (final CompositePattern p)
    {
      p.childrenAccept (this);
      return VoidValue.VOID;
    }

    @Override
    public VoidValue visitUnary (final UnaryPattern p)
    {
      return p.getChild ().accept (this);
    }

    @Override
    public VoidValue visitDefine (final DefineComponent c)
    {
      c.getBody ().accept (this);
      return VoidValue.VOID;
    }

    @Override
    public VoidValue visitDiv (final DivComponent c)
    {
      c.componentsAccept (this);
      return VoidValue.VOID;
    }

    @Override
    public VoidValue visitInclude (final IncludeComponent c)
    {
      final String saveInheritedNamespace = inheritedNamespace;
      inheritedNamespace = c.getNs ();
      si.getSchema (c.getUri ()).componentsAccept (this);
      inheritedNamespace = saveInheritedNamespace;
      return VoidValue.VOID;
    }

    void assignPrefixes ()
    {
      for (final Map.Entry <String, Map <String, PrefixUsage>> entry : namespacePrefixUsageMap.entrySet ())
      {
        final String ns = entry.getKey ();
        if (!ns.equals ("") && !ns.equals (WellKnownNamespaces.XML))
        {
          final Map <String, PrefixUsage> prefixUsageMap = entry.getValue ();
          if (prefixUsageMap != null)
          {
            Map.Entry <String, PrefixUsage> best = null;
            for (final Map.Entry <String, PrefixUsage> tem : prefixUsageMap.entrySet ())
            {
              if ((best == null || (tem.getValue ()).count > (best.getValue ()).count) && prefixOk (tem.getKey (), ns))
                best = tem;
            }
            if (best != null)
              usePrefix (best.getKey (), ns);
          }
        }
      }
    }
  }

  PrefixManager (final SchemaInfo si)
  {
    usePrefix ("xml", WellKnownNamespaces.XML);
    new PrefixSelector (si).assignPrefixes ();
  }

  String getPrefix (final String namespace)
  {
    String prefix = prefixMap.get (namespace);
    if (prefix == null && namespace.equals (WellKnownNamespaces.XML_SCHEMA))
    {
      for (final String xsdPrefixe : xsdPrefixes)
        if (tryUsePrefix (xsdPrefixe, namespace))
          return xsdPrefixe;
    }
    if (prefix == null)
      prefix = tryUseUri (namespace);
    if (prefix == null)
    {
      do
      {
        prefix = "ns" + Integer.toString (nextGenIndex++);
      } while (!tryUsePrefix (prefix, namespace));
    }
    return prefix;
  }

  private String tryUseUri (final String namespace)
  {
    final String segment = chooseSegment (namespace);
    if (segment == null)
      return null;
    if (segment.length () <= MAX_PREFIX_LENGTH && tryUsePrefix (segment, namespace))
      return segment;
    for (int i = 1; i <= segment.length (); i++)
    {
      final String prefix = segment.substring (0, i);
      if (tryUsePrefix (prefix, namespace))
        return prefix;
    }
    return null;
  }

  private boolean tryUsePrefix (final String prefix, final String namespace)
  {
    if (!prefixOk (prefix, namespace))
      return false;
    usePrefix (prefix, namespace);
    return true;
  }

  private boolean prefixOk (final String prefix, final String namespace)
  {
    return (!usedPrefixes.contains (prefix) && !(reservedPrefixes.contains (prefix) && namespace.equals (WellKnownNamespaces.XML_SCHEMA)));
  }

  private void usePrefix (final String prefix, final String namespace)
  {
    usedPrefixes.add (prefix);
    prefixMap.put (namespace, prefix);
  }

  static private String chooseSegment (final String ns)
  {
    int off = ns.indexOf ('#');
    if (off >= 0)
    {
      final String segment = ns.substring (off + 1).toLowerCase ();
      if (Naming.isNcname (segment))
        return segment;
    }
    else
      off = ns.length ();
    for (;;)
    {
      final int i = ns.lastIndexOf ('/', off - 1);
      if (i < 0 || (i > 0 && ns.charAt (i - 1) == '/'))
        break;
      final String segment = ns.substring (i + 1, off).toLowerCase ();
      if (segmentOk (segment))
        return segment;
      off = i;
    }
    off = ns.indexOf (':');
    if (off >= 0)
    {
      final String segment = ns.substring (off + 1).toLowerCase ();
      if (segmentOk (segment))
        return segment;
    }
    return null;
  }

  private static boolean segmentOk (final String segment)
  {
    return Naming.isNcname (segment) && !segment.equals ("ns") && !segment.equals ("namespace");
  }

  public String generateSourceUri (final String ns)
  {
    // TODO add method to OutputDirectory to do this properly
    if (ns.equals (""))
      return "local";
    else
      return "/" + getPrefix (ns);
  }
}
