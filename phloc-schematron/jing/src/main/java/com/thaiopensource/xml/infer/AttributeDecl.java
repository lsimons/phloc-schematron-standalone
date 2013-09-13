package com.thaiopensource.xml.infer;

import com.thaiopensource.xml.util.Name;

public class AttributeDecl
{
  private final Name datatype;
  private final boolean optional;

  public AttributeDecl (final Name datatype, final boolean optional)
  {
    this.datatype = datatype;
    this.optional = optional;
  }

  /**
   * @return null for anything
   */
  public Name getDatatype ()
  {
    return datatype;
  }

  public boolean isOptional ()
  {
    return optional;
  }
}
