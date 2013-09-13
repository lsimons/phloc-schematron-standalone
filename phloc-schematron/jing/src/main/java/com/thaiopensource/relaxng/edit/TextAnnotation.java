package com.thaiopensource.relaxng.edit;

public class TextAnnotation extends AnnotationChild
{
  private String value;

  public TextAnnotation (final String value)
  {
    this.value = value;
  }

  public String getValue ()
  {
    return value;
  }

  public void setValue (final String value)
  {
    this.value = value;
  }

  @Override
  public <T> T accept (final AnnotationChildVisitor <T> visitor)
  {
    return visitor.visitText (this);
  }
}
