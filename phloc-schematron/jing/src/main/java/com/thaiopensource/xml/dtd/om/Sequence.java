package com.thaiopensource.xml.dtd.om;

public class Sequence extends ModelGroup
{

  private final ModelGroup [] members;

  public Sequence (final ModelGroup [] members)
  {
    this.members = members;
  }

  @Override
  public int getType ()
  {
    return SEQUENCE;
  }

  public ModelGroup [] getMembers ()
  {
    final ModelGroup [] tem = new ModelGroup [members.length];
    System.arraycopy (members, 0, tem, 0, members.length);
    return tem;
  }

  @Override
  public void accept (final ModelGroupVisitor visitor) throws Exception
  {
    visitor.sequence (getMembers ());
  }
}
