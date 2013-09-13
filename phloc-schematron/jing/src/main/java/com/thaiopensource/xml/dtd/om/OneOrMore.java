package com.thaiopensource.xml.dtd.om;

public class OneOrMore extends ModelGroup
{

  private final ModelGroup member;

  public OneOrMore (final ModelGroup member)
  {
    this.member = member;
  }

  @Override
  public int getType ()
  {
    return ONE_OR_MORE;
  }

  public ModelGroup getMember ()
  {
    return member;
  }

  @Override
  public void accept (final ModelGroupVisitor visitor) throws Exception
  {
    visitor.oneOrMore (member);
  }
}
