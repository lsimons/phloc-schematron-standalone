package com.thaiopensource.relaxng.parse.sax;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.thaiopensource.relaxng.parse.Annotations;
import com.thaiopensource.relaxng.parse.CommentList;
import com.thaiopensource.relaxng.parse.ParseReceiver;
import com.thaiopensource.relaxng.parse.ParsedPatternFuture;
import com.thaiopensource.relaxng.parse.SchemaBuilder;
import com.thaiopensource.relaxng.parse.Scope;
import com.thaiopensource.resolver.xml.sax.SAXResolver;

public class SAXParseReceiver <P, NC, L, EA, CL extends CommentList <L>, A extends Annotations <L, EA, CL>> extends
                                                                                                            SAXSubParser <P, NC, L, EA, CL, A> implements
                                                                                                                                              ParseReceiver <P, NC, L, EA, CL, A>
{
  public SAXParseReceiver (final SAXResolver resolver, final ErrorHandler eh)
  {
    super (resolver, eh);
  }

  public ParsedPatternFuture <P> installHandlers (final XMLReader xr,
                                                  final SchemaBuilder <P, NC, L, EA, CL, A> schemaBuilder,
                                                  final Scope <P, L, EA, CL, A> scope) throws SAXException
  {
    return new SchemaParser <P, NC, L, EA, CL, A> (xr, eh, schemaBuilder, null, scope);
  }
}
