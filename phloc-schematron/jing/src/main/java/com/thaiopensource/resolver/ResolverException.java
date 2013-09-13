package com.thaiopensource.resolver;

/**
 *
 */
public class ResolverException extends Exception
{
  public ResolverException (final Throwable t)
  {
    super (t);
  }

  public ResolverException (final String message)
  {
    super (message);
  }

  public Throwable unwrap ()
  {
    if (getMessage () == null)
    {
      final Throwable t = getCause ();
      if (t != null)
        return t;
    }
    return this;
  }
}
