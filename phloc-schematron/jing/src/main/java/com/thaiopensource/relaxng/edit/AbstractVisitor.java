package com.thaiopensource.relaxng.edit;

import com.thaiopensource.util.VoidValue;

public class AbstractVisitor extends AbstractPatternVisitor <VoidValue> implements
                                                                       ComponentVisitor <VoidValue>,
                                                                       NameClassVisitor <VoidValue>,
                                                                       AnnotationChildVisitor <VoidValue>,
                                                                       AttributeAnnotationVisitor <VoidValue>
{

  @Override
  public VoidValue visitPattern (final Pattern p)
  {
    return VoidValue.VOID;
  }

  public VoidValue visitDefine (final DefineComponent c)
  {
    return visitComponent (c);
  }

  public VoidValue visitDiv (final DivComponent c)
  {
    return visitComponent (c);
  }

  public VoidValue visitInclude (final IncludeComponent c)
  {
    return visitComponent (c);
  }

  public VoidValue visitComponent (final Component c)
  {
    return VoidValue.VOID;
  }

  public VoidValue visitChoice (final ChoiceNameClass nc)
  {
    return visitNameClass (nc);
  }

  public VoidValue visitAnyName (final AnyNameNameClass nc)
  {
    return visitNameClass (nc);
  }

  public VoidValue visitNsName (final NsNameNameClass nc)
  {
    return visitNameClass (nc);
  }

  public VoidValue visitName (final NameNameClass nc)
  {
    return visitNameClass (nc);
  }

  public VoidValue visitNameClass (final NameClass nc)
  {
    return VoidValue.VOID;
  }

  public VoidValue visitText (final TextAnnotation ta)
  {
    return visitAnnotationChild (ta);
  }

  public VoidValue visitComment (final Comment c)
  {
    return visitAnnotationChild (c);
  }

  public VoidValue visitElement (final ElementAnnotation ea)
  {
    return visitAnnotationChild (ea);
  }

  public VoidValue visitAnnotationChild (final AnnotationChild ac)
  {
    return VoidValue.VOID;
  }

  public VoidValue visitAttribute (final AttributeAnnotation a)
  {
    return VoidValue.VOID;
  }
}
