package com.thaiopensource.resolver;

import java.io.IOException;

/**
 *
 */
public class SequenceResolver implements Resolver
{
  private final Resolver r1;
  private final Resolver r2;

  public SequenceResolver (final Resolver r1, final Resolver r2)
  {
    this.r1 = r1;
    this.r2 = r2;
  }

  public void resolve (final Identifier id, final Input input) throws IOException, ResolverException
  {
    r1.resolve (id, input);
    r2.resolve (id, input);
  }

  public void open (final Input input) throws IOException, ResolverException
  {
    r1.open (input);
    r2.open (input);
  }
}
