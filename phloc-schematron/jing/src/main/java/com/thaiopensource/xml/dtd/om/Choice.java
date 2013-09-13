package com.thaiopensource.xml.dtd.om;

public class Choice extends ModelGroup
{

  private final ModelGroup [] members;

  public Choice (final ModelGroup [] members)
  {
    this.members = members;
  }

  @Override
  public int getType ()
  {
    return CHOICE;
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
    visitor.choice (getMembers ());
  }
}
