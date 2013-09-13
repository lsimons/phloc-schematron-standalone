package com.thaiopensource.xml.dtd.om;

public class Optional extends ModelGroup
{

  private final ModelGroup member;

  public Optional (final ModelGroup member)
  {
    this.member = member;
  }

  @Override
  public int getType ()
  {
    return OPTIONAL;
  }

  public ModelGroup getMember ()
  {
    return member;
  }

  @Override
  public void accept (final ModelGroupVisitor visitor) throws Exception
  {
    visitor.optional (member);
  }
}
