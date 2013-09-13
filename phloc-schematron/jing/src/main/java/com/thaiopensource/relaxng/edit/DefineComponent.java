package com.thaiopensource.relaxng.edit;

public class DefineComponent extends Component
{
  public final static String START = new String ("#start");
  private String name;
  private Pattern body;
  private Combine combine;

  public DefineComponent (final String name, final Pattern body)
  {
    this.name = name;
    this.body = body;
  }

  public String getName ()
  {
    return name;
  }

  public void setName (final String name)
  {
    this.name = name;
  }

  public Pattern getBody ()
  {
    return body;
  }

  public void setBody (final Pattern body)
  {
    this.body = body;
  }

  public Combine getCombine ()
  {
    return combine;
  }

  public void setCombine (final Combine combine)
  {
    this.combine = combine;
  }

  @Override
  public <T> T accept (final ComponentVisitor <T> visitor)
  {
    return visitor.visitDefine (this);
  }
}
