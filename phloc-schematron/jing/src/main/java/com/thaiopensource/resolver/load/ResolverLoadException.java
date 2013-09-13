package com.thaiopensource.resolver.load;

/**
 *
 */
public class ResolverLoadException extends Exception
{
  public ResolverLoadException (final String message)
  {
    super (message);
  }

  public ResolverLoadException (final Throwable cause)
  {
    super (cause);
  }
}
