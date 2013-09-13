package com.thaiopensource.relaxng.pattern;

import org.relaxng.datatype.Datatype;

class DatatypeValue
{
  private final Object value;
  private final Datatype dt;

  DatatypeValue (final Object value, final Datatype dt)
  {
    this.value = value;
    this.dt = dt;
  }

  @Override
  public int hashCode ()
  {
    return dt.hashCode () ^ dt.valueHashCode (value);
  }

  @Override
  public boolean equals (final Object obj)
  {
    if (!(obj instanceof DatatypeValue))
      return false;
    final DatatypeValue other = (DatatypeValue) obj;
    if (other.dt != dt)
      return false;
    return dt.sameValue (value, other.value);
  }
}
