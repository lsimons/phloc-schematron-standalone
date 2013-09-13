package com.thaiopensource.relaxng.input.parse;

import java.util.List;

import com.thaiopensource.relaxng.edit.AnnotationChild;
import com.thaiopensource.relaxng.edit.AttributeAnnotation;
import com.thaiopensource.relaxng.edit.ElementAnnotation;
import com.thaiopensource.relaxng.edit.SourceLocation;
import com.thaiopensource.relaxng.edit.TextAnnotation;
import com.thaiopensource.relaxng.parse.BuildException;
import com.thaiopensource.relaxng.parse.ElementAnnotationBuilder;

public class ElementAnnotationBuilderImpl implements
                                         ElementAnnotationBuilder <SourceLocation, ElementAnnotationBuilderImpl, CommentListImpl>
{
  private final ElementAnnotation element;
  private CommentListImpl comments;

  ElementAnnotationBuilderImpl (final CommentListImpl comments, final ElementAnnotation element)
  {
    this.comments = comments;
    this.element = element;
  }

  public void addText (final String value, final SourceLocation loc, final CommentListImpl comments) throws BuildException
  {
    final TextAnnotation t = new TextAnnotation (value);
    t.setSourceLocation (loc);
    if (comments != null)
      element.getChildren ().addAll (comments.list);
    element.getChildren ().add (t);
  }

  public void addAttribute (final String ns,
                            final String localName,
                            final String prefix,
                            final String value,
                            final SourceLocation loc) throws BuildException
  {
    final AttributeAnnotation att = new AttributeAnnotation (ns, localName, value);
    att.setPrefix (prefix);
    att.setSourceLocation (loc);
    element.getAttributes ().add (att);
  }

  public ElementAnnotationBuilderImpl makeElementAnnotation () throws BuildException
  {
    return this;
  }

  public void addElement (final ElementAnnotationBuilderImpl ea) throws BuildException
  {
    ea.addTo (element.getChildren ());
  }

  public void addComment (final CommentListImpl comments) throws BuildException
  {
    if (comments != null)
      element.getChildren ().addAll (comments.list);
  }

  public void addLeadingComment (final CommentListImpl comments) throws BuildException
  {
    if (this.comments == null)
      this.comments = comments;
    else
      if (comments != null)
        this.comments.add (comments);
  }

  void addTo (final List <AnnotationChild> elementList)
  {
    if (comments != null)
      elementList.addAll (comments.list);
    elementList.add (element);
  }
}
