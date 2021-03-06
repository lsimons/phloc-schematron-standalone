package com.thaiopensource.relaxng.edit;

import java.util.List;
import java.util.Vector;

public abstract class Annotated extends SourceObject
{
  private final List <Comment> leadingComments = new Vector <Comment> ();
  private final List <AttributeAnnotation> attributeAnnotations = new Vector <AttributeAnnotation> ();
  private final List <AnnotationChild> childElementAnnotations = new Vector <AnnotationChild> ();
  private final List <AnnotationChild> followingElementAnnotations = new Vector <AnnotationChild> ();
  private NamespaceContext context;

  public List <Comment> getLeadingComments ()
  {
    return leadingComments;
  }

  public List <AttributeAnnotation> getAttributeAnnotations ()
  {
    return attributeAnnotations;
  }

  public List <AnnotationChild> getChildElementAnnotations ()
  {
    return childElementAnnotations;
  }

  public List <AnnotationChild> getFollowingElementAnnotations ()
  {
    return followingElementAnnotations;
  }

  public boolean mayContainText ()
  {
    return false;
  }

  public NamespaceContext getContext ()
  {
    return context;
  }

  public void setContext (final NamespaceContext context)
  {
    this.context = context;
  }

  public String getAttributeAnnotation (final String ns, final String localName)
  {
    for (final AttributeAnnotation a : attributeAnnotations)
      if (a.getNamespaceUri ().equals (ns) && a.getLocalName ().equals (localName))
        return a.getValue ();

    return null;
  }

  public void attributeAnnotationsAccept (final AttributeAnnotationVisitor <?> visitor)
  {
    for (final AttributeAnnotation a : attributeAnnotations)
      a.accept (visitor);
  }

  public void childElementAnnotationsAccept (final AnnotationChildVisitor <?> visitor)
  {
    for (final AnnotationChild a : childElementAnnotations)
      a.accept (visitor);
  }

  public void followingElementAnnotationsAccept (final AnnotationChildVisitor <?> visitor)
  {
    for (final AnnotationChild a : followingElementAnnotations)
      a.accept (visitor);
  }

  public void leadingCommentsAccept (final AnnotationChildVisitor <?> visitor)
  {
    for (final Comment c : leadingComments)
      c.accept (visitor);
  }
}
