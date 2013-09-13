package com.thaiopensource.xml.dtd.om;

import com.thaiopensource.xml.em.ExternalId;

public class ExternalIdDef extends Def
{
  private final ExternalId externalId;

  public ExternalIdDef (final String name, final ExternalId externalId)
  {
    super (name);
    this.externalId = externalId;
  }

  @Override
  public int getType ()
  {
    return EXTERNAL_ID_DEF;
  }

  public ExternalId getExternalId ()
  {
    return externalId;
  }

  @Override
  public void accept (final TopLevelVisitor visitor) throws Exception
  {
    visitor.externalIdDef (getName (), externalId);
  }
}
