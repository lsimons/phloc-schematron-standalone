package com.thaiopensource.validate.picl;

import java.util.Stack;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import com.thaiopensource.util.Localizer;
import com.thaiopensource.util.PropertyMap;
import com.thaiopensource.util.SinglePropertyMap;
import com.thaiopensource.validate.IncorrectSchemaException;
import com.thaiopensource.validate.Schema;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.validate.Validator;
import com.thaiopensource.validate.auto.SchemaFuture;
import com.thaiopensource.xml.sax.CountingErrorHandler;
import com.thaiopensource.xml.sax.DelegatingContentHandler;
import com.thaiopensource.xml.util.WellKnownNamespaces;

class SchemaParser extends DelegatingContentHandler implements SchemaFuture, NamespaceContext
{
  private final Vector <KeyConstraint> constraints = new Vector <KeyConstraint> ();
  private final PropertyMap properties;
  private final CountingErrorHandler ceh;
  private Locator locator;
  private final Stack <String> prefixes = new Stack <String> ();
  private final Localizer localizer = new Localizer (SchemaParser.class);
  private final PatternParser patternParser;

  SchemaParser (final PropertyMap properties, final Schema piclSchema)
  {
    this.properties = properties;
    ceh = new CountingErrorHandler (properties.get (ValidateProperty.ERROR_HANDLER));
    final Validator validator = piclSchema.createValidator (SinglePropertyMap.newInstance (ValidateProperty.ERROR_HANDLER,
                                                                                           ceh));
    setDelegate (validator.getContentHandler ());
    patternParser = new PatternParser (ceh, localizer);
  }

  @Override
  public void setDocumentLocator (final Locator locator)
  {
    super.setDocumentLocator (locator);
    this.locator = locator;
  }

  @Override
  public void startDocument () throws SAXException
  {
    super.startDocument ();
    prefixes.push ("xml");
    prefixes.push (WellKnownNamespaces.XML);
  }

  @Override
  public void startPrefixMapping (String prefix, String uri) throws SAXException
  {
    if (prefix == null)
      prefix = "";
    prefixes.push (prefix);
    if (uri != null && uri.length () == 0)
      uri = null;
    prefixes.push (uri);
    super.startPrefixMapping (prefix, uri);
  }

  @Override
  public void endPrefixMapping (final String prefix) throws SAXException
  {
    prefixes.pop ();
    prefixes.pop ();
    super.endPrefixMapping (prefix);
  }

  @Override
  public void startElement (final String namespaceURI, final String localName, final String qName, final Attributes atts) throws SAXException
  {
    super.startElement (namespaceURI, localName, qName, atts);
    if (ceh.getHadErrorOrFatalError ())
      return;
    if (!localName.equals ("constraint"))
      return;
    final String key = atts.getValue ("", "key");
    try
    {
      final Pattern keyPattern = patternParser.parse (key, locator, this);
      final String ref = atts.getValue ("", "ref");
      if (ref != null)
      {
        final Pattern refPattern = patternParser.parse (ref, locator, this);
        constraints.addElement (new KeyRefConstraint (keyPattern, refPattern));
      }
      else
        constraints.addElement (new KeyConstraint (keyPattern));
    }
    catch (final InvalidPatternException e)
    {}
  }

  public Schema getSchema () throws IncorrectSchemaException
  {
    if (ceh.getHadErrorOrFatalError ())
      throw new IncorrectSchemaException ();
    Constraint constraint;
    if (constraints.size () == 1)
      constraint = constraints.elementAt (0);
    else
    {
      final Constraint [] v = new Constraint [constraints.size ()];
      for (int i = 0; i < v.length; i++)
        v[i] = constraints.elementAt (i);
      constraint = new MultiConstraint (v);
    }
    return new SchemaImpl (properties, constraint);
  }

  public RuntimeException unwrapException (final RuntimeException e)
  {
    return e;
  }

  public String getNamespaceUri (final String prefix)
  {
    for (int i = prefixes.size (); i > 0; i -= 2)
    {
      if (prefixes.elementAt (i - 2).equals (prefix))
        return prefixes.elementAt (i - 1);
    }
    return null;
  }

  public String defaultPrefix ()
  {
    return "";
  }
}
