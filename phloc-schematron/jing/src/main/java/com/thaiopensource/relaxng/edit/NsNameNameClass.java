package com.thaiopensource.relaxng.edit;

public class NsNameNameClass extends OpenNameClass
{
  private String ns;

  public NsNameNameClass (final String ns)
  {
    this.ns = ns;
  }

  public NsNameNameClass (final String ns, final NameClass except)
  {
    super (except);
    this.ns = ns;
  }

  public String getNs ()
  {
    return ns;
  }

  public void setNs (final String ns)
  {
    this.ns = ns;
  }

  @Override
  public <T> T accept (final NameClassVisitor <T> visitor)
  {
    return visitor.visitNsName (this);
  }
}
