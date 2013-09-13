package com.thaiopensource.relaxng.output.rng;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thaiopensource.relaxng.edit.AbstractVisitor;
import com.thaiopensource.relaxng.edit.Annotated;
import com.thaiopensource.relaxng.edit.AnnotationChild;
import com.thaiopensource.relaxng.edit.AnyNameNameClass;
import com.thaiopensource.relaxng.edit.AttributeAnnotation;
import com.thaiopensource.relaxng.edit.AttributePattern;
import com.thaiopensource.relaxng.edit.ChoiceNameClass;
import com.thaiopensource.relaxng.edit.Component;
import com.thaiopensource.relaxng.edit.CompositePattern;
import com.thaiopensource.relaxng.edit.Container;
import com.thaiopensource.relaxng.edit.DataPattern;
import com.thaiopensource.relaxng.edit.DefineComponent;
import com.thaiopensource.relaxng.edit.DivComponent;
import com.thaiopensource.relaxng.edit.ElementAnnotation;
import com.thaiopensource.relaxng.edit.ExternalRefPattern;
import com.thaiopensource.relaxng.edit.GrammarPattern;
import com.thaiopensource.relaxng.edit.IncludeComponent;
import com.thaiopensource.relaxng.edit.NameClass;
import com.thaiopensource.relaxng.edit.NameClassedPattern;
import com.thaiopensource.relaxng.edit.NameNameClass;
import com.thaiopensource.relaxng.edit.NamespaceContext;
import com.thaiopensource.relaxng.edit.NsNameNameClass;
import com.thaiopensource.relaxng.edit.Param;
import com.thaiopensource.relaxng.edit.Pattern;
import com.thaiopensource.relaxng.edit.UnaryPattern;
import com.thaiopensource.relaxng.edit.ValuePattern;
import com.thaiopensource.util.VoidValue;
import com.thaiopensource.xml.util.WellKnownNamespaces;

class Analyzer extends AbstractVisitor
{

  private VoidValue visitAnnotated (final Annotated anno)
  {
    if (anno.getAttributeAnnotations ().size () > 0 ||
        anno.getChildElementAnnotations ().size () > 0 ||
        anno.getFollowingElementAnnotations ().size () > 0)
      noteContext (anno.getContext ());
    visitAnnotationAttributes (anno.getAttributeAnnotations ());
    visitAnnotationChildren (anno.getChildElementAnnotations ());
    visitAnnotationChildren (anno.getFollowingElementAnnotations ());
    return VoidValue.VOID;
  }

  private void visitAnnotationAttributes (final List <AttributeAnnotation> list)
  {
    for (int i = 0, len = list.size (); i < len; i++)
    {
      final AttributeAnnotation att = list.get (i);
      if (att.getNamespaceUri ().length () != 0)
        noteNs (att.getPrefix (), att.getNamespaceUri ());
    }
  }

  private void visitAnnotationChildren (final List <AnnotationChild> list)
  {
    for (int i = 0, len = list.size (); i < len; i++)
    {
      final AnnotationChild ac = list.get (i);
      if (ac instanceof ElementAnnotation)
      {
        final ElementAnnotation elem = (ElementAnnotation) ac;
        if (elem.getPrefix () != null)
          noteNs (elem.getPrefix (), elem.getNamespaceUri ());
        visitAnnotationAttributes (elem.getAttributes ());
        visitAnnotationChildren (elem.getChildren ());
      }
    }
  }

  @Override
  public VoidValue visitPattern (final Pattern p)
  {
    return visitAnnotated (p);
  }

  @Override
  public VoidValue visitDefine (final DefineComponent c)
  {
    visitAnnotated (c);
    return c.getBody ().accept (this);
  }

  @Override
  public VoidValue visitDiv (final DivComponent c)
  {
    visitAnnotated (c);
    return visitContainer (c);
  }

  @Override
  public VoidValue visitInclude (final IncludeComponent c)
  {
    visitAnnotated (c);
    noteInheritNs (c.getNs ());
    return visitContainer (c);
  }

  @Override
  public VoidValue visitGrammar (final GrammarPattern p)
  {
    visitAnnotated (p);
    return visitContainer (p);
  }

  private VoidValue visitContainer (final Container c)
  {
    final List <Component> list = c.getComponents ();
    for (int i = 0, len = list.size (); i < len; i++)
      (list.get (i)).accept (this);
    return VoidValue.VOID;
  }

  @Override
  public VoidValue visitUnary (final UnaryPattern p)
  {
    visitAnnotated (p);
    return p.getChild ().accept (this);
  }

  @Override
  public VoidValue visitComposite (final CompositePattern p)
  {
    visitAnnotated (p);
    final List <Pattern> list = p.getChildren ();
    for (int i = 0, len = list.size (); i < len; i++)
      (list.get (i)).accept (this);
    return VoidValue.VOID;
  }

  @Override
  public VoidValue visitNameClassed (final NameClassedPattern p)
  {
    p.getNameClass ().accept (this);
    return visitUnary (p);
  }

  @Override
  public VoidValue visitAttribute (final AttributePattern p)
  {
    final NameClass nc = p.getNameClass ();
    if (nc instanceof NameNameClass && ((NameNameClass) nc).getNamespaceUri ().equals (""))
      return visitUnary (p);
    return visitNameClassed (p);
  }

  @Override
  public VoidValue visitChoice (final ChoiceNameClass nc)
  {
    visitAnnotated (nc);
    final List <NameClass> list = nc.getChildren ();
    for (int i = 0, len = list.size (); i < len; i++)
      (list.get (i)).accept (this);
    return VoidValue.VOID;
  }

  @Override
  public VoidValue visitValue (final ValuePattern p)
  {
    visitAnnotated (p);
    if (!p.getType ().equals ("token") || !p.getDatatypeLibrary ().equals (""))
      noteDatatypeLibrary (p.getDatatypeLibrary ());
    for (final Map.Entry <String, String> entry : p.getPrefixMap ().entrySet ())
    {
      noteNs (entry.getKey (), entry.getValue ());
    }
    return VoidValue.VOID;
  }

  @Override
  public VoidValue visitData (final DataPattern p)
  {
    visitAnnotated (p);
    noteDatatypeLibrary (p.getDatatypeLibrary ());
    final Pattern except = p.getExcept ();
    if (except != null)
      except.accept (this);
    for (final Param param : p.getParams ())
      visitAnnotated (param);
    return VoidValue.VOID;
  }

  @Override
  public VoidValue visitExternalRef (final ExternalRefPattern p)
  {
    visitAnnotated (p);
    noteInheritNs (p.getNs ());
    return VoidValue.VOID;
  }

  @Override
  public VoidValue visitName (final NameNameClass nc)
  {
    visitAnnotated (nc);
    noteNs (nc.getPrefix (), nc.getNamespaceUri ());
    return VoidValue.VOID;
  }

  @Override
  public VoidValue visitAnyName (final AnyNameNameClass nc)
  {
    visitAnnotated (nc);
    final NameClass except = nc.getExcept ();
    if (except != null)
      except.accept (this);
    return VoidValue.VOID;
  }

  @Override
  public VoidValue visitNsName (final NsNameNameClass nc)
  {
    visitAnnotated (nc);
    noteInheritNs (nc.getNs ());
    final NameClass except = nc.getExcept ();
    if (except != null)
      except.accept (this);
    return VoidValue.VOID;
  }

  private String datatypeLibrary = null;
  private final Map <String, String> prefixMap = new HashMap <String, String> ();
  private boolean haveInherit = false;
  private NamespaceContext lastContext = null;
  private String noPrefixNs = null;

  private void noteDatatypeLibrary (final String uri)
  {
    if (datatypeLibrary == null || datatypeLibrary.length () == 0)
      datatypeLibrary = uri;
  }

  private void noteInheritNs (final String ns)
  {
    if (ns == NameClass.INHERIT_NS)
      haveInherit = true;
    else
      noPrefixNs = ns;
  }

  private void noteNs (String prefix, final String ns)
  {
    if (ns == NameClass.INHERIT_NS)
    {
      haveInherit = true;
      return;
    }
    if (prefix == null)
      prefix = "";
    if (ns == null || (ns.length () == 0 && prefix.length () != 0) || prefixMap.containsKey (prefix))
      return;
    prefixMap.put (prefix, ns);
  }

  private void noteContext (final NamespaceContext context)
  {
    if (context == null || context == lastContext)
      return;
    lastContext = context;
    for (final String prefix : context.getPrefixes ())
      noteNs (prefix, context.getNamespace (prefix));
  }

  Map <String, String> getPrefixMap ()
  {
    if (haveInherit)
      prefixMap.remove ("");
    else
      if (noPrefixNs != null && !prefixMap.containsKey (""))
        prefixMap.put ("", noPrefixNs);
    prefixMap.put ("xml", WellKnownNamespaces.XML);
    return prefixMap;
  }

  String getDatatypeLibrary ()
  {
    return datatypeLibrary;
  }

}
