package com.thaiopensource.xml.dtd.om;

public class IncludedSection extends TopLevel
{

  private final Flag flag;
  private final TopLevel [] contents;

  public IncludedSection (final Flag flag, final TopLevel [] contents)
  {
    this.flag = flag;
    this.contents = contents;
  }

  @Override
  public int getType ()
  {
    return INCLUDED_SECTION;
  }

  public Flag getFlag ()
  {
    return flag;
  }

  public TopLevel [] getContents ()
  {
    final TopLevel [] tem = new TopLevel [contents.length];
    System.arraycopy (contents, 0, tem, 0, contents.length);
    return tem;
  }

  @Override
  public void accept (final TopLevelVisitor visitor) throws Exception
  {
    visitor.includedSection (flag, getContents ());
  }

}
