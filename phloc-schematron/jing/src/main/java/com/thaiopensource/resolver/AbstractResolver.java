package com.thaiopensource.resolver;

import java.io.IOException;

/**
 *
 */
public class AbstractResolver implements Resolver
{
  public void resolve (final Identifier id, final Input input) throws IOException, ResolverException
  {
    // do nothing
  }

  public void open (final Input input) throws IOException, ResolverException
  {
    // do nothing
  }
}
