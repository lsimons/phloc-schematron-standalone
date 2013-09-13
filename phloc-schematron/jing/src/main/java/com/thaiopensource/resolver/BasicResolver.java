package com.thaiopensource.resolver;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 *
 */
public class BasicResolver implements Resolver
{
  static private final BasicResolver theInstance = new BasicResolver ();

  protected BasicResolver ()
  {}

  public static BasicResolver getInstance ()
  {
    return theInstance;
  }

  public void resolve (final Identifier id, final Input input) throws IOException, ResolverException
  {
    if (!input.isResolved ())
      input.setUri (resolveUri (id));
  }

  public void open (final Input input) throws IOException, ResolverException
  {
    if (!input.isUriDefinitive ())
      return;
    URI uri;
    try
    {
      uri = new URI (input.getUri ());
    }
    catch (final URISyntaxException e)
    {
      throw new ResolverException (e);
    }
    if (!uri.isAbsolute ())
      throw new ResolverException ("cannot open relative URI: " + uri);
    final URL url = new URL (uri.toASCIIString ());
    // XXX should set the encoding properly
    // XXX if this is HTTP and we've been redirected, should do input.setURI
    // with the new URI
    input.setByteStream (url.openStream ());
  }

  public static String resolveUri (final Identifier id) throws ResolverException
  {
    try
    {
      final String uriRef = id.getUriReference ();
      final URI uri = new URI (uriRef);
      if (!uri.isAbsolute ())
      {
        final String base = id.getBase ();
        if (base != null)
          return new URI (base).resolve (uri).toString ();
      }
      return uriRef;
    }
    catch (final URISyntaxException e)
    {
      throw new ResolverException (e);
    }
  }
}
