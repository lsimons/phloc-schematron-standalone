package com.thaiopensource.relaxng.pattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.thaiopensource.xml.util.Name;

/**
 * Base class for normalizing name classes.
 */
public abstract class AbstractNameClassNormalizer
{
  private static final String IMPOSSIBLE = "\u0000";

  protected abstract boolean contains (Name name);

  protected abstract void accept (NameClassVisitor visitor);

  public NormalizedNameClass normalize ()
  {
    final List <Name> mentionedNames = new ArrayList <Name> ();
    final List <String> mentionedNamespaces = new ArrayList <String> ();
    accept (new NameClassVisitor ()
    {
      public void visitChoice (final NameClass nc1, final NameClass nc2)
      {
        nc1.accept (this);
        nc2.accept (this);
      }

      public void visitNsName (final String ns)
      {
        mentionedNamespaces.add (ns);
      }

      public void visitNsNameExcept (final String ns, final NameClass nc)
      {
        mentionedNamespaces.add (ns);
        nc.accept (this);
      }

      public void visitAnyName ()
      {}

      public void visitAnyNameExcept (final NameClass nc)
      {
        nc.accept (this);
      }

      public void visitName (final Name name)
      {
        mentionedNames.add (name);
      }

      public void visitNull ()
      {}

      public void visitError ()
      {}
    });
    if (contains (new Name (IMPOSSIBLE, IMPOSSIBLE)))
    {
      final Set <Name> includedNames = new HashSet <Name> ();
      final Set <String> excludedNamespaces = new HashSet <String> ();
      final Set <Name> excludedNames = new HashSet <Name> ();
      for (final String ns : mentionedNamespaces)
      {
        if (!contains (new Name (ns, IMPOSSIBLE)))
          excludedNamespaces.add (ns);
      }
      for (final Name name : mentionedNames)
      {
        final boolean in = contains (name);
        if (excludedNamespaces.contains (name.getNamespaceUri ()))
        {
          if (in)
            includedNames.add (name);
        }
        else
          if (!in)
            excludedNames.add (name);
      }
      return new NormalizedAnyNameClass (includedNames, excludedNamespaces, excludedNames);
    }
    final Map <String, HashSet <String>> nsMap = new HashMap <String, HashSet <String>> ();
    for (final String ns : mentionedNamespaces)
    {
      if (contains (new Name (ns, IMPOSSIBLE)) && nsMap.get (ns) == null)
        nsMap.put (ns, new HashSet <String> ());
    }
    final Set <Name> includedNames = new HashSet <Name> ();
    for (final Name name : mentionedNames)
    {
      final boolean in = contains (name);
      final Set <String> excluded = nsMap.get (name.getNamespaceUri ());
      if (excluded == null)
      {
        if (in)
          includedNames.add (name);
      }
      else
        if (!in)
          excluded.add (name.getLocalName ());
    }
    return new NormalizedNsNameClass (includedNames, nsMap);
  }
}
