package com.thaiopensource.relaxng.edit;

import com.thaiopensource.util.VoidValue;

public class VoidVisitor implements
                        PatternVisitor <VoidValue>,
                        NameClassVisitor <VoidValue>,
                        ComponentVisitor <VoidValue>,
                        AnnotationChildVisitor <VoidValue>,
                        AttributeAnnotationVisitor <VoidValue>
{
  public final VoidValue visitElement (final ElementPattern p)
  {
    voidVisitElement (p);
    return VoidValue.VOID;
  }

  public void voidVisitElement (final ElementPattern p)
  {
    voidVisitPattern (p);
    p.getNameClass ().accept (this);
    p.getChild ().accept (this);
  }

  public void voidVisitPattern (final Pattern p)
  {
    voidVisitAnnotated (p);
  }

  public void voidVisitAnnotated (final Annotated p)
  {
    p.leadingCommentsAccept (this);
    p.attributeAnnotationsAccept (this);
    p.childElementAnnotationsAccept (this);
    p.followingElementAnnotationsAccept (this);
  }

  public final VoidValue visitChoice (final ChoiceNameClass nc)
  {
    voidVisitChoice (nc);
    return VoidValue.VOID;
  }

  public void voidVisitChoice (final ChoiceNameClass nc)
  {
    voidVisitNameClass (nc);
    nc.childrenAccept (this);
  }

  public void voidVisitNameClass (final NameClass nc)
  {
    voidVisitAnnotated (nc);
  }

  public final VoidValue visitDiv (final DivComponent c)
  {
    voidVisitDiv (c);
    return VoidValue.VOID;
  }

  public void voidVisitDiv (final DivComponent c)
  {
    voidVisitComponent (c);
    c.componentsAccept (this);
  }

  public void voidVisitComponent (final Component c)
  {
    voidVisitAnnotated (c);
  }

  public final VoidValue visitAttribute (final AttributePattern p)
  {
    voidVisitAttribute (p);
    return VoidValue.VOID;
  }

  public void voidVisitAttribute (final AttributePattern p)
  {
    voidVisitPattern (p);
    p.getNameClass ().accept (this);
    p.getChild ().accept (this);
  }

  public final VoidValue visitAnyName (final AnyNameNameClass nc)
  {
    voidVisitAnyName (nc);
    return VoidValue.VOID;
  }

  public void voidVisitAnyName (final AnyNameNameClass nc)
  {
    voidVisitNameClass (nc);
    final NameClass e = nc.getExcept ();
    if (e != null)
      e.accept (this);
  }

  public final VoidValue visitInclude (final IncludeComponent c)
  {
    voidVisitInclude (c);
    return VoidValue.VOID;
  }

  public void voidVisitInclude (final IncludeComponent c)
  {
    voidVisitComponent (c);
    c.componentsAccept (this);
  }

  public final VoidValue visitOneOrMore (final OneOrMorePattern p)
  {
    voidVisitOneOrMore (p);
    return VoidValue.VOID;
  }

  public void voidVisitOneOrMore (final OneOrMorePattern p)
  {
    voidVisitPattern (p);
    p.getChild ().accept (this);
  }

  public final VoidValue visitNsName (final NsNameNameClass nc)
  {
    voidVisitNsName (nc);
    return VoidValue.VOID;
  }

  public void voidVisitNsName (final NsNameNameClass nc)
  {
    voidVisitNameClass (nc);
    final NameClass e = nc.getExcept ();
    if (e != null)
      e.accept (this);
  }

  public final VoidValue visitDefine (final DefineComponent c)
  {
    voidVisitDefine (c);
    return VoidValue.VOID;
  }

  public void voidVisitDefine (final DefineComponent c)
  {
    voidVisitComponent (c);
    c.getBody ().accept (this);
  }

  public final VoidValue visitZeroOrMore (final ZeroOrMorePattern p)
  {
    voidVisitPattern (p);
    p.getChild ().accept (this);
    return VoidValue.VOID;
  }

  public final VoidValue visitName (final NameNameClass nc)
  {
    voidVisitName (nc);
    return VoidValue.VOID;
  }

  public void voidVisitName (final NameNameClass nc)
  {
    voidVisitNameClass (nc);
  }

  public final VoidValue visitOptional (final OptionalPattern p)
  {
    voidVisitOptional (p);
    return VoidValue.VOID;
  }

  public void voidVisitOptional (final OptionalPattern p)
  {
    voidVisitPattern (p);
    p.getChild ().accept (this);
  }

  public final VoidValue visitInterleave (final InterleavePattern p)
  {
    voidVisitInterleave (p);
    return VoidValue.VOID;
  }

  public void voidVisitInterleave (final InterleavePattern p)
  {
    voidVisitPattern (p);
    p.childrenAccept (this);
  }

  public final VoidValue visitGroup (final GroupPattern p)
  {
    voidVisitGroup (p);
    return VoidValue.VOID;
  }

  public void voidVisitGroup (final GroupPattern p)
  {
    voidVisitPattern (p);
    p.childrenAccept (this);
  }

  public final VoidValue visitChoice (final ChoicePattern p)
  {
    voidVisitChoice (p);
    return VoidValue.VOID;
  }

  public void voidVisitChoice (final ChoicePattern p)
  {
    voidVisitPattern (p);
    p.childrenAccept (this);
  }

  public final VoidValue visitGrammar (final GrammarPattern p)
  {
    voidVisitGrammar (p);
    return VoidValue.VOID;
  }

  public void voidVisitGrammar (final GrammarPattern p)
  {
    voidVisitPattern (p);
    p.componentsAccept (this);
  }

  public final VoidValue visitExternalRef (final ExternalRefPattern p)
  {
    voidVisitExternalRef (p);
    return VoidValue.VOID;
  }

  public void voidVisitExternalRef (final ExternalRefPattern p)
  {
    voidVisitPattern (p);
  }

  public final VoidValue visitRef (final RefPattern p)
  {
    voidVisitRef (p);
    return VoidValue.VOID;
  }

  public void voidVisitRef (final RefPattern p)
  {
    voidVisitPattern (p);
  }

  public final VoidValue visitParentRef (final ParentRefPattern p)
  {
    voidVisitParentRef (p);
    return VoidValue.VOID;
  }

  public void voidVisitParentRef (final ParentRefPattern p)
  {
    voidVisitPattern (p);
  }

  public final VoidValue visitValue (final ValuePattern p)
  {
    voidVisitValue (p);
    return VoidValue.VOID;
  }

  public void voidVisitValue (final ValuePattern p)
  {
    voidVisitPattern (p);
  }

  public final VoidValue visitData (final DataPattern p)
  {
    voidVisitData (p);
    return VoidValue.VOID;
  }

  public void voidVisitData (final DataPattern p)
  {
    voidVisitPattern (p);
    final Pattern e = p.getExcept ();
    if (e != null)
      e.accept (this);
    for (final Param param : p.getParams ())
      voidVisitAnnotated (param);
  }

  public final VoidValue visitMixed (final MixedPattern p)
  {
    voidVisitMixed (p);
    return VoidValue.VOID;
  }

  public void voidVisitMixed (final MixedPattern p)
  {
    voidVisitPattern (p);
    p.getChild ().accept (this);
  }

  public final VoidValue visitList (final ListPattern p)
  {
    voidVisitList (p);
    return VoidValue.VOID;
  }

  public void voidVisitList (final ListPattern p)
  {
    voidVisitPattern (p);
    p.getChild ().accept (this);
  }

  public final VoidValue visitText (final TextPattern p)
  {
    voidVisitText (p);
    return VoidValue.VOID;
  }

  public void voidVisitText (final TextPattern p)
  {
    voidVisitPattern (p);
  }

  public final VoidValue visitEmpty (final EmptyPattern p)
  {
    voidVisitEmpty (p);
    return VoidValue.VOID;
  }

  public void voidVisitEmpty (final EmptyPattern p)
  {
    voidVisitPattern (p);
  }

  public final VoidValue visitNotAllowed (final NotAllowedPattern p)
  {
    voidVisitNotAllowed (p);
    return VoidValue.VOID;
  }

  public void voidVisitNotAllowed (final NotAllowedPattern p)
  {
    voidVisitPattern (p);
  }

  public final VoidValue visitText (final TextAnnotation ta)
  {
    voidVisitText (ta);
    return VoidValue.VOID;
  }

  public void voidVisitText (final TextAnnotation ta)
  {
    voidVisitAnnotationChild (ta);
  }

  public final VoidValue visitComment (final Comment c)
  {
    voidVisitComment (c);
    return VoidValue.VOID;
  }

  public void voidVisitComment (final Comment c)
  {
    voidVisitAnnotationChild (c);
  }

  public final VoidValue visitElement (final ElementAnnotation ea)
  {
    voidVisitElement (ea);
    return VoidValue.VOID;
  }

  public void voidVisitElement (final ElementAnnotation ea)
  {
    voidVisitAnnotationChild (ea);
    ea.attributesAccept (this);
    ea.childrenAccept (this);
  }

  public void voidVisitAnnotationChild (final AnnotationChild ac)
  {}

  public final VoidValue visitAttribute (final AttributeAnnotation a)
  {
    voidVisitAttribute (a);
    return VoidValue.VOID;
  }

  public void voidVisitAttribute (final AttributeAnnotation a)
  {}
}
