package com.thaiopensource.relaxng.output.dtd;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.thaiopensource.relaxng.edit.AbstractPatternVisitor;
import com.thaiopensource.relaxng.edit.AnyNameNameClass;
import com.thaiopensource.relaxng.edit.AttributePattern;
import com.thaiopensource.relaxng.edit.ChoiceNameClass;
import com.thaiopensource.relaxng.edit.ChoicePattern;
import com.thaiopensource.relaxng.edit.Component;
import com.thaiopensource.relaxng.edit.ComponentVisitor;
import com.thaiopensource.relaxng.edit.CompositePattern;
import com.thaiopensource.relaxng.edit.Container;
import com.thaiopensource.relaxng.edit.DataPattern;
import com.thaiopensource.relaxng.edit.DefineComponent;
import com.thaiopensource.relaxng.edit.DivComponent;
import com.thaiopensource.relaxng.edit.ElementPattern;
import com.thaiopensource.relaxng.edit.EmptyPattern;
import com.thaiopensource.relaxng.edit.ExternalRefPattern;
import com.thaiopensource.relaxng.edit.GrammarPattern;
import com.thaiopensource.relaxng.edit.GroupPattern;
import com.thaiopensource.relaxng.edit.IncludeComponent;
import com.thaiopensource.relaxng.edit.InterleavePattern;
import com.thaiopensource.relaxng.edit.ListPattern;
import com.thaiopensource.relaxng.edit.MixedPattern;
import com.thaiopensource.relaxng.edit.NameClass;
import com.thaiopensource.relaxng.edit.NameClassVisitor;
import com.thaiopensource.relaxng.edit.NameNameClass;
import com.thaiopensource.relaxng.edit.NotAllowedPattern;
import com.thaiopensource.relaxng.edit.NsNameNameClass;
import com.thaiopensource.relaxng.edit.OneOrMorePattern;
import com.thaiopensource.relaxng.edit.OptionalPattern;
import com.thaiopensource.relaxng.edit.ParentRefPattern;
import com.thaiopensource.relaxng.edit.Pattern;
import com.thaiopensource.relaxng.edit.PatternVisitor;
import com.thaiopensource.relaxng.edit.RefPattern;
import com.thaiopensource.relaxng.edit.SchemaCollection;
import com.thaiopensource.relaxng.edit.TextPattern;
import com.thaiopensource.relaxng.edit.ValuePattern;
import com.thaiopensource.relaxng.edit.ZeroOrMorePattern;
import com.thaiopensource.relaxng.output.common.ErrorReporter;
import com.thaiopensource.relaxng.output.common.NameClassSplitter;
import com.thaiopensource.util.VoidValue;
import com.thaiopensource.xml.util.Name;
import com.thaiopensource.xml.util.Naming;
import com.thaiopensource.xml.util.WellKnownNamespaces;

class Analysis
{
  private final NamespaceManager nsm = new NamespaceManager ();
  private final AttlistMapper am = new AttlistMapper ();
  private final ErrorReporter er;
  private final Map <Pattern, ContentType> contentTypes = new HashMap <Pattern, ContentType> ();
  private final Map <Pattern, AttributeType> attributeTypes = new HashMap <Pattern, AttributeType> ();
  private final Map <Pattern, Set <Name>> attributeAlphabets = new HashMap <Pattern, Set <Name>> ();
  private final Map <Pattern, Set <String>> attributeNamespaces = new HashMap <Pattern, Set <String>> ();
  private Map <String, Pattern> defines = null;
  private final Set <String> attlists = new HashSet <String> ();
  private final Map <String, GrammarPart> parts = new HashMap <String, GrammarPart> ();
  private final Map <Pattern, Pattern> seenTable = new HashMap <Pattern, Pattern> ();
  private final Map <Name, ElementPattern> elementDecls = new HashMap <Name, ElementPattern> ();
  private ContentType startType = ContentType.ERROR;
  private GrammarPart mainPart;
  private final SchemaCollection schemas;
  private GrammarPattern grammarPattern;
  private final AttributeTyper attributeTyper = new AttributeTyper ();
  private final AttributeAlphabetComputer attributeAlphabetComputer = new AttributeAlphabetComputer ();
  private final AttributeNamespacesComputer attributeNamespacesComputer = new AttributeNamespacesComputer ();
  private final IncludeContentChecker includeContentChecker = new IncludeContentChecker ();
  private final static Set <String> EMPTY_STRING_SET = Collections.emptySet ();
  private final static Set <Name> EMPTY_NAME_SET = Collections.emptySet ();

  private class Analyzer implements
                        PatternVisitor <ContentType>,
                        ComponentVisitor <VoidValue>,
                        NameClassVisitor <VoidValue>
  {
    private ElementPattern ancestorPattern;
    private final Set <String> pendingRefs;

    public Analyzer ()
    {
      pendingRefs = new HashSet <String> ();
    }

    private Analyzer (final ElementPattern ancestorPattern)
    {
      this.ancestorPattern = ancestorPattern;
      pendingRefs = new HashSet <String> ();
    }

    private Analyzer (final Set <String> pendingRefs)
    {
      this.pendingRefs = pendingRefs;
    }

    public ContentType visitEmpty (final EmptyPattern p)
    {
      return ContentType.EMPTY;
    }

    public ContentType visitData (final DataPattern p)
    {
      return ContentType.SIMPLE_TYPE;
    }

    public ContentType visitValue (final ValuePattern p)
    {
      final Datatypes.Info info = Datatypes.getInfo (p.getDatatypeLibrary (), p.getType ());
      if (info.usesTokenEquality () && Naming.isNmtoken (p.getValue ()))
        return ContentType.ENUM;
      if (info.usesCdataEquality ())
        return ContentType.VALUE;
      return ContentType.SIMPLE_TYPE;
    }

    public ContentType visitElement (final ElementPattern p)
    {
      int len;
      if (seen (p))
        len = NameClassSplitter.split (p.getNameClass ()).size ();
      else
      {
        new Analyzer (p).analyzeContentType (p.getChild ());
        final List <NameNameClass> names = noteNames (p.getNameClass (), true);
        len = names.size ();
        for (int i = 0; i < len; i++)
        {
          final NameNameClass nnc = names.get (i);
          String ns = nnc.getNamespaceUri ();
          if (ns == NameClass.INHERIT_NS)
            ns = "";
          final Name name = new Name (ns, nnc.getLocalName ());
          final ElementPattern prev = elementDecls.get (name);
          if (prev != null)
          {
            er.error ("sorry_multiple_element", ns, name.getLocalName (), p.getSourceLocation ());
            er.error ("other_element", prev.getSourceLocation ());
          }
          else
            elementDecls.put (name, p);
        }
      }
      return len == 1 ? ContentType.DIRECT_SINGLE_ELEMENT : ContentType.ELEMENT_CLASS;
    }

    public ContentType visitAttribute (final AttributePattern p)
    {
      noteNames (p.getNameClass (), false);
      final ContentType t = analyzeContentType (p.getChild ());
      if (t.isA (ContentType.MODEL_GROUP) || t == ContentType.MIXED_ELEMENT_CLASS || t == ContentType.MIXED_MODEL)
        er.error ("bad_attribute_type", p.getSourceLocation ());
      if (ancestorPattern != null)
        am.noteAttribute (ancestorPattern);
      return ContentType.EMPTY;
    }

    private List <NameNameClass> noteNames (final NameClass nc, final boolean defaultable)
    {
      nc.accept (this);
      final List <NameNameClass> names = NameClassSplitter.split (nc);
      final int len = names.size ();
      for (int i = 0; i < len; i++)
        nsm.noteName (names.get (i), defaultable);
      return names;
    }

    public ContentType visitNotAllowed (final NotAllowedPattern p)
    {
      return ContentType.NOT_ALLOWED;
    }

    public ContentType visitText (final TextPattern p)
    {
      return ContentType.TEXT;
    }

    public ContentType visitList (final ListPattern p)
    {
      return ContentType.SIMPLE_TYPE;
    }

    public ContentType visitOneOrMore (final OneOrMorePattern p)
    {
      return checkContentType ("sorry_one_or_more",
                               ContentType.oneOrMore (analyzeContentTypeNullAncestorPattern (p.getChild ())),
                               p);
    }

    public ContentType visitZeroOrMore (final ZeroOrMorePattern p)
    {
      return checkContentType ("sorry_zero_or_more",
                               ContentType.zeroOrMore (analyzeContentTypeNullAncestorPattern (p.getChild ())),
                               p);
    }

    public ContentType visitChoice (final ChoicePattern p)
    {
      final List <Pattern> children = p.getChildren ();
      final Iterator <Pattern> iter = children.iterator ();
      ContentType tem = analyzeContentType (iter.next ());
      while (iter.hasNext ())
        tem = checkContentType ("sorry_choice", ContentType.choice (tem, analyzeContentType (iter.next ())), p);
      if (getAttributeType (p) == AttributeType.MULTI)
      {
        final Set <Name> attributeNames = new HashSet <Name> ();
        for (final Pattern child : children)
        {
          final Set <Name> childAttributeNames = getAttributeAlphabet (child);
          for (final Name name : childAttributeNames)
          {
            if (attributeNames.contains (name))
              er.error ("sorry_choice_attribute_name",
                        name.getNamespaceUri (),
                        name.getLocalName (),
                        p.getSourceLocation ());
            else
              attributeNames.add (name);
          }
        }
      }
      return tem;
    }

    public ContentType visitInterleave (final InterleavePattern p)
    {
      final List <Pattern> children = p.getChildren ();
      ContentType tem = analyzeContentType (children.get (0));
      for (int i = 1, len = children.size (); i < len; i++)
        tem = checkContentType ("sorry_interleave",
                                ContentType.interleave (tem, analyzeContentType (children.get (i))),
                                p);
      return tem;
    }

    public ContentType visitGroup (final GroupPattern p)
    {
      final List <Pattern> children = p.getChildren ();
      ContentType tem = analyzeContentType (children.get (0));
      for (int i = 1, len = children.size (); i < len; i++)
        tem = checkContentType ("sorry_group", ContentType.group (tem, analyzeContentType (children.get (i))), p);
      return tem;
    }

    public ContentType visitRef (final RefPattern p)
    {
      final String name = p.getName ();
      final Pattern def = getBody (name);
      if (def == null)
      {
        er.error ("undefined_ref", p.getSourceLocation ());
        return ContentType.ERROR;
      }
      if (pendingRefs.contains (name))
      {
        er.error ("ref_loop", p.getSourceLocation ());
        return ContentType.ERROR;
      }
      pendingRefs.add (name);
      final ContentType t = ContentType.ref (new Analyzer (pendingRefs).analyzeContentType (def));
      pendingRefs.remove (name);
      if (t.isA (ContentType.EMPTY))
        am.noteAttributeGroupRef (ancestorPattern, p.getName ());
      return ContentType.ref (t);
    }

    public ContentType visitParentRef (final ParentRefPattern p)
    {
      er.error ("sorry_parent_ref", p.getSourceLocation ());
      return null;
    }

    public ContentType visitGrammar (final GrammarPattern p)
    {
      if (defines != null)
      {
        er.error ("sorry_nested_grammar", p.getSourceLocation ());
        return ContentType.ERROR;
      }
      defines = new HashMap <String, Pattern> ();
      try
      {
        mainPart = new GrammarPart (er, defines, attlists, schemas, parts, p);
      }
      catch (final GrammarPart.IncludeLoopException e)
      {
        er.error ("include_loop", e.getInclude ().getSourceLocation ());
        return ContentType.ERROR;
      }
      grammarPattern = p;
      visitContainer (p);
      return startType;
    }

    public ContentType visitExternalRef (final ExternalRefPattern p)
    {
      er.error ("sorry_external_ref", p.getSourceLocation ());
      return null;
    }

    public ContentType visitMixed (final MixedPattern p)
    {
      return checkContentType ("sorry_mixed", ContentType.mixed (analyzeContentType (p.getChild ())), p);
    }

    public ContentType visitOptional (final OptionalPattern p)
    {
      return checkContentType ("sorry_optional",
                               ContentType.optional (analyzeContentTypeNullAncestorPattern (p.getChild ())),
                               p);
    }

    public VoidValue visitContainer (final Container c)
    {
      final List <Component> list = c.getComponents ();
      for (int i = 0, len = list.size (); i < len; i++)
        list.get (i).accept (this);
      return VoidValue.VOID;
    }

    public VoidValue visitDiv (final DivComponent c)
    {
      return visitContainer (c);
    }

    public VoidValue visitDefine (final DefineComponent c)
    {
      if (c.getName () == DefineComponent.START)
        startType = analyzeContentType (c.getBody ());
      else
        new Analyzer ().analyzeContentType (c.getBody ());
      if (attlists.contains (c.getName ()) && getContentType (c.getBody ()) != ContentType.EMPTY)
      {
        er.error ("not_attlist", c.getName (), c.getSourceLocation ());
        attlists.remove (c.getName ());
      }
      return VoidValue.VOID;
    }

    public VoidValue visitInclude (final IncludeComponent c)
    {
      includeContentChecker.visitContainer (c);
      visitContainer ((GrammarPattern) (schemas.getSchemaDocumentMap ().get (c.getUri ())).getPattern ());
      return VoidValue.VOID;
    }

    public VoidValue visitChoice (final ChoiceNameClass nc)
    {
      final List <NameClass> list = nc.getChildren ();
      for (int i = 0, len = list.size (); i < len; i++)
        list.get (i).accept (this);
      return VoidValue.VOID;
    }

    public VoidValue visitAnyName (final AnyNameNameClass nc)
    {
      er.error ("sorry_wildcard", nc.getSourceLocation ());
      return VoidValue.VOID;
    }

    public VoidValue visitNsName (final NsNameNameClass nc)
    {
      er.error ("sorry_wildcard", nc.getSourceLocation ());
      return VoidValue.VOID;
    }

    public VoidValue visitName (final NameNameClass nc)
    {
      nsm.noteName (nc, true);
      return VoidValue.VOID;
    }

    ContentType checkContentType (final String key, final ContentType t, final Pattern p)
    {
      if (t != null)
        return t;
      er.error (key, p.getSourceLocation ());
      return ContentType.ERROR;
    }

    ContentType analyzeContentType (final Pattern p)
    {
      ContentType t = contentTypes.get (p);
      if (t == null)
      {
        t = p.accept (this);
        contentTypes.put (p, t);
      }
      return t;
    }

    ContentType analyzeContentTypeNullAncestorPattern (final Pattern p)
    {
      return (ancestorPattern == null ? this : new Analyzer (pendingRefs)).analyzeContentType (p);
    }

  }

  class IncludeContentChecker implements ComponentVisitor <VoidValue>
  {
    public VoidValue visitContainer (final Container c)
    {
      final List <Component> list = c.getComponents ();
      for (int i = 0, len = list.size (); i < len; i++)
        list.get (i).accept (this);
      return VoidValue.VOID;
    }

    public VoidValue visitDefine (final DefineComponent c)
    {
      er.error ("sorry_include_override", c.getSourceLocation ());
      return VoidValue.VOID;
    }

    public VoidValue visitDiv (final DivComponent c)
    {
      return visitContainer (c);
    }

    public VoidValue visitInclude (final IncludeComponent c)
    {
      return VoidValue.VOID;
    }
  }

  private class AttributeTyper extends AbstractPatternVisitor <AttributeType>
  {
    @Override
    public AttributeType visitPattern (final Pattern p)
    {
      return AttributeType.EMPTY;
    }

    @Override
    public AttributeType visitMixed (final MixedPattern p)
    {
      return getAttributeType (p.getChild ());
    }

    @Override
    public AttributeType visitOneOrMore (final OneOrMorePattern p)
    {
      return getAttributeType (p.getChild ());
    }

    @Override
    public AttributeType visitZeroOrMore (final ZeroOrMorePattern p)
    {
      return getAttributeType (p.getChild ());
    }

    @Override
    public AttributeType visitOptional (final OptionalPattern p)
    {
      return getAttributeType (p.getChild ());
    }

    @Override
    public AttributeType visitComposite (final CompositePattern p)
    {
      final List <Pattern> list = p.getChildren ();
      AttributeType at = getAttributeType (list.get (0));
      for (int i = 1, len = list.size (); i < len; i++)
        at = AttributeType.group (at, getAttributeType (list.get (i)));
      return at;
    }

    @Override
    public AttributeType visitAttribute (final AttributePattern p)
    {
      return AttributeType.SINGLE;
    }

    @Override
    public AttributeType visitEmpty (final EmptyPattern p)
    {
      return AttributeType.MULTI;
    }

    @Override
    public AttributeType visitRef (final RefPattern p)
    {
      return getAttributeType (getBody (p.getName ()));
    }
  }

  private class AttributeAlphabetComputer extends AbstractPatternVisitor <Set <Name>>
  {
    @Override
    public Set <Name> visitPattern (final Pattern p)
    {
      return EMPTY_NAME_SET;
    }

    @Override
    public Set <Name> visitMixed (final MixedPattern p)
    {
      return getAttributeAlphabet (p.getChild ());
    }

    @Override
    public Set <Name> visitOneOrMore (final OneOrMorePattern p)
    {
      return getAttributeAlphabet (p.getChild ());
    }

    @Override
    public Set <Name> visitZeroOrMore (final ZeroOrMorePattern p)
    {
      return getAttributeAlphabet (p.getChild ());
    }

    @Override
    public Set <Name> visitOptional (final OptionalPattern p)
    {
      return getAttributeAlphabet (p.getChild ());
    }

    @Override
    public Set <Name> visitComposite (final CompositePattern p)
    {
      final List <Pattern> list = p.getChildren ();
      final Set <Name> result = new HashSet <Name> ();
      for (int i = 0, len = list.size (); i < len; i++)
        result.addAll (getAttributeAlphabet (list.get (i)));
      return result;
    }

    @Override
    public Set <Name> visitAttribute (final AttributePattern p)
    {
      final Set <Name> result = new HashSet <Name> ();
      final List <NameNameClass> names = NameClassSplitter.split (p.getNameClass ());
      for (int i = 0, len = names.size (); i < len; i++)
      {
        final NameNameClass nnc = names.get (i);
        String ns = nnc.getNamespaceUri ();
        if (ns == NameClass.INHERIT_NS)
          ns = "";
        result.add (new Name (ns, nnc.getLocalName ()));
      }
      return result;
    }

    @Override
    public Set <Name> visitRef (final RefPattern p)
    {
      return getAttributeAlphabet (getBody (p.getName ()));
    }
  }

  private class AttributeNamespacesComputer extends AbstractPatternVisitor <Set <String>>
  {
    @Override
    public Set <String> visitPattern (final Pattern p)
    {
      return EMPTY_STRING_SET;
    }

    @Override
    public Set <String> visitMixed (final MixedPattern p)
    {
      return getAttributeNamespaces (p.getChild ());
    }

    @Override
    public Set <String> visitOneOrMore (final OneOrMorePattern p)
    {
      return getAttributeNamespaces (p.getChild ());
    }

    @Override
    public Set <String> visitZeroOrMore (final ZeroOrMorePattern p)
    {
      return getAttributeNamespaces (p.getChild ());
    }

    @Override
    public Set <String> visitOptional (final OptionalPattern p)
    {
      return getAttributeNamespaces (p.getChild ());
    }

    @Override
    public Set <String> visitComposite (final CompositePattern p)
    {
      Set <String> result = EMPTY_STRING_SET;
      boolean newResult = false;
      for (final Pattern child : p.getChildren ())
      {
        final Set <String> tem = getAttributeNamespaces (child);
        if (tem != EMPTY_STRING_SET && !result.containsAll (tem))
        {
          if (result == EMPTY_STRING_SET)
            result = tem;
          else
          {
            if (!newResult)
            {
              result = new HashSet <String> (result);
              newResult = true;
            }
            result.addAll (tem);
          }
        }
      }
      if (newResult)
        result = Collections.unmodifiableSet (result);
      return result;
    }

    @Override
    public Set <String> visitAttribute (final AttributePattern p)
    {
      Set <String> result = null;
      final List <NameNameClass> names = NameClassSplitter.split (p.getNameClass ());
      for (final NameNameClass name : names)
      {
        final String ns = name.getNamespaceUri ();
        if (ns.length () != 0 && ns != NameClass.INHERIT_NS && !ns.equals (WellKnownNamespaces.XML))
        {
          if (result == null)
            result = new HashSet <String> ();
          result.add (ns);
        }
      }
      if (result == null)
        return EMPTY_STRING_SET;
      return Collections.unmodifiableSet (result);
    }

    @Override
    public Set <String> visitRef (final RefPattern p)
    {
      return getAttributeNamespaces (getBody (p.getName ()));
    }
  }

  private boolean seen (final Pattern p)
  {
    if (seenTable.get (p) != null)
      return true;
    seenTable.put (p, p);
    return false;
  }

  Analysis (final SchemaCollection schemas, final ErrorReporter er)
  {
    this.schemas = schemas;
    this.er = er;
    new Analyzer ().analyzeContentType (getPattern ());
    checkAttlists ();
    if (!er.getHadError ())
      nsm.assignPrefixes ();
  }

  private void checkAttlists ()
  {
    for (final String name : attlists)
      if (getParamEntityElementName (name) == null)
        er.error ("not_attlist", name, getBody (name).getSourceLocation ());
  }

  Pattern getPattern ()
  {
    return (schemas.getSchemaDocumentMap ().get (schemas.getMainUri ())).getPattern ();
  }

  String getPrefixForNamespaceUri (final String ns)
  {
    return nsm.getPrefixForNamespaceUri (ns);
  }

  String getElementPrefixForNamespaceUri (final String ns)
  {
    if (ns.equals ("") || ns.equals (nsm.getDefaultNamespaceUri ()) || ns == NameClass.INHERIT_NS)
      return null;
    return nsm.getPrefixForNamespaceUri (ns);
  }

  String getParamEntityElementName (final String name)
  {
    final NameNameClass nc = am.getParamEntityElementName (name);
    if (nc == null)
      return null;
    final String prefix = getElementPrefixForNamespaceUri (nc.getNamespaceUri ());
    final String localName = nc.getLocalName ();
    if (prefix == null)
      return localName;
    return prefix + ":" + localName;
  }

  ContentType getContentType (final Pattern p)
  {
    return contentTypes.get (p);
  }

  AttributeType getAttributeType (final Pattern p)
  {
    AttributeType at = attributeTypes.get (p);
    if (at == null)
    {
      at = p.accept (attributeTyper);
      attributeTypes.put (p, at);
    }
    return at;
  }

  Set <Name> getAttributeAlphabet (final Pattern p)
  {
    Set <Name> aa = attributeAlphabets.get (p);
    if (aa == null)
    {
      aa = Collections.unmodifiableSet (p.accept (attributeAlphabetComputer));
      attributeAlphabets.put (p, aa);
    }
    return aa;
  }

  Set <String> getAttributeNamespaces (final Pattern p)
  {
    Set <String> aa = attributeNamespaces.get (p);
    if (aa == null)
    {
      aa = p.accept (attributeNamespacesComputer);
      attributeNamespaces.put (p, aa);
    }
    return aa;
  }

  Pattern getBody (final String name)
  {
    return defines.get (name);
  }

  GrammarPattern getGrammarPattern ()
  {
    return grammarPattern;
  }

  String getMainUri ()
  {
    return schemas.getMainUri ();
  }

  GrammarPart getGrammarPart (final String sourceUri)
  {
    if (sourceUri.equals (schemas.getMainUri ()))
      return mainPart;
    else
      return parts.get (sourceUri);
  }

  Pattern getSchema (final String sourceUri)
  {
    return (schemas.getSchemaDocumentMap ().get (sourceUri)).getPattern ();
  }

  String getEncoding (final String sourceUri)
  {
    return (schemas.getSchemaDocumentMap ().get (sourceUri)).getEncoding ();
  }
}
