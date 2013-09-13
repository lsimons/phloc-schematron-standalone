package com.thaiopensource.xml.dtd.parse;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.thaiopensource.xml.tok.Tokenizer;

class DtdBuilder
{
  private final Vector atoms;
  private final Vector decls = new Vector ();

  private final Hashtable paramEntityTable = new Hashtable ();
  private final Hashtable generalEntityTable = new Hashtable ();
  private final Hashtable normalizedTable = new Hashtable ();
  private final Hashtable notationTable = new Hashtable ();
  private final Hashtable ambigNameTable = new Hashtable ();

  DtdBuilder (final Vector atoms)
  {
    this.atoms = atoms;
  }

  Vector getDecls ()
  {
    return decls;
  }

  Entity lookupParamEntity (final String name)
  {
    return (Entity) paramEntityTable.get (name);
  }

  Entity createParamEntity (final String name)
  {
    final Entity e = new Entity (name, true);
    Entity prev = (Entity) paramEntityTable.get (name);
    if (prev != null)
    {
      while (prev.overrides != null)
        prev = prev.overrides;
      prev.overrides = e;
      e.overridden = true;
    }
    else
      paramEntityTable.put (name, e);
    return e;
  }

  Entity lookupGeneralEntity (final String name)
  {
    return (Entity) generalEntityTable.get (name);
  }

  Entity createGeneralEntity (final String name)
  {
    final Entity e = new Entity (name, false);
    Entity prev = (Entity) generalEntityTable.get (name);
    if (prev != null)
    {
      while (prev.overrides != null)
        prev = prev.overrides;
      prev.overrides = e;
      e.overridden = true;
    }
    else
      generalEntityTable.put (name, e);
    return e;
  }

  Notation lookupNotation (final String name)
  {
    return (Notation) notationTable.get (name);
  }

  Notation createNotation (final String name)
  {
    Notation n = (Notation) notationTable.get (name);
    if (n != null)
      return null;
    n = new Notation (name);
    notationTable.put (name, n);
    return n;
  }

  void unexpandEntities ()
  {
    for (final Enumeration e = paramEntityTable.elements (); e.hasMoreElements ();)
      ((Entity) e.nextElement ()).unexpandEntities ();
  }

  void createDecls ()
  {
    new AtomParser (this, new AtomStream (atoms), new PrologParser (PrologParser.EXTERNAL_ENTITY), decls).parse ();
  }

  void analyzeSemantics ()
  {
    /*
     * A parameter entity such as <!ENTITY % n.foo "foo"> where n.foo is
     * referenced only in model groups could either be a name spec for an
     * undefined element or a model group. If the element name "foo" is always
     * referenced via n.foo, then we assume it's a name spec, otherwise we take
     * it to be a model group.
     */

    for (final Enumeration e = paramEntityTable.elements (); e.hasMoreElements ();)
    {
      final Entity ent = (Entity) e.nextElement ();
      final String name = ent.ambiguousNameSpec ();
      if (name != null)
      {
        final Entity prevEnt = (Entity) ambigNameTable.get (name);
        if (prevEnt != null)
        {
          prevEnt.maybeNameSpec = false;
          ent.maybeNameSpec = false;
        }
        else
          ambigNameTable.put (name, ent);
      }
    }
    Decl.examineElementNames (this, decls.elements ());

    for (final Enumeration e = paramEntityTable.elements (); e.hasMoreElements ();)
      ((Entity) e.nextElement ()).analyzeSemantic ();
  }

  void noteElementName (final String name, final Entity entity)
  {
    final Entity cur = (Entity) ambigNameTable.get (name);
    if (cur != null && cur != entity)
      cur.maybeNameSpec = false;
  }

  Vector createTopLevel ()
  {
    return Decl.declsToTopLevel (this, decls.elements ());
  }

  void dump ()
  {
    dumpEntity ("#doc", atoms);
  }

  private static void dumpEntity (final String name, final Vector atoms)
  {
    System.out.println ("<e name=\"" + name + "\">");
    dumpAtoms (atoms);
    System.out.println ("</e>");
  }

  private static void dumpAtoms (final Vector v)
  {
    final int n = v.size ();
    for (int i = 0; i < n; i++)
    {
      final Atom a = (Atom) v.elementAt (i);
      final Entity e = a.getEntity ();
      if (e != null)
        dumpEntity (e.name, e.atoms);
      else
        if (a.getTokenType () != Tokenizer.TOK_PROLOG_S)
        {
          System.out.print ("<t>");
          dumpString (a.getToken ());
          System.out.println ("</t>");
        }
    }
  }

  private static void dumpString (final String s)
  {
    final int n = s.length ();
    for (int i = 0; i < n; i++)
      switch (s.charAt (i))
      {
        case '<':
          System.out.print ("&lt;");
          break;
        case '>':
          System.out.print ("&gt;");
          break;
        case '&':
          System.out.print ("&amp;");
          break;
        default:
          System.out.print (s.charAt (i));
          break;
      }
  }

  void setNormalized (final String origValue, final String normalizedValue)
  {
    normalizedTable.put (origValue, normalizedValue);
  }

  String getNormalized (final String origValue)
  {
    return (String) normalizedTable.get (origValue);
  }
}
