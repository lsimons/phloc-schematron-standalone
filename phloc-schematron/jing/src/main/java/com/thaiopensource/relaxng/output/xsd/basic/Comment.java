package com.thaiopensource.relaxng.output.xsd.basic;

import com.thaiopensource.relaxng.edit.SourceLocation;

public class Comment extends Located implements TopLevel
{
  private String content;

  public Comment (final SourceLocation location, final String content)
  {
    super (location);
    this.content = content;
  }

  public String getContent ()
  {
    return content;
  }

  public void setContent (final String content)
  {
    this.content = content;
  }

  public void accept (final SchemaVisitor visitor)
  {
    visitor.visitComment (this);
  }
}
