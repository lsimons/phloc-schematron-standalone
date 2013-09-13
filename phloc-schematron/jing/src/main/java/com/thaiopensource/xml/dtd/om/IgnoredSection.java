package com.thaiopensource.xml.dtd.om;

public class IgnoredSection extends TopLevel
{

  private final Flag flag;
  private final String contents;

  public IgnoredSection (final Flag flag, final String contents)
  {
    this.flag = flag;
    this.contents = contents;
  }

  @Override
  public int getType ()
  {
    return IGNORED_SECTION;
  }

  public Flag getFlag ()
  {
    return flag;
  }

  public String getContents ()
  {
    return contents;
  }

  @Override
  public void accept (final TopLevelVisitor visitor) throws Exception
  {
    visitor.ignoredSection (flag, contents);
  }

}
