package com.thaiopensource.xml.dtd.om;

public class TokenizedDatatype extends Datatype
{
  private final String typeName;

  public TokenizedDatatype (final String typeName)
  {
    this.typeName = typeName;
  }

  @Override
  public int getType ()
  {
    return TOKENIZED;
  }

  public String getTypeName ()
  {
    return typeName;
  }

  @Override
  public void accept (final DatatypeVisitor visitor) throws Exception
  {
    visitor.tokenizedDatatype (typeName);
  }
}
