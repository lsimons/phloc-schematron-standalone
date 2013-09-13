package com.thaiopensource.xml.infer;

import java.util.HashMap;
import java.util.Map;

import com.thaiopensource.xml.util.Name;

public class ElementDecl
{
  private Particle contentModel;
  private Name datatype;
  private final Map <Name, AttributeDecl> attributeDecls = new HashMap <Name, AttributeDecl> ();

  public Map <Name, AttributeDecl> getAttributeDecls ()
  {
    return attributeDecls;
  }

  public Particle getContentModel ()
  {
    return contentModel;
  }

  public void setContentModel (final Particle contentModel)
  {
    this.datatype = null;
    this.contentModel = contentModel;
  }

  public Name getDatatype ()
  {
    return datatype;
  }

  public void setDatatype (final Name datatype)
  {
    this.contentModel = null;
    this.datatype = datatype;
  }

}
