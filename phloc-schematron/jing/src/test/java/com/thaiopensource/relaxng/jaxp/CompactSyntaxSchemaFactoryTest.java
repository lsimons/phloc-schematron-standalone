package com.thaiopensource.relaxng.jaxp;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests CompactSyntaxSchemaFactory.
 */
public class CompactSyntaxSchemaFactoryTest extends SchemaFactoryImplTest
{
  public CompactSyntaxSchemaFactoryTest ()
  {
    super (CompactSyntaxSchemaFactory.class);
  }

  @Test
  public void testIsSchemaLanguageSupported ()
  {
    Assert.assertFalse (factory ().isSchemaLanguageSupported (XMLSyntaxSchemaFactory.SCHEMA_LANGUAGE));
    Assert.assertTrue (factory ().isSchemaLanguageSupported (CompactSyntaxSchemaFactory.SCHEMA_LANGUAGE));
  }

  @Override
  protected String element (final String name, final String [] contentPatterns)
  {
    final StringBuilder builder = new StringBuilder ();
    builder.append ("element ").append (name).append (" {");
    for (int i = 0; i < contentPatterns.length; i++)
    {
      if (i > 0)
        builder.append (", ");
      builder.append (contentPatterns[i]);
    }
    if (contentPatterns.length == 0)
      builder.append ("empty");
    builder.append ("}");
    return builder.toString ();
  }

  @Override
  protected String attribute (final String name)
  {
    return "attribute " + name + " { text }";
  }

  @Override
  protected String externalRef (final String uri)
  {
    return "external \"" + uri + "\"";
  }

  @Override
  protected String getLSType ()
  {
    return CompactSyntaxSchemaFactory.SCHEMA_LANGUAGE;
  }
}
