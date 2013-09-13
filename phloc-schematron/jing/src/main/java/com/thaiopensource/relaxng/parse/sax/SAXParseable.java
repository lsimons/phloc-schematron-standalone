package com.thaiopensource.relaxng.parse.sax;

import java.io.IOException;

import javax.xml.transform.sax.SAXSource;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.thaiopensource.relaxng.parse.Annotations;
import com.thaiopensource.relaxng.parse.BuildException;
import com.thaiopensource.relaxng.parse.CommentList;
import com.thaiopensource.relaxng.parse.IllegalSchemaException;
import com.thaiopensource.relaxng.parse.IncludedGrammar;
import com.thaiopensource.relaxng.parse.SchemaBuilder;
import com.thaiopensource.relaxng.parse.Scope;
import com.thaiopensource.relaxng.parse.SubParseable;
import com.thaiopensource.resolver.xml.sax.SAXResolver;
import com.thaiopensource.util.Uri;

public class SAXParseable <P, NC, L, EA, CL extends CommentList <L>, A extends Annotations <L, EA, CL>> extends
                                                                                                        SAXSubParser <P, NC, L, EA, CL, A> implements
                                                                                                                                          SubParseable <P, NC, L, EA, CL, A>
{
  private final SAXSource source;

  /**
   * @param source
   *        XMLReader must be non-null
   * @param resolver
   * @param eh
   */
  public SAXParseable (final SAXSource source, final SAXResolver resolver, final ErrorHandler eh)
  {
    super (resolver, eh);
    this.source = source;
  }

  public P parse (final SchemaBuilder <P, NC, L, EA, CL, A> schemaBuilder, final Scope <P, L, EA, CL, A> scope) throws BuildException,
                                                                                                               IllegalSchemaException
  {
    try
    {
      final XMLReader xr = source.getXMLReader ();
      final SchemaParser <P, NC, L, EA, CL, A> sp = new SchemaParser <P, NC, L, EA, CL, A> (xr,
                                                                                            eh,
                                                                                            schemaBuilder,
                                                                                            null,
                                                                                            scope);
      xr.parse (source.getInputSource ());
      return sp.getParsedPattern ();
    }
    catch (final SAXException e)
    {
      throw BuildException.fromSAXException (e);
    }
    catch (final IOException e)
    {
      throw new BuildException (e);
    }
  }

  public P parseAsInclude (final SchemaBuilder <P, NC, L, EA, CL, A> schemaBuilder,
                           final IncludedGrammar <P, L, EA, CL, A> g) throws BuildException, IllegalSchemaException
  {
    try
    {
      final XMLReader xr = source.getXMLReader ();
      final SchemaParser <P, NC, L, EA, CL, A> sp = new SchemaParser <P, NC, L, EA, CL, A> (xr, eh, schemaBuilder, g, g);
      xr.parse (source.getInputSource ());
      return sp.getParsedPattern ();
    }
    catch (final SAXException e)
    {
      throw BuildException.fromSAXException (e);
    }
    catch (final IOException e)
    {
      throw new BuildException (e);
    }
  }

  public String getUri ()
  {
    final String uri = source.getInputSource ().getSystemId ();
    if (uri == null)
      return null;
    return Uri.escapeDisallowedChars (uri);
  }
}
