package com.thaiopensource.relaxng.input.parse;

import java.util.List;
import java.util.Vector;

import com.thaiopensource.relaxng.edit.Comment;
import com.thaiopensource.relaxng.edit.SourceLocation;
import com.thaiopensource.relaxng.parse.BuildException;
import com.thaiopensource.relaxng.parse.CommentList;

public class CommentListImpl implements CommentList <SourceLocation>
{
  final List <Comment> list = new Vector <Comment> ();

  public void addComment (final String value, final SourceLocation loc) throws BuildException
  {
    final Comment comment = new Comment (value);
    comment.setSourceLocation (loc);
    list.add (comment);
  }

  void add (final CommentListImpl comments)
  {
    list.addAll (comments.list);
  }
}
