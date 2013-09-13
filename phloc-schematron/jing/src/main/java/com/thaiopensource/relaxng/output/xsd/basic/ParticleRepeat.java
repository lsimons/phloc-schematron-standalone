package com.thaiopensource.relaxng.output.xsd.basic;

import com.thaiopensource.relaxng.edit.SourceLocation;

public class ParticleRepeat extends Particle
{
  private final Particle child;
  private final Occurs occurs;

  public ParticleRepeat (final SourceLocation location,
                         final Annotation annotation,
                         final Particle child,
                         final Occurs occurs)
  {
    super (location, annotation);
    this.child = child;
    this.occurs = occurs;
  }

  public Particle getChild ()
  {
    return child;
  }

  public Occurs getOccurs ()
  {
    return occurs;
  }

  @Override
  public <T> T accept (final ParticleVisitor <T> visitor)
  {
    return visitor.visitRepeat (this);
  }

  @Override
  public boolean equals (final Object obj)
  {
    if (!super.equals (obj))
      return false;
    final ParticleRepeat other = (ParticleRepeat) obj;
    return this.child.equals (other.child) && this.occurs.equals (other.occurs);
  }

  @Override
  public int hashCode ()
  {
    return super.hashCode () ^ child.hashCode () ^ occurs.hashCode ();
  }
}
