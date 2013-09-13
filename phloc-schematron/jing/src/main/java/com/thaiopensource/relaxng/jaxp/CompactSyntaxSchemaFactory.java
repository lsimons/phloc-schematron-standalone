package com.thaiopensource.relaxng.jaxp;

import javax.xml.transform.sax.SAXSource;

import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;

import com.thaiopensource.relaxng.parse.Parseable;
import com.thaiopensource.relaxng.parse.compact.CompactParseable;
import com.thaiopensource.relaxng.pattern.AnnotationsImpl;
import com.thaiopensource.relaxng.pattern.CommentListImpl;
import com.thaiopensource.relaxng.pattern.NameClass;
import com.thaiopensource.relaxng.pattern.Pattern;
import com.thaiopensource.resolver.xml.sax.SAX;
import com.thaiopensource.resolver.xml.sax.SAXResolver;
import com.thaiopensource.util.VoidValue;
import com.thaiopensource.validation.Constants;

/**
 * A SchemaFactory that supports RELAX NG Compact Syntax.
 */
public class CompactSyntaxSchemaFactory extends SchemaFactoryImpl
{
  /**
   * The String that is used to identify the schema language, when the schema
   * language is RELAX NG Compact Syntax. Usually XML schema languages use an
   * XML syntax, and the schema language can be identified by the namespace URI,
   * but this is not the case with RELAX NG Compact Syntax. RELAX NG Compact
   * syntax has a registered MIME type, which is
   * application/relax-ng-compact-syntax, so we use the URI corresponding to
   * that.
   * 
   * @see javax.xml.validation.SchemaFactory#isSchemaLanguageSupported
   * @see javax.xml.validation.SchemaFactory#newInstance(String)
   */
  static final public String SCHEMA_LANGUAGE = Constants.RELAXNG_COMPACT_URI;

  @Override
  public boolean isSchemaLanguageSupported (final String schemaLanguage)
  {
    return schemaLanguage.equals (SCHEMA_LANGUAGE);
  }

  @Override
  protected Parseable <Pattern, NameClass, Locator, VoidValue, CommentListImpl, AnnotationsImpl> createParseable (final SAXSource source,
                                                                                                                  final SAXResolver saxResolver,
                                                                                                                  final ErrorHandler eh)
  {
    return new CompactParseable <Pattern, NameClass, Locator, VoidValue, CommentListImpl, AnnotationsImpl> (SAX.createInput (source.getInputSource ()),
                                                                                                            saxResolver.getResolver (),
                                                                                                            eh);
  }
}
