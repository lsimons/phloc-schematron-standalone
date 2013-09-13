package com.thaiopensource.resolver.xml.sax;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.EntityResolver2;

import com.thaiopensource.resolver.AbstractResolver;
import com.thaiopensource.resolver.BasicResolver;
import com.thaiopensource.resolver.Identifier;
import com.thaiopensource.resolver.Input;
import com.thaiopensource.resolver.Resolver;
import com.thaiopensource.resolver.ResolverException;
import com.thaiopensource.resolver.xml.ExternalDTDSubsetIdentifier;
import com.thaiopensource.resolver.xml.ExternalEntityIdentifier;
import com.thaiopensource.resolver.xml.ExternalIdentifier;

/**
 *
 */
public class SAX
{
  private SAX ()
  {}

  private static final class EntityResolverWrapper extends AbstractResolver
  {
    private final EntityResolver entityResolver;
    private final EntityResolver2 entityResolver2;
    private final boolean promiscuous;

    private EntityResolverWrapper (final EntityResolver entityResolver, final boolean promiscuous)
    {
      this.entityResolver = entityResolver;
      if (entityResolver instanceof EntityResolver2)
        entityResolver2 = (EntityResolver2) entityResolver;
      else
        entityResolver2 = null;
      this.promiscuous = promiscuous;
    }

    @Override
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

    @Override
    public void resolve (final Identifier id, final Input input) throws IOException, ResolverException
    {
      if (input.isResolved ())
        return;
      String publicId;
      String entityName = null;
      if (id instanceof ExternalIdentifier)
      {
        publicId = ((ExternalIdentifier) id).getPublicId ();
        if (id instanceof ExternalEntityIdentifier)
          entityName = ((ExternalEntityIdentifier) id).getEntityName ();
        else
          if (id instanceof ExternalDTDSubsetIdentifier)
            entityName = "[dtd]";
      }
      else
      {
        if (!promiscuous)
          return;
        publicId = null;
      }
      try
      {
        InputSource inputSource;
        if (entityName != null && entityResolver2 != null)
          inputSource = entityResolver2.resolveEntity (entityName, publicId, id.getBase (), id.getUriReference ());
        else
          inputSource = entityResolver.resolveEntity (publicId, getSystemId (id));
        if (inputSource != null)
          setInput (input, inputSource);
      }
      catch (final SAXException e)
      {
        throw toResolverException (e);
      }
    }
  }

  public static Resolver createResolver (final EntityResolver entityResolver, final boolean promiscuous)
  {
    return new EntityResolverWrapper (entityResolver, promiscuous);
  }

  public static EntityResolver2 createEntityResolver (final Resolver resolver)
  {
    if (resolver == null)
      throw new NullPointerException ();
    return new EntityResolverImpl (resolver);
  }

  public static Input createInput (final InputSource inputSource)
  {
    final Input input = new Input ();
    setInput (input, inputSource);
    return input;
  }

  // public because needed by transform package
  public static void setInput (final Input input, final InputSource inputSource)
  {
    input.setByteStream (inputSource.getByteStream ());
    input.setCharacterStream (inputSource.getCharacterStream ());
    input.setUri (inputSource.getSystemId ());
    input.setEncoding (inputSource.getEncoding ());
  }

  public static Exception getWrappedException (final SAXException e)
  {
    // not purely a wrapper
    if (e.getMessage () != null)
      return null;
    return e.getException ();
  }

  public static ResolverException toResolverException (final SAXException e)
  {
    final Exception wrapped = getWrappedException (e);
    if (wrapped != null)
    {
      if (wrapped instanceof ResolverException)
        return (ResolverException) wrapped;
      return new ResolverException (wrapped);
    }
    return new ResolverException (e);
  }

  public static SAXException toSAXException (final ResolverException e)
  {
    final Throwable cause = e.getCause ();
    if (cause != null && cause instanceof SAXException)
      return (SAXException) cause;
    return new SAXException (e);
  }

  static InputSource createInputSource (final Input input)
  {
    final InputSource inputSource = new InputSource ();
    inputSource.setByteStream (input.getByteStream ());
    inputSource.setCharacterStream (input.getCharacterStream ());
    inputSource.setEncoding (input.getEncoding ());
    inputSource.setSystemId (input.getUri ());
    return inputSource;
  }

  static String getSystemId (final Identifier id)
  {
    try
    {
      return BasicResolver.resolveUri (id);
    }
    catch (final ResolverException e)
    {}
    return id.getUriReference ();
  }

  // precondition: input.isResolved()
  static InputSource createInputSource (final Identifier id, final Input input)
  {
    final InputSource inputSource = createInputSource (input);
    if (id instanceof ExternalIdentifier)
      inputSource.setPublicId (((ExternalIdentifier) id).getPublicId ());
    if (inputSource.getSystemId () == null)
      inputSource.setSystemId (getSystemId (id));
    return inputSource;
  }

  static private class EntityResolverImpl implements EntityResolver2
  {
    private final Resolver resolver;

    private EntityResolverImpl (final Resolver resolver)
    {
      this.resolver = resolver;
    }

    public InputSource resolveEntity (final String publicId, final String systemId) throws SAXException, IOException
    {
      if (systemId == null)
        return null;
      final ExternalIdentifier id = new ExternalIdentifier (systemId, null, publicId);
      final Input input = new Input ();
      try
      {
        resolver.resolve (id, input);
      }
      catch (final ResolverException e)
      {
        throw toSAXException (e);
      }
      if (input.isResolved ())
        return createInputSource (id, input);
      return null;
    }

    public InputSource resolveEntity (final String name, final String publicId, final String base, final String systemId) throws SAXException,
                                                                                                                         IOException
    {
      if (systemId == null)
        return null;
      ExternalIdentifier id;
      if ("[doc]".equals (name))
        id = new ExternalDTDSubsetIdentifier (systemId, base, publicId, null);
      else
        if (name == null || name.indexOf ('[') >= 0 || name.indexOf ('#') >= 0)
          id = new ExternalIdentifier (systemId, base, publicId);
        else
          id = new ExternalEntityIdentifier (systemId, base, publicId, name);
      final Input input = new Input ();
      try
      {
        resolver.resolve (id, input);
      }
      catch (final ResolverException e)
      {
        throw toSAXException (e);
      }
      if (input.isResolved ())
        return createInputSource (id, input);
      return null;
    }

    public InputSource getExternalSubset (final String name, final String base) throws SAXException, IOException
    {
      return null;
    }
  }
}
