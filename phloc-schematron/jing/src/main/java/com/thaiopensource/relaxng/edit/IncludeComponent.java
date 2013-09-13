package com.thaiopensource.relaxng.edit;

import java.util.List;
import java.util.Vector;

public class IncludeComponent extends Component implements Container
{
  // the actual URI used
  private String uri;
  private String ns;
  // the specified href
  private String href;
  // the base for resolving the baseUri
  private String baseUri;
  private final List <Component> components = new Vector <Component> ();

  public IncludeComponent ()
  {}

  public IncludeComponent (final String uri)
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

  public List <Component> getComponents ()
  {
    return components;
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
  public <T> T accept (final ComponentVisitor <T> visitor)
  {
    return visitor.visitInclude (this);
  }

  public void componentsAccept (final ComponentVisitor <?> visitor)
  {
    for (final Component c : components)
      c.accept (visitor);
  }
}
