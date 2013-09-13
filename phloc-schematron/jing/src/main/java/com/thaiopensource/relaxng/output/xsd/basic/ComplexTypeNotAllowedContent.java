package com.thaiopensource.relaxng.output.xsd.basic;

public class ComplexTypeNotAllowedContent extends ComplexType
{
  public ComplexTypeNotAllowedContent ()
  {}

  @Override
  public <T> T accept (final ComplexTypeVisitor <T> visitor)
  {
    return visitor.visitNotAllowedContent (this);
  }
}
