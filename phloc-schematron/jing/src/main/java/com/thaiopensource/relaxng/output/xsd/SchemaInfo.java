package com.thaiopensource.relaxng.output.xsd;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.thaiopensource.relaxng.edit.AbstractPatternVisitor;
import com.thaiopensource.relaxng.edit.AttributePattern;
import com.thaiopensource.relaxng.edit.ChoicePattern;
import com.thaiopensource.relaxng.edit.Combine;
import com.thaiopensource.relaxng.edit.ComponentVisitor;
import com.thaiopensource.relaxng.edit.CompositePattern;
import com.thaiopensource.relaxng.edit.DataPattern;
import com.thaiopensource.relaxng.edit.DefineComponent;
import com.thaiopensource.relaxng.edit.DivComponent;
import com.thaiopensource.relaxng.edit.ElementPattern;
import com.thaiopensource.relaxng.edit.EmptyPattern;
import com.thaiopensource.relaxng.edit.GrammarPattern;
import com.thaiopensource.relaxng.edit.GroupPattern;
import com.thaiopensource.relaxng.edit.IncludeComponent;
import com.thaiopensource.relaxng.edit.InterleavePattern;
import com.thaiopensource.relaxng.edit.ListPattern;
import com.thaiopensource.relaxng.edit.MixedPattern;
import com.thaiopensource.relaxng.edit.NotAllowedPattern;
import com.thaiopensource.relaxng.edit.OneOrMorePattern;
import com.thaiopensource.relaxng.edit.OptionalPattern;
import com.thaiopensource.relaxng.edit.Pattern;
import com.thaiopensource.relaxng.edit.PatternVisitor;
import com.thaiopensource.relaxng.edit.RefPattern;
import com.thaiopensource.relaxng.edit.SchemaCollection;
import com.thaiopensource.relaxng.edit.SchemaDocument;
import com.thaiopensource.relaxng.edit.TextPattern;
import com.thaiopensource.relaxng.edit.ValuePattern;
import com.thaiopensource.relaxng.edit.ZeroOrMorePattern;
import com.thaiopensource.relaxng.output.common.ErrorReporter;
import com.thaiopensource.util.VoidValue;

class SchemaInfo
{
  private final SchemaCollection sc;
  private final GrammarPattern grammar;
  private final ErrorReporter er;
  private final Map <Pattern, ChildType> childTypeMap = new HashMap <Pattern, ChildType> ();
  private final Map <String, Define> defineMap = new HashMap <String, Define> ();
  private final Set <DefineComponent> ignoredDefines = new HashSet <DefineComponent> ();
  private final PatternVisitor <ChildType> childTypeVisitor = new ChildTypeVisitor ();

  private static final int DEFINE_KEEP = 0;
  private static final int DEFINE_IGNORE = 1;
  private static final int DEFINE_REQUIRE = 2;

  static private class Define
  {
    int status = DEFINE_KEEP;
    boolean hadImplicit;
    Combine combine;
    Pattern pattern;
    CompositePattern wrapper;
    DefineComponent head;
  }

  abstract class PatternAnalysisVisitor <T> extends AbstractPatternVisitor <T>
  {
    abstract T get (Pattern p);

    abstract T choice (T o1, T o2);

    abstract T group (T o1, T o2);

    T interleave (final T o1, final T o2)
    {
      return group (o1, o2);
    }

    T ref (final T obj)
    {
      return obj;
    }

    T oneOrMore (final T obj)
    {
      return group (obj, obj);
    }

    abstract T empty ();

    abstract T text ();

    abstract T data ();

    abstract T notAllowed ();

    T list (final T obj)
    {
      return data ();
    }

    @Override
    public T visitChoice (final ChoicePattern p)
    {
      final List <Pattern> list = p.getChildren ();
      T obj = get (list.get (0));
      for (int i = 1, length = list.size (); i < length; i++)
        obj = choice (obj, get (list.get (i)));
      return obj;
    }

    @Override
    public T visitGroup (final GroupPattern p)
    {
      final List <Pattern> list = p.getChildren ();
      T obj = get (list.get (0));
      for (int i = 1, length = list.size (); i < length; i++)
        obj = group (obj, get (list.get (i)));
      return obj;
    }

    @Override
    public T visitInterleave (final InterleavePattern p)
    {
      final List <Pattern> list = p.getChildren ();
      T obj = get (list.get (0));
      for (int i = 1, length = list.size (); i < length; i++)
        obj = interleave (obj, get (list.get (i)));
      return obj;
    }

    @Override
    public T visitZeroOrMore (final ZeroOrMorePattern p)
    {
      return choice (empty (), oneOrMore (get (p.getChild ())));
    }

    @Override
    public T visitOneOrMore (final OneOrMorePattern p)
    {
      return oneOrMore (get (p.getChild ()));
    }

    @Override
    public T visitOptional (final OptionalPattern p)
    {
      return choice (empty (), get (p.getChild ()));
    }

    @Override
    public T visitEmpty (final EmptyPattern p)
    {
      return empty ();
    }

    @Override
    public T visitRef (final RefPattern p)
    {
      return ref (get (getBody (p)));
    }

    @Override
    public T visitMixed (final MixedPattern p)
    {
      return interleave (text (), get (p.getChild ()));
    }

    @Override
    public T visitText (final TextPattern p)
    {
      return text ();
    }

    @Override
    public T visitData (final DataPattern p)
    {
      return data ();
    }

    @Override
    public T visitValue (final ValuePattern p)
    {
      return data ();
    }

    @Override
    public T visitList (final ListPattern p)
    {
      return list (get (p.getChild ()));
    }

    @Override
    public T visitNotAllowed (final NotAllowedPattern p)
    {
      return notAllowed ();
    }

    @Override
    public T visitPattern (final Pattern p)
    {
      return null;
    }
  }

  class ChildTypeVisitor extends PatternAnalysisVisitor <ChildType>
  {
    @Override
    ChildType get (final Pattern p)
    {
      return getChildType (p);
    }

    @Override
    ChildType empty ()
    {
      return ChildType.EMPTY;
    }

    @Override
    ChildType text ()
    {
      return ChildType.choice (ChildType.TEXT, ChildType.EMPTY);
    }

    @Override
    ChildType data ()
    {
      return ChildType.DATA;
    }

    @Override
    ChildType notAllowed ()
    {
      return ChildType.NOT_ALLOWED;
    }

    @Override
    ChildType list (final ChildType t)
    {
      if (t == ChildType.NOT_ALLOWED)
        return t;
      return data ();
    }

    @Override
    ChildType choice (final ChildType t1, final ChildType t2)
    {
      return ChildType.choice (t1, t2);
    }

    @Override
    ChildType group (final ChildType t1, final ChildType t2)
    {
      return ChildType.group (t1, t2);
    }

    @Override
    public ChildType visitElement (final ElementPattern p)
    {
      return ChildType.ELEMENT;
    }

    @Override
    public ChildType visitAttribute (final AttributePattern p)
    {
      if (getChildType (p.getChild ()) == ChildType.NOT_ALLOWED)
        return ChildType.NOT_ALLOWED;
      return ChildType.choice (ChildType.ATTRIBUTE, ChildType.EMPTY);
    }
  }

  static class MyOverride
  {
    int status;
    final Define define;
    final String name;

    MyOverride (final Define define, final String name)
    {
      this.define = define;
      this.name = name;
    }
  }

  class GrammarVisitor implements ComponentVisitor <VoidValue>
  {
    private final Set <String> openIncludes = new HashSet <String> ();
    private final Set <String> allIncludes = new HashSet <String> ();
    private List <MyOverride> overrides = null;

    public VoidValue visitDefine (final DefineComponent c)
    {
      final Define define = lookupDefine (c.getName ());
      if (overrides != null)
        overrides.add (new MyOverride (define, c.getName ()));
      if (define.status != DEFINE_KEEP)
      {
        ignoredDefines.add (c);
        define.status = DEFINE_IGNORE;
        return VoidValue.VOID;
      }
      if (c.getCombine () == null)
      {
        if (define.hadImplicit)
        {
          er.error ("multiple_define", c.getName (), c.getSourceLocation ());
          return VoidValue.VOID;
        }
        define.hadImplicit = true;
      }
      else
        if (define.combine == null)
        {
          define.combine = c.getCombine ();
          if (define.combine == Combine.CHOICE)
            define.wrapper = new ChoicePattern ();
          else
            define.wrapper = new InterleavePattern ();
          define.wrapper.setSourceLocation (c.getSourceLocation ());
        }
        else
          if (define.combine != c.getCombine ())
          {
            er.error ("inconsistent_combine", c.getName (), c.getSourceLocation ());
            return VoidValue.VOID;
          }
      if (define.pattern == null)
      {
        define.pattern = c.getBody ();
        define.head = c;
      }
      else
      {
        if (define.pattern != define.wrapper)
          define.wrapper.getChildren ().add (define.pattern);
        define.wrapper.getChildren ().add (c.getBody ());
        define.pattern = define.wrapper;
      }
      return VoidValue.VOID;
    }

    public VoidValue visitDiv (final DivComponent c)
    {
      c.componentsAccept (this);
      return VoidValue.VOID;
    }

    public VoidValue visitInclude (final IncludeComponent c)
    {
      final List <MyOverride> overrides = new Vector <MyOverride> ();
      final List <MyOverride> savedOverrides = this.overrides;
      this.overrides = overrides;
      c.componentsAccept (this);
      this.overrides = savedOverrides;
      final String uri = c.getUri ();
      if (openIncludes.contains (uri))
        er.error ("include_loop", uri, c.getSourceLocation ());
      else
        if (allIncludes.contains (uri))
          er.error ("multiple_include", uri, c.getSourceLocation ());
        else
        {
          for (final MyOverride or : overrides)
          {
            or.status = or.define.status;
            or.define.status = DEFINE_REQUIRE;
          }
          allIncludes.add (uri);
          openIncludes.add (uri);
          getSchema (uri).componentsAccept (this);
          openIncludes.remove (uri);
          for (final MyOverride or : overrides)
          {
            if (or.define.status == DEFINE_REQUIRE)
            {
              if (or.name == DefineComponent.START)
                er.error ("missing_start_replacement", c.getSourceLocation ());
              else
                er.error ("missing_define_replacement", or.name, c.getSourceLocation ());
            }
            or.define.status = or.status;
          }
        }
      return VoidValue.VOID;
    }
  }

  SchemaInfo (final SchemaCollection sc, final ErrorReporter er)
  {
    this.sc = sc;
    this.er = er;
    forceGrammar ();
    grammar = getSchema (sc.getMainUri ());
    grammar.componentsAccept (new GrammarVisitor ());
  }

  private void forceGrammar ()
  {
    final SchemaDocument sd = sc.getSchemaDocumentMap ().get (sc.getMainUri ());
    sd.setPattern (convertToGrammar (sd.getPattern ()));
    // TODO convert other schemas
  }

  private static GrammarPattern convertToGrammar (final Pattern p)
  {
    if (p instanceof GrammarPattern)
      return (GrammarPattern) p;
    final GrammarPattern g = new GrammarPattern ();
    g.setSourceLocation (p.getSourceLocation ());
    g.setContext (p.getContext ());
    final DefineComponent dc = new DefineComponent (DefineComponent.START, p);
    dc.setSourceLocation (p.getSourceLocation ());
    g.getComponents ().add (dc);
    return g;
  }

  GrammarPattern getGrammar ()
  {
    return grammar;
  }

  String getMainUri ()
  {
    return sc.getMainUri ();
  }

  GrammarPattern getSchema (final String sourceUri)
  {
    return (GrammarPattern) (sc.getSchemaDocumentMap ().get (sourceUri)).getPattern ();
  }

  String getEncoding (final String sourceUri)
  {
    return (sc.getSchemaDocumentMap ().get (sourceUri)).getEncoding ();
  }

  ChildType getChildType (final Pattern p)
  {
    ChildType ct = childTypeMap.get (p);
    if (ct == null)
    {
      ct = p.accept (childTypeVisitor);
      childTypeMap.put (p, ct);
    }
    return ct;
  }

  Pattern getStart ()
  {
    return lookupDefine (DefineComponent.START).pattern;
  }

  Pattern getBody (final RefPattern p)
  {
    return lookupDefine (p.getName ()).pattern;
  }

  Pattern getBody (final DefineComponent c)
  {
    final Define def = lookupDefine (c.getName ());
    if (def == null || def.head != c)
      return null;
    return def.pattern;
  }

  boolean isIgnored (final DefineComponent c)
  {
    return ignoredDefines.contains (c);
  }

  private Define lookupDefine (final String name)
  {
    Define define = defineMap.get (name);
    if (define == null)
    {
      define = new Define ();
      defineMap.put (name, define);
    }
    return define;
  }

}
