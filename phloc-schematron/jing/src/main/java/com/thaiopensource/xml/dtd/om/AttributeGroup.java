package com.thaiopensource.xml.dtd.om;

public class AttributeGroup
{
  private final AttributeGroupMember [] members;

  public AttributeGroup (final AttributeGroupMember [] members)
  {
    this.members = members;
  }

  public AttributeGroupMember [] getMembers ()
  {
    final AttributeGroupMember [] tem = new AttributeGroupMember [members.length];
    System.arraycopy (members, 0, tem, 0, members.length);
    return tem;
  }

  public void accept (final AttributeGroupVisitor visitor) throws Exception
  {
    for (final AttributeGroupMember member : members)
      member.accept (visitor);
  }
}
