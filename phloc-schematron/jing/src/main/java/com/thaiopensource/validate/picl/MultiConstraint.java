package com.thaiopensource.validate.picl;

class MultiConstraint implements Constraint
{
  private final Constraint [] constraints;

  MultiConstraint (final Constraint [] constraints)
  {
    this.constraints = constraints;
  }

  public void activate (final PatternManager pm)
  {
    for (final Constraint constraint : constraints)
      constraint.activate (pm);
  }
}
