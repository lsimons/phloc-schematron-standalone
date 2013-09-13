package com.thaiopensource.relaxng.jarv;

import java.io.IOException;

import javax.xml.transform.sax.SAXSource;

import org.iso_relax.verifier.Schema;
import org.iso_relax.verifier.VerifierFactory;
import org.relaxng.datatype.DatatypeLibraryFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import com.thaiopensource.datatype.DatatypeLibraryLoader;
import com.thaiopensource.relaxng.parse.IllegalSchemaException;
import com.thaiopensource.relaxng.parse.Parseable;
import com.thaiopensource.relaxng.parse.sax.SAXParseable;
import com.thaiopensource.relaxng.pattern.AnnotationsImpl;
import com.thaiopensource.relaxng.pattern.CommentListImpl;
import com.thaiopensource.relaxng.pattern.NameClass;
import com.thaiopensource.relaxng.pattern.Pattern;
import com.thaiopensource.relaxng.pattern.SchemaBuilderImpl;
import com.thaiopensource.relaxng.pattern.SchemaPatternBuilder;
import com.thaiopensource.resolver.Resolver;
import com.thaiopensource.resolver.xml.sax.SAX;
import com.thaiopensource.resolver.xml.sax.SAXResolver;
import com.thaiopensource.util.VoidValue;
import com.thaiopensource.xml.sax.DraconianErrorHandler;

public class VerifierFactoryImpl extends VerifierFactory
{
  private final DatatypeLibraryFactory dlf = new DatatypeLibraryLoader ();
  private final ErrorHandler eh = new DraconianErrorHandler ();

  public VerifierFactoryImpl ()
  {}

  @Override
  public Schema compileSchema (final InputSource inputSource) throws SAXException, IOException
  {
    final SchemaPatternBuilder spb = new SchemaPatternBuilder ();
    Resolver resolver = null;
    final EntityResolver entityResolver = getEntityResolver ();
    if (entityResolver != null)
      resolver = SAX.createResolver (entityResolver, true);
    final SAXResolver saxResolver = new SAXResolver (resolver);
    final Parseable <Pattern, NameClass, Locator, VoidValue, CommentListImpl, AnnotationsImpl> parseable = new SAXParseable <Pattern, NameClass, Locator, VoidValue, CommentListImpl, AnnotationsImpl> (new SAXSource (saxResolver.createXMLReader (),
                                                                                                                                                                                                                       inputSource),
                                                                                                                                                                                                        saxResolver,
                                                                                                                                                                                                        eh);
    try
    {
      return new SchemaImpl (SchemaBuilderImpl.parse (parseable, eh, dlf, spb, false), spb);
    }
    catch (final IllegalSchemaException e)
    {
      throw new SAXException ("unreported schema error");
    }
  }
}
