package com.thaiopensource.xml.dtd.om;

public class Comment extends TopLevel
{
  private final String value;

  public Comment (final String value)
  {
    this.value = value;
  }

  @Override
  public int getType ()
  {
    return COMMENT;
  }

  public String getValue ()
  {
    return value;
  }

  @Override
  public void accept (final TopLevelVisitor visitor) throws Exception
  {
    visitor.comment (value);
  }
}
