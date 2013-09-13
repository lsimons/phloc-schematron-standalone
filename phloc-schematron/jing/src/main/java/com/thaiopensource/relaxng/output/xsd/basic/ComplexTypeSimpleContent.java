package com.thaiopensource.relaxng.output.xsd.basic;

public class ComplexTypeSimpleContent extends ComplexTypeAllowedContent
{
  private final SimpleType simpleType;

  public ComplexTypeSimpleContent (final AttributeUse attributeUses, final SimpleType simpleType)
  {
    super (attributeUses);
    this.simpleType = simpleType;
  }

  public SimpleType getSimpleType ()
  {
    return simpleType;
  }

  @Override
  public <T> T accept (final ComplexTypeVisitor <T> visitor)
  {
    return visitor.visitSimpleContent (this);
  }

  @Override
  public boolean equals (final Object obj)
  {
    if (!(obj instanceof ComplexTypeSimpleContent))
      return false;
    final ComplexTypeSimpleContent other = (ComplexTypeSimpleContent) obj;
    return this.getAttributeUses ().equals (other.getAttributeUses ()) && this.simpleType.equals (other.simpleType);
  }

  @Override
  public int hashCode ()
  {
    return getAttributeUses ().hashCode () ^ simpleType.hashCode ();
  }
}
