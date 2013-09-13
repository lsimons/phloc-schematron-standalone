package com.thaiopensource.relaxng.output.xsd.basic;

import com.thaiopensource.util.Equal;

public class Annotation
{
  private final String documentation;

  public Annotation (final String documentation)
  {
    this.documentation = documentation;
  }

  public String getDocumentation ()
  {
    return documentation;
  }

  @Override
  public boolean equals (final Object obj)
  {
    return obj instanceof Annotation && Equal.equal (documentation, ((Annotation) obj).documentation);
  }

  @Override
  public int hashCode ()
  {
    if (documentation != null)
      return documentation.hashCode ();
    return Annotation.class.hashCode ();
  }
}
