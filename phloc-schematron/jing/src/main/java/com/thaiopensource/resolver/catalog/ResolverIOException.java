package com.thaiopensource.resolver.catalog;

import java.io.IOException;

import com.thaiopensource.resolver.ResolverException;

/**
 * A wrapper for a ResolverException to allow it to be passed up by the catalog
 * parser.
 */
public class ResolverIOException extends IOException
{
  private final ResolverException resolverException;

  public ResolverIOException (final ResolverException resolverException)
  {
    this.resolverException = resolverException;
  }

  public ResolverException getResolverException ()
  {
    return resolverException;
  }
}
