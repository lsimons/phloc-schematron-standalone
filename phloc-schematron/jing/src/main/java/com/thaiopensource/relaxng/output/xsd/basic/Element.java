package com.thaiopensource.relaxng.output.xsd.basic;

import com.thaiopensource.relaxng.edit.SourceLocation;
import com.thaiopensource.xml.util.Name;

public class Element extends Particle implements Structure
{
  private final Name name;
  private final ComplexType complexType;

  public Element (final SourceLocation location,
                  final Annotation annotation,
                  final Name name,
                  final ComplexType complexType)
  {
    super (location, annotation);
    this.name = name;
    this.complexType = complexType;
  }

  public Name getName ()
  {
    return name;
  }

  public ComplexType getComplexType ()
  {
    return complexType;
  }

  @Override
  public <T> T accept (final ParticleVisitor <T> visitor)
  {
    return visitor.visitElement (this);
  }

  public <T> T accept (final StructureVisitor <T> visitor)
  {
    return visitor.visitElement (this);
  }

  @Override
  public boolean equals (final Object obj)
  {
    if (!super.equals (obj))
      return false;
    final Element other = (Element) obj;
    return this.name.equals (other.name) && this.complexType.equals (other.complexType);
  }

  @Override
  public int hashCode ()
  {
    return super.hashCode () ^ name.hashCode () ^ complexType.hashCode ();
  }
}
