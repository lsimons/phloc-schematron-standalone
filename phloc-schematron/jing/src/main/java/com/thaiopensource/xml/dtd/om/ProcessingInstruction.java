package com.thaiopensource.xml.dtd.om;

public class ProcessingInstruction extends TopLevel
{
  private final String target;
  private final String value;

  public ProcessingInstruction (final String target, final String value)
  {
    this.target = target;
    this.value = value;
  }

  @Override
  public int getType ()
  {
    return PROCESSING_INSTRUCTION;
  }

  public String getTarget ()
  {
    return target;
  }

  public String getValue ()
  {
    return value;
  }

  @Override
  public void accept (final TopLevelVisitor visitor) throws Exception
  {
    visitor.processingInstruction (target, value);
  }
}
