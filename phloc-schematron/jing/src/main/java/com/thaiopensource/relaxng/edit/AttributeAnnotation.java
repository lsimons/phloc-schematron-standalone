package com.thaiopensource.relaxng.edit;

public class AttributeAnnotation extends SourceObject
{
  private String namespaceUri;
  private String localName;
  private String prefix;
  private String value;

  /**
   * namespaceUri is never null; empty string is used for absent namespace
   */
  public AttributeAnnotation (final String namespaceUri, final String localName, final String value)
  {
    this.namespaceUri = namespaceUri;
    this.localName = localName;
    this.value = value;
  }

  public String getNamespaceUri ()
  {
    return namespaceUri;
  }

  public void setNamespaceUri (final String namespaceUri)
  {
    this.namespaceUri = namespaceUri;
  }

  public String getLocalName ()
  {
    return localName;
  }

  public void setLocalName (final String localName)
  {
    this.localName = localName;
  }

  public String getPrefix ()
  {
    return prefix;
  }

  public void setPrefix (final String prefix)
  {
    this.prefix = prefix;
  }

  public String getValue ()
  {
    return value;
  }

  public void setValue (final String value)
  {
    this.value = value;
  }

  public <T> T accept (final AttributeAnnotationVisitor <T> visitor)
  {
    return visitor.visitAttribute (this);
  }
}
