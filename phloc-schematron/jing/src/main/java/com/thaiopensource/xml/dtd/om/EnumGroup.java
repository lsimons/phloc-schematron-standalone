package com.thaiopensource.xml.dtd.om;

public class EnumGroup
{
  private final EnumGroupMember [] members;

  public EnumGroup (final EnumGroupMember [] members)
  {
    this.members = members;
  }

  public EnumGroupMember [] getMembers ()
  {
    final EnumGroupMember [] tem = new EnumGroupMember [members.length];
    System.arraycopy (members, 0, tem, 0, members.length);
    return tem;
  }

  public void accept (final EnumGroupVisitor visitor) throws Exception
  {
    for (final EnumGroupMember member : members)
      member.accept (visitor);
  }
}
