package com.thaiopensource.relaxng.parse;

import org.xml.sax.SAXException;

import com.thaiopensource.resolver.ResolverException;

public class BuildException extends RuntimeException
{
  private final Throwable cause;

  public BuildException (final Throwable cause)
  {
    if (cause == null)
      throw new NullPointerException ("null cause");
    this.cause = cause;
  }

  @Override
  public Throwable getCause ()
  {
    return cause;
  }

  public static BuildException fromSAXException (final SAXException e)
  {
    final Exception inner = e.getException ();
    if (inner instanceof BuildException)
      return (BuildException) inner;
    return new BuildException (e);
  }

  public static BuildException fromResolverException (final ResolverException e)
  {
    if (e.getMessage () == null)
    {
      final Throwable t = e.unwrap ();
      if (t != null)
      {
        if (t instanceof BuildException)
          throw (BuildException) t;
        throw new BuildException (t);
      }
    }
    throw new BuildException (e);
  }
}
