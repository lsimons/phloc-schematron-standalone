package com.thaiopensource.validate.jarv;

import org.iso_relax.verifier.VerifierConfigurationException;
import org.iso_relax.verifier.VerifierFactory;

import com.thaiopensource.validate.Option;
import com.thaiopensource.validate.SchemaReader;
import com.thaiopensource.validate.SchemaReaderFactory;

public class JarvSchemaReaderFactory implements SchemaReaderFactory
{
  public SchemaReader createSchemaReader (final String namespaceUri)
  {
    try
    {
      final VerifierFactory vf = VerifierFactory.newInstance (namespaceUri);
      if (vf != null)
        return new VerifierFactorySchemaReader (vf);
    }
    catch (final VerifierConfigurationException e)
    {}
    return null;
  }

  public Option getOption (final String uri)
  {
    return null;
  }
}
