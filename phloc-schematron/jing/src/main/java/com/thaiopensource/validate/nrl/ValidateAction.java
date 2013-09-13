package com.thaiopensource.validate.nrl;

import org.xml.sax.SAXException;

import com.thaiopensource.validate.Schema;

class ValidateAction extends NoResultAction
{
  private final Schema schema;

  ValidateAction (final ModeUsage modeUsage, final Schema schema)
  {
    super (modeUsage);
    this.schema = schema;
  }

  @Override
  void perform (final SectionState state) throws SAXException
  {
    state.addValidator (schema, getModeUsage ());
  }

  @Override
  NoResultAction changeCurrentMode (final Mode mode)
  {
    return new ValidateAction (getModeUsage ().changeCurrentMode (mode), schema);
  }

  @Override
  public boolean equals (final Object obj)
  {
    return super.equals (obj) && schema.equals (((ValidateAction) obj).schema);
  }

  @Override
  public int hashCode ()
  {
    return super.hashCode () ^ schema.hashCode ();
  }
}
