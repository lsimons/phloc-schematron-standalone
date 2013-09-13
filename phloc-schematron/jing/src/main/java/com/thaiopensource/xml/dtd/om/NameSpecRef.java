package com.thaiopensource.xml.dtd.om;

public class NameSpecRef extends NameSpec
{
  private final String name;
  private final NameSpec nameSpec;

  public NameSpecRef (final String name, final NameSpec nameSpec)
  {
    this.name = name;
    this.nameSpec = nameSpec;
  }

  @Override
  public int getType ()
  {
    return NAME_SPEC_REF;
  }

  public String getName ()
  {
    return name;
  }

  public NameSpec getNameSpec ()
  {
    return nameSpec;
  }

  @Override
  public void accept (final NameSpecVisitor visitor) throws Exception
  {
    visitor.nameSpecRef (name, nameSpec);
  }

  @Override
  public String getValue ()
  {
    return nameSpec.getValue ();
  }
}
