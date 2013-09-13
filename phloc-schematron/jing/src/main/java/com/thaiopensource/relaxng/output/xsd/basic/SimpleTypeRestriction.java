package com.thaiopensource.relaxng.output.xsd.basic;

import java.util.Collections;
import java.util.List;

import com.thaiopensource.relaxng.edit.SourceLocation;

public class SimpleTypeRestriction extends SimpleType
{
  private final String name;
  private final List <Facet> facets;

  public SimpleTypeRestriction (final SourceLocation location,
                                final Annotation annotation,
                                final String name,
                                final List <Facet> facets)
  {
    super (location, annotation);
    this.name = name;
    this.facets = Collections.unmodifiableList (facets);
  }

  /**
   * Name is the name of a builtin simple type. facets is a list of facets
   */

  public String getName ()
  {
    return name;
  }

  public List <Facet> getFacets ()
  {
    return facets;
  }

  @Override
  public <T> T accept (final SimpleTypeVisitor <T> visitor)
  {
    return visitor.visitRestriction (this);
  }

  @Override
  public boolean equals (final Object obj)
  {
    if (!super.equals (obj))
      return false;
    final SimpleTypeRestriction other = (SimpleTypeRestriction) obj;
    return this.name.equals (other.name) && this.facets.equals (other.facets);
  }

  @Override
  public int hashCode ()
  {
    return super.hashCode () ^ name.hashCode () ^ facets.hashCode ();
  }
}
