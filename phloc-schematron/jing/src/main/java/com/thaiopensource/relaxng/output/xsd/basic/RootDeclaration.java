package com.thaiopensource.relaxng.output.xsd.basic;

import com.thaiopensource.relaxng.edit.SourceLocation;

public class RootDeclaration extends Annotated implements TopLevel
{
  private Particle particle;

  public RootDeclaration (final SourceLocation location, final Annotation annotation, final Particle particle)
  {
    super (location, annotation);
    this.particle = particle;
  }

  public Particle getParticle ()
  {
    return particle;
  }

  public void setParticle (final Particle particle)
  {
    this.particle = particle;
  }

  public void accept (final SchemaVisitor visitor)
  {
    visitor.visitRoot (this);
  }
}
