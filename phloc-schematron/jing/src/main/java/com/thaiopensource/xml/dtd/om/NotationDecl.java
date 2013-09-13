package com.thaiopensource.xml.dtd.om;

import com.thaiopensource.xml.em.ExternalId;

public class NotationDecl extends TopLevel
{

  private final String name;
  private final ExternalId externalId;

  public NotationDecl (final String name, final ExternalId externalId)
  {
    this.name = name;
    this.externalId = externalId;
  }

  @Override
  public int getType ()
  {
    return NOTATION_DECL;
  }

  public String getName ()
  {
    return name;
  }

  public ExternalId getExternalId ()
  {
    return externalId;
  }

  @Override
  public void accept (final TopLevelVisitor visitor) throws Exception
  {
    visitor.notationDecl (name, externalId);
  }
}
