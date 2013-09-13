package com.thaiopensource.relaxng.output.xsd.basic;

import com.thaiopensource.util.VoidValue;

public abstract class SchemaWalker implements
                                  ParticleVisitor <VoidValue>,
                                  SimpleTypeVisitor <VoidValue>,
                                  SchemaVisitor,
                                  ComplexTypeVisitor <VoidValue>,
                                  AttributeUseVisitor <VoidValue>
{
  public VoidValue visitElement (final Element p)
  {
    return p.getComplexType ().accept (this);
  }

  public VoidValue visitWildcardElement (final WildcardElement p)
  {
    return VoidValue.VOID;
  }

  public VoidValue visitRepeat (final ParticleRepeat p)
  {
    return p.getChild ().accept (this);
  }

  public VoidValue visitSequence (final ParticleSequence p)
  {
    return visitGroup (p);
  }

  public VoidValue visitChoice (final ParticleChoice p)
  {
    return visitGroup (p);
  }

  public VoidValue visitAll (final ParticleAll p)
  {
    return visitGroup (p);
  }

  public VoidValue visitGroup (final ParticleGroup p)
  {
    for (final Particle child : p.getChildren ())
      child.accept (this);
    return VoidValue.VOID;
  }

  public VoidValue visitGroupRef (final GroupRef p)
  {
    return VoidValue.VOID;
  }

  public VoidValue visitRestriction (final SimpleTypeRestriction t)
  {
    return VoidValue.VOID;
  }

  public VoidValue visitUnion (final SimpleTypeUnion t)
  {
    for (final SimpleType child : t.getChildren ())
      child.accept (this);
    return VoidValue.VOID;
  }

  public VoidValue visitList (final SimpleTypeList t)
  {
    return t.getItemType ().accept (this);
  }

  public VoidValue visitRef (final SimpleTypeRef t)
  {
    return VoidValue.VOID;
  }

  public void visitGroup (final GroupDefinition def)
  {
    def.getParticle ().accept (this);
  }

  public void visitAttributeGroup (final AttributeGroupDefinition def)
  {
    def.getAttributeUses ().accept (this);
  }

  public VoidValue visitAttribute (final Attribute a)
  {
    if (a.getType () == null)
      return VoidValue.VOID;
    return a.getType ().accept (this);
  }

  public VoidValue visitWildcardAttribute (final WildcardAttribute a)
  {
    return VoidValue.VOID;
  }

  public VoidValue visitOptionalAttribute (final OptionalAttribute a)
  {
    return a.getAttribute ().accept (this);
  }

  public VoidValue visitAttributeGroupRef (final AttributeGroupRef a)
  {
    return VoidValue.VOID;
  }

  public VoidValue visitAttributeGroup (final AttributeGroup a)
  {
    for (final AttributeUse child : a.getChildren ())
      child.accept (this);
    return VoidValue.VOID;
  }

  public VoidValue visitAttributeUseChoice (final AttributeUseChoice a)
  {
    return visitAttributeGroup (a);
  }

  public void visitSimpleType (final SimpleTypeDefinition def)
  {
    def.getSimpleType ().accept (this);
  }

  public void visitRoot (final RootDeclaration decl)
  {
    decl.getParticle ().accept (this);
  }

  public void visitInclude (final Include include)
  {
    include.getIncludedSchema ().accept (this);
  }

  public void visitComment (final Comment comment)
  {}

  public VoidValue visitComplexContent (final ComplexTypeComplexContent t)
  {
    t.getAttributeUses ().accept (this);
    if (t.getParticle () == null)
      return VoidValue.VOID;
    return t.getParticle ().accept (this);
  }

  public VoidValue visitSimpleContent (final ComplexTypeSimpleContent t)
  {
    t.getAttributeUses ().accept (this);
    return t.getSimpleType ().accept (this);
  }

  public VoidValue visitNotAllowedContent (final ComplexTypeNotAllowedContent t)
  {
    return VoidValue.VOID;
  }
}
