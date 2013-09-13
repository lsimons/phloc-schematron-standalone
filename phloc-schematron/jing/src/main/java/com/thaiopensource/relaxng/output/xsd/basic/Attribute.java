package com.thaiopensource.relaxng.output.xsd.basic;

import com.thaiopensource.relaxng.edit.SourceLocation;
import com.thaiopensource.util.Equal;
import com.thaiopensource.xml.util.Name;

public class Attribute extends SingleAttributeUse implements Structure
{
  private final Name name;
  private final SimpleType type;

  /**
   * type may be null, indicating any type
   */

  public Attribute (final SourceLocation location, final Annotation annotation, final Name name, final SimpleType type)
  {
    super (location, annotation);
    this.name = name;
    this.type = type;
  }

  @Override
  public Name getName ()
  {
    return name;
  }

  @Override
  public SimpleType getType ()
  {
    return type;
  }

  @Override
  public <T> T accept (final AttributeUseVisitor <T> visitor)
  {
    return visitor.visitAttribute (this);
  }

  public <T> T accept (final StructureVisitor <T> visitor)
  {
    return visitor.visitAttribute (this);
  }

  @Override
  public boolean equals (final Object obj)
  {
    if (!super.equals (obj))
      return false;
    final Attribute other = (Attribute) obj;
    return Equal.equal (this.type, other.type) && this.name.equals (other.name);
  }

  @Override
  public int hashCode ()
  {
    int hc = super.hashCode () ^ name.hashCode ();
    if (type != null)
      hc ^= type.hashCode ();
    return hc;
  }

  @Override
  public boolean isOptional ()
  {
    return false;
  }
}
