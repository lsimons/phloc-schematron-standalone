package com.thaiopensource.xml.dtd.om;

public class ZeroOrMore extends ModelGroup
{

  private final ModelGroup member;

  public ZeroOrMore (final ModelGroup member)
  {
    this.member = member;
  }

  @Override
  public int getType ()
  {
    return ZERO_OR_MORE;
  }

  public ModelGroup getMember ()
  {
    return member;
  }

  @Override
  public void accept (final ModelGroupVisitor visitor) throws Exception
  {
    visitor.zeroOrMore (member);
  }
}
