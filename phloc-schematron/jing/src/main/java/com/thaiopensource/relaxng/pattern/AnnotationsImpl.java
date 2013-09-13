package com.thaiopensource.relaxng.pattern;

import org.xml.sax.Locator;

import com.thaiopensource.relaxng.parse.Annotations;
import com.thaiopensource.relaxng.parse.BuildException;
import com.thaiopensource.util.VoidValue;

public abstract class AnnotationsImpl extends CommentListImpl implements
                                                             Annotations <Locator, VoidValue, CommentListImpl>
{
  public void addAttribute (final String ns,
                            final String localName,
                            final String prefix,
                            final String value,
                            final Locator loc) throws BuildException
  {}

  public void addElement (final VoidValue voidValue) throws BuildException
  {}

  public void addComment (final CommentListImpl comments) throws BuildException
  {}

  public void addLeadingComment (final CommentListImpl comments) throws BuildException
  {}
}
