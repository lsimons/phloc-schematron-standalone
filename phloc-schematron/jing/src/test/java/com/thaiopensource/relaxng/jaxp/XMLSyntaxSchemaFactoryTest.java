package com.thaiopensource.relaxng.jaxp;

import javax.xml.XMLConstants;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.thaiopensource.xml.util.WellKnownNamespaces;

/**
 *
 */
public class XMLSyntaxSchemaFactoryTest extends SchemaFactoryImplTest
{

  private static final String NS = XMLConstants.RELAXNG_NS_URI;

  public XMLSyntaxSchemaFactoryTest ()
  {
    super (XMLSyntaxSchemaFactory.class);
  }

  @Test
  public void testIsSchemaLanguageSupported ()
  {
    Assert.assertTrue (factory ().isSchemaLanguageSupported (NS));
    Assert.assertTrue (factory ().isSchemaLanguageSupported (XMLSyntaxSchemaFactory.SCHEMA_LANGUAGE));
    Assert.assertTrue (factory ().isSchemaLanguageSupported (WellKnownNamespaces.RELAX_NG));
    Assert.assertFalse (factory ().isSchemaLanguageSupported (CompactSyntaxSchemaFactory.SCHEMA_LANGUAGE));
  }

  @Override
  protected String element (final String name, final String [] contentPatterns)
  {
    final StringBuilder builder = new StringBuilder ();
    builder.append ("<element xmlns='" + NS + "' name='").append (name).append ("'>");
    for (final String contentPattern : contentPatterns)
      builder.append (contentPattern);
    if (contentPatterns.length == 0)
      builder.append ("<empty/>");
    builder.append ("</element>");
    return builder.toString ();
  }

  @Override
  protected String attribute (final String name)
  {
    return "<attribute name='" + name + "'/>";
  }

  @Override
  protected String externalRef (final String uri)
  {
    return "<externalRef xmlns='" + NS + "' href='" + uri + "'/>";
  }

  @Override
  protected String getLSType ()
  {
    return NS;
  }
}
