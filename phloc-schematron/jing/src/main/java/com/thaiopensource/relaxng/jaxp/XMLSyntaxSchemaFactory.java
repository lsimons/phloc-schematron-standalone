package com.thaiopensource.relaxng.jaxp;

import javax.xml.transform.sax.SAXSource;

import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import com.thaiopensource.relaxng.parse.Parseable;
import com.thaiopensource.relaxng.parse.sax.SAXParseable;
import com.thaiopensource.relaxng.pattern.AnnotationsImpl;
import com.thaiopensource.relaxng.pattern.CommentListImpl;
import com.thaiopensource.relaxng.pattern.NameClass;
import com.thaiopensource.relaxng.pattern.Pattern;
import com.thaiopensource.resolver.xml.sax.SAXResolver;
import com.thaiopensource.util.VoidValue;
import com.thaiopensource.validation.Constants;

/**
 * A SchemaFactory that supports RELAX NG with the original XML syntax.
 */
public class XMLSyntaxSchemaFactory extends SchemaFactoryImpl
{

  /**
   * The String that is used to identify the schema language, when the schema
   * language is RELAX NG with the original XML syntax. The String is the
   * namespace URI for RELAX NG schemas.
   */
  static final public String SCHEMA_LANGUAGE = Constants.RELAXNG_XML_URI;

  @Override
  protected Parseable <Pattern, NameClass, Locator, VoidValue, CommentListImpl, AnnotationsImpl> createParseable (SAXSource source,
                                                                                                                  final SAXResolver resolver,
                                                                                                                  final ErrorHandler eh) throws SAXException
  {
    if (source.getXMLReader () == null)
      source = new SAXSource (resolver.createXMLReader (), source.getInputSource ());
    return new SAXParseable <Pattern, NameClass, Locator, VoidValue, CommentListImpl, AnnotationsImpl> (source,
                                                                                                        resolver,
                                                                                                        eh);
  }

  @Override
  public boolean isSchemaLanguageSupported (final String schemaLanguage)
  {
    return schemaLanguage.equals (SCHEMA_LANGUAGE);
  }

}
