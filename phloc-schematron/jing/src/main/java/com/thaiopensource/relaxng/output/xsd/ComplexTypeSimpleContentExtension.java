package com.thaiopensource.relaxng.output.xsd;

import com.thaiopensource.relaxng.output.xsd.basic.AttributeUse;
import com.thaiopensource.relaxng.output.xsd.basic.ComplexTypeSimpleContent;
import com.thaiopensource.relaxng.output.xsd.basic.SimpleType;

class ComplexTypeSimpleContentExtension extends ComplexTypeSimpleContent
{
  private final String base;

  ComplexTypeSimpleContentExtension (final AttributeUse attributeUses, final SimpleType simpleType, final String base)
  {
    super (attributeUses, simpleType);
    this.base = base;
  }

  ComplexTypeSimpleContentExtension (final ComplexTypeSimpleContent ct)
  {
    super (ct.getAttributeUses (), ct.getSimpleType ());
    this.base = null;
  }

  String getBase ()
  {
    return base;
  }
}
