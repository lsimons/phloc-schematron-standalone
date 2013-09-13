package com.thaiopensource.relaxng.parse.compact;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;

import org.xml.sax.ErrorHandler;

import com.thaiopensource.relaxng.parse.Annotations;
import com.thaiopensource.relaxng.parse.BuildException;
import com.thaiopensource.relaxng.parse.CommentList;
import com.thaiopensource.relaxng.parse.IllegalSchemaException;
import com.thaiopensource.relaxng.parse.IncludedGrammar;
import com.thaiopensource.relaxng.parse.SchemaBuilder;
import com.thaiopensource.relaxng.parse.Scope;
import com.thaiopensource.relaxng.parse.SubParseable;
import com.thaiopensource.resolver.Identifier;
import com.thaiopensource.resolver.Input;
import com.thaiopensource.resolver.MediaTypedIdentifier;
import com.thaiopensource.resolver.Resolver;
import com.thaiopensource.resolver.ResolverException;
import com.thaiopensource.util.Uri;
import com.thaiopensource.xml.util.EncodingMap;

public class CompactParseable <P, NC, L, EA, CL extends CommentList <L>, A extends Annotations <L, EA, CL>> implements
                                                                                                            SubParseable <P, NC, L, EA, CL, A>
{
  private final Input in;
  private final Resolver resolver;
  private final ErrorHandler eh;
  private static final String MEDIA_TYPE = "application/relax-ng-compact-syntax";

  public CompactParseable (final Input in, final Resolver resolver, final ErrorHandler eh)
  {
    this.in = in;
    this.resolver = resolver;
    this.eh = eh;
  }

  public P parse (final SchemaBuilder <P, NC, L, EA, CL, A> sb, final Scope <P, L, EA, CL, A> scope) throws BuildException,
                                                                                                    IllegalSchemaException
  {
    return new CompactSyntax <P, NC, L, EA, CL, A> (makeReader (in), in.getUri (), sb, eh).parse (scope);
  }

  public SubParseable <P, NC, L, EA, CL, A> createSubParseable (final String href, final String base) throws BuildException
  {
    final Identifier id = new MediaTypedIdentifier (href, base, MEDIA_TYPE);
    final Input input = new Input ();
    try
    {
      resolver.resolve (id, input);
    }
    catch (final ResolverException e)
    {
      throw BuildException.fromResolverException (e);
    }
    catch (final IOException e)
    {
      throw new BuildException (e);
    }
    return new CompactParseable <P, NC, L, EA, CL, A> (input, resolver, eh);
  }

  public P parseAsInclude (final SchemaBuilder <P, NC, L, EA, CL, A> sb, final IncludedGrammar <P, L, EA, CL, A> g) throws BuildException,
                                                                                                                   IllegalSchemaException
  {
    return new CompactSyntax <P, NC, L, EA, CL, A> (makeReader (in), in.getUri (), sb, eh).parseInclude (g);
  }

  public String getUri ()
  {
    final String uri = in.getUri ();
    if (uri == null)
      return null;
    return Uri.escapeDisallowedChars (uri);
  }

  private static final String UTF8 = EncodingMap.getJavaName ("UTF-8");
  private static final String UTF16 = EncodingMap.getJavaName ("UTF-16");

  private Reader makeReader (final Input in) throws BuildException
  {
    try
    {
      resolver.open (in);
      Reader reader = in.getCharacterStream ();
      if (reader == null)
      {
        InputStream byteStream = in.getByteStream ();
        if (byteStream == null)
          throw new IllegalArgumentException ("invalid input for CompactParseable");
        String encoding = in.getEncoding ();
        if (encoding == null)
        {
          final PushbackInputStream pb = new PushbackInputStream (byteStream, 2);
          encoding = detectEncoding (pb);
          byteStream = pb;
        }
        reader = new InputStreamReader (byteStream, encoding);
      }
      return reader;
    }
    catch (final ResolverException e)
    {
      throw BuildException.fromResolverException (e);
    }
    catch (final IOException e)
    {
      throw new BuildException (e);
    }
  }

  static private String detectEncoding (final PushbackInputStream in) throws IOException
  {
    String encoding = UTF8;
    final int b1 = in.read ();
    if (b1 != -1)
    {
      final int b2 = in.read ();
      if (b2 != -1)
      {
        in.unread (b2);
        if ((b1 == 0xFF && b2 == 0xFE) || (b1 == 0xFE && b2 == 0xFF))
          encoding = UTF16;
      }
      in.unread (b1);
    }
    return encoding;
  }
}
