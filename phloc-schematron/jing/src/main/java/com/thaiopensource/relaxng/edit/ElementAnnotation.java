package com.thaiopensource.relaxng.edit;

import java.util.List;
import java.util.Vector;

public class ElementAnnotation extends AnnotationChild
{
  private String namespaceUri;
  private String localName;
  private String prefix;
  private NamespaceContext context;
  private final List <AttributeAnnotation> attributes = new Vector <AttributeAnnotation> ();
  private final List <AnnotationChild> children = new Vector <AnnotationChild> ();

  public ElementAnnotation (final String namespaceUri, final String localName)
  {
    this.namespaceUri = namespaceUri;
    this.localName = localName;
  }

  public List <AttributeAnnotation> getAttributes ()
  {
    return attributes;
  }

  public List <AnnotationChild> getChildren ()
  {
    return children;
  }

  public String getNamespaceUri ()
  {
    return namespaceUri;
  }

  public void setNamespaceUri (final String namespaceUri)
  {
    this.namespaceUri = namespaceUri;
  }

  public String getLocalName ()
  {
    return localName;
  }

  public void setLocalName (final String localName)
  {
    this.localName = localName;
  }

  public String getPrefix ()
  {
    return prefix;
  }

  public void setPrefix (final String prefix)
  {
    this.prefix = prefix;
  }

  public NamespaceContext getContext ()
  {
    return context;
  }

  public void setContext (final NamespaceContext context)
  {
    this.context = context;
  }

  @Override
  public <T> T accept (final AnnotationChildVisitor <T> visitor)
  {
    return visitor.visitElement (this);
  }

  public void attributesAccept (final AttributeAnnotationVisitor <?> visitor)
  {
    for (final AttributeAnnotation a : attributes)
      a.accept (visitor);
  }

  public void childrenAccept (final AnnotationChildVisitor <?> visitor)
  {
    for (final AnnotationChild c : children)
      c.accept (visitor);
  }
}
