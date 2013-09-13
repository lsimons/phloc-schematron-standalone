package com.thaiopensource.relaxng.input.dtd;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.thaiopensource.resolver.Input;
import com.thaiopensource.resolver.Resolver;
import com.thaiopensource.resolver.ResolverException;
import com.thaiopensource.resolver.xml.ExternalEntityIdentifier;
import com.thaiopensource.xml.em.EntityManager;
import com.thaiopensource.xml.em.ExternalId;
import com.thaiopensource.xml.em.OpenEntity;

/**
 *
 */
public class ResolverEntityManager extends EntityManager
{
  private final Resolver resolver;

  public ResolverEntityManager (final Resolver resolver)
  {
    this.resolver = resolver;
  }

  @Override
  public OpenEntity open (final String systemId) throws IOException
  {
    final Input input = new Input ();
    input.setUri (systemId);
    try
    {
      return open (input);
    }
    catch (final ResolverException e)
    {
      throw toIOException (e);
    }
  }

  @Override
  public OpenEntity open (final ExternalId xid, final boolean isParameterEntity, String entityName) throws IOException
  {
    final Input input = new Input ();
    if (isParameterEntity)
      entityName = "%" + entityName;
    try
    {
      resolver.resolve (new ExternalEntityIdentifier (xid.getSystemId (),
                                                      xid.getBaseUri (),
                                                      xid.getPublicId (),
                                                      entityName), input);
      return open (input);
    }
    catch (final ResolverException e)
    {
      throw toIOException (e);
    }
  }

  private OpenEntity open (final Input input) throws ResolverException, IOException
  {
    resolver.open (input);
    if (!input.isOpen ())
      throw new ResolverException ("could not open input");
    final Reader reader = input.getCharacterStream ();
    String encoding = input.getEncoding ();
    final String systemId = input.getUri ();
    if (reader != null)
    {
      if (encoding == null)
        encoding = "UTF-8"; // XXX not sure if it's safe to pass null here
      return new OpenEntity (reader, systemId, systemId, encoding);
    }
    final InputStream in = input.getByteStream ();
    if (encoding != null)
      return new OpenEntity (new InputStreamReader (in, encoding), systemId, systemId, encoding);
    return detectEncoding (in, systemId);
  }

  private static IOException toIOException (final ResolverException e)
  {
    final String message = e.getMessage ();
    final Throwable cause = e.getCause ();
    if (message == null)
    {
      if (cause instanceof IOException)
        return (IOException) cause;
      // Avoid IOException(Throwable) because it's 1.6
      return new IOException (cause.getMessage ());
    }
    // Avoid IOException(String, Throwable) because it's 1.6
    return new IOException (message);
  }
}
