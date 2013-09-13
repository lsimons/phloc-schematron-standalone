package com.thaiopensource.relaxng.edit;

public class NameNameClass extends NameClass
{
  private String namespaceUri;
  private String localName;
  private String prefix;

  public NameNameClass (final String namespaceUri, final String localName)
  {
    this.namespaceUri = namespaceUri;
    this.localName = localName;
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

  /**
   * Returns non-empty string or null if there was no prefix.
   */
  public String getPrefix ()
  {
    return prefix;
  }

  /**
   * prefix must be non-empty string or null if there is no prefix.
   */
  public void setPrefix (final String prefix)
  {
    this.prefix = prefix;
  }

  @Override
  public boolean mayContainText ()
  {
    return true;
  }

  @Override
  public <T> T accept (final NameClassVisitor <T> visitor)
  {
    return visitor.visitName (this);
  }
}
