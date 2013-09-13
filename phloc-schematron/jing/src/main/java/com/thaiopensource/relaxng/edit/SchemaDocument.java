package com.thaiopensource.relaxng.edit;

public class SchemaDocument
{
  private Pattern pattern;
  private String encoding;

  public SchemaDocument (final Pattern pattern)
  {
    this.pattern = pattern;
  }

  public SchemaDocument (final Pattern pattern, final String encoding)
  {
    this.pattern = pattern;
    this.encoding = encoding;
  }

  public Pattern getPattern ()
  {
    return pattern;
  }

  public void setPattern (final Pattern pattern)
  {
    this.pattern = pattern;
  }

  public String getEncoding ()
  {
    return encoding;
  }

  public void setEncoding (final String encoding)
  {
    this.encoding = encoding;
  }
}
