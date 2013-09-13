package com.thaiopensource.xml.dtd.om;

import com.thaiopensource.xml.em.ExternalId;

public class ExternalEntityDecl extends TopLevel
{

  private final String name;
  private final ExternalId externalId;

  public ExternalEntityDecl (final String name, final ExternalId externalId)
  {
    this.name = name;
    this.externalId = externalId;
  }

  @Override
  public int getType ()
  {
    return EXTERNAL_ENTITY_DECL;
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
    visitor.externalEntityDecl (name, externalId);
  }
}
