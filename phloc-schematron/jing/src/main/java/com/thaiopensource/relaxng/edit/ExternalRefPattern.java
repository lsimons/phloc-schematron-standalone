package com.thaiopensource.relaxng.edit;

public class ExternalRefPattern extends Pattern
{
  private String uri;
  private String ns;
  private String href;
  private String baseUri;

  public ExternalRefPattern (final String uri)
  {
    this.uri = uri;
  }

  public String getUri ()
  {
    return uri;
  }

  public void setUri (final String uri)
  {
    this.uri = uri;
  }

  public String getNs ()
  {
    return ns;
  }

  public void setNs (final String ns)
  {
    this.ns = ns;
  }

  public String getHref ()
  {
    return href;
  }

  public void setHref (final String href)
  {
    this.href = href;
  }

  public String getBaseUri ()
  {
    return baseUri;
  }

  public void setBaseUri (final String baseUri)
  {
    this.baseUri = baseUri;
  }

  @Override
  public <T> T accept (final PatternVisitor <T> visitor)
  {
    return visitor.visitExternalRef (this);
  }
}
