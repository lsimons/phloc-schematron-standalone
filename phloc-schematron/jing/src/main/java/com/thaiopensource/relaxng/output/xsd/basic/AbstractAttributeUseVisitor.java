package com.thaiopensource.relaxng.output.xsd.basic;

public abstract class AbstractAttributeUseVisitor <T> implements AttributeUseVisitor <T>
{
  public T visitAttributeUseChoice (final AttributeUseChoice a)
  {
    return visitAttributeGroup (a);
  }
}
