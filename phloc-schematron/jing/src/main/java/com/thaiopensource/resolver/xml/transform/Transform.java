package com.thaiopensource.resolver.xml.transform;

import java.io.IOException;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.thaiopensource.resolver.AbstractResolver;
import com.thaiopensource.resolver.Identifier;
import com.thaiopensource.resolver.Input;
import com.thaiopensource.resolver.Resolver;
import com.thaiopensource.resolver.ResolverException;
import com.thaiopensource.resolver.xml.sax.SAX;
import com.thaiopensource.resolver.xml.sax.SAXInput;
import com.thaiopensource.resolver.xml.sax.SAXResolver;

/**
 *
 */
public class Transform
{
  private Transform ()
  {}

  /**
   * Creates a URIResolver that returns a SAXSource.
   * 
   * @param resolver
   * @return
   */
  public static URIResolver createSAXURIResolver (final Resolver resolver)
  {
    final SAXResolver saxResolver = new SAXResolver (resolver);
    return new URIResolver ()
    {
      public Source resolve (final String href, final String base) throws TransformerException
      {
        try
        {
          return saxResolver.resolve (href, base);
        }
        catch (final SAXException e)
        {
          throw toTransformerException (e);
        }
        catch (final IOException e)
        {
          throw new TransformerException (e);
        }
      }
    };
  }

  public static Resolver createResolver (final URIResolver uriResolver)
  {
    return new AbstractResolver ()
    {
      @Override
      public void resolve (final Identifier id, final Input input) throws IOException, ResolverException
      {
        if (input.isResolved ())
          return;
        Source source;
        try
        {
          source = uriResolver.resolve (id.getUriReference (), id.getBase ());
        }
        catch (final TransformerException e)
        {
          throw toResolverException (e);
        }
        if (source == null)
          return;
        if (source instanceof SAXSource)
        {
          setInput (input, (SAXSource) source);
          return;
        }
        final InputSource in = SAXSource.sourceToInputSource (source);
        if (in != null)
        {
          SAX.setInput (input, in);
          return;
        }
        // XXX handle StAXSource
        throw new ResolverException ("URIResolver returned unsupported subclass of Source");
      }
    };
  }

  private static void setInput (final Input input, final SAXSource source)
  {
    final XMLReader reader = source.getXMLReader ();
    if (reader != null)
    {
      if (input instanceof SAXInput)
        ((SAXInput) input).setXMLReader (reader);
    }
    final InputSource in = source.getInputSource ();
    if (in != null)
      SAX.setInput (input, in);
  }

  private static TransformerException toTransformerException (final SAXException e)
  {
    final Exception wrapped = SAX.getWrappedException (e);
    if (wrapped != null)
    {
      if (wrapped instanceof TransformerException)
        return (TransformerException) wrapped;
      return new TransformerException (wrapped);
    }
    return new TransformerException (e);
  }

  private static ResolverException toResolverException (final TransformerException e)
  {
    final Throwable wrapped = getWrappedException (e);
    if (wrapped != null)
    {
      if (wrapped instanceof ResolverException)
        return (ResolverException) wrapped;
      return new ResolverException (wrapped);
    }
    return new ResolverException (e);
  }

  private static Throwable getWrappedException (final TransformerException e)
  {
    final Throwable wrapped = e.getException ();
    if (wrapped == null)
      return null;
    final String message = e.getMessage ();
    if (message != null && !message.equals (wrapped.getMessage ()))
      return null;
    return wrapped;
  }
}
