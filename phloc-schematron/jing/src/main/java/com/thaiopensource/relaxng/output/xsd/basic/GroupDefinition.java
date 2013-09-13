package com.thaiopensource.relaxng.output.xsd.basic;

import com.thaiopensource.relaxng.edit.SourceLocation;

public class GroupDefinition extends Definition
{
  private Particle particle;

  public GroupDefinition (final SourceLocation location,
                          final Annotation annotation,
                          final Schema parentSchema,
                          final String name,
                          final Particle particle)
  {
    super (location, annotation, parentSchema, name);
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
    visitor.visitGroup (this);
  }
}
