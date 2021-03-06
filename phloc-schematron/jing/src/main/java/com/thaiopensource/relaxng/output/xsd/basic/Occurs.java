package com.thaiopensource.relaxng.output.xsd.basic;

public class Occurs
{
  private final int min;
  private final int max;
  static public final int UNBOUNDED = Integer.MAX_VALUE;
  static public final Occurs EXACTLY_ONE = new Occurs (1, 1);
  static public final Occurs ONE_OR_MORE = new Occurs (1, UNBOUNDED);
  static public final Occurs ZERO_OR_MORE = new Occurs (0, UNBOUNDED);
  static public final Occurs OPTIONAL = new Occurs (0, 1);

  public Occurs (final int min, final int max)
  {
    this.min = min;
    this.max = max;
  }

  public int getMin ()
  {
    return min;
  }

  public int getMax ()
  {
    return max;
  }

  @Override
  public boolean equals (final Object obj)
  {
    if (!(obj instanceof Occurs))
      return false;
    return this.min == ((Occurs) obj).min && max == ((Occurs) obj).max;
  }

  @Override
  public int hashCode ()
  {
    return min ^ max;
  }

  static public Occurs add (final Occurs occ1, final Occurs occ2)
  {
    return new Occurs (occ1.min + occ2.min, occ1.max == UNBOUNDED || occ2.max == UNBOUNDED ? UNBOUNDED : occ1.max +
                                                                                                         occ2.max);
  }

  static public Occurs multiply (final Occurs occ1, final Occurs occ2)
  {
    return new Occurs (occ1.min * occ2.min, occ1.max == UNBOUNDED || occ2.max == UNBOUNDED ? UNBOUNDED : occ1.max *
                                                                                                         occ2.max);
  }

}
