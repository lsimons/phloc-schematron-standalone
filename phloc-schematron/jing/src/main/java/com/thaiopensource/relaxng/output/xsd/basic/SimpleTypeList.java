package com.thaiopensource.relaxng.output.xsd.basic;

import com.thaiopensource.relaxng.edit.SourceLocation;

public class SimpleTypeList extends SimpleType
{
  private final SimpleType itemType;
  private final Occurs occurs;

  public SimpleTypeList (final SourceLocation location,
                         final Annotation annotation,
                         final SimpleType itemType,
                         final Occurs occurs)
  {
    super (location, annotation);
    this.itemType = itemType;
    this.occurs = occurs;
  }

  public SimpleType getItemType ()
  {
    return itemType;
  }

  public Occurs getOccurs ()
  {
    return occurs;
  }

  @Override
  public <T> T accept (final SimpleTypeVisitor <T> visitor)
  {
    return visitor.visitList (this);
  }

  @Override
  public boolean equals (final Object obj)
  {
    if (!super.equals (obj))
      return false;
    final SimpleTypeList other = (SimpleTypeList) obj;
    return this.itemType.equals (other.itemType) && this.occurs.equals (other.occurs);
  }

  @Override
  public int hashCode ()
  {
    return super.hashCode () ^ itemType.hashCode () ^ occurs.hashCode ();
  }
}
