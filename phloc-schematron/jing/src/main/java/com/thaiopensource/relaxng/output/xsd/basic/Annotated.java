package com.thaiopensource.relaxng.output.xsd.basic;

import com.thaiopensource.relaxng.edit.SourceLocation;
import com.thaiopensource.util.Equal;

public class Annotated extends Located
{
  private final Annotation annotation;

  public Annotated (final SourceLocation location, final Annotation annotation)
  {
    super (location);
    this.annotation = annotation;
  }

  public Annotation getAnnotation ()
  {
    return annotation;
  }

  @Override
  public boolean equals (final Object obj)
  {
    if (obj == null)
      return false;
    if (this.getClass () != obj.getClass ())
      return false;
    return Equal.equal (annotation, ((Annotated) obj).annotation);
  }

  @Override
  public int hashCode ()
  {
    int hc = getClass ().hashCode ();
    if (annotation != null)
      hc ^= annotation.hashCode ();
    return hc;
  }
}
