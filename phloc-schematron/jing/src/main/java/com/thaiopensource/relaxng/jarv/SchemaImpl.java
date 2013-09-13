package com.thaiopensource.relaxng.jarv;

import org.iso_relax.verifier.Schema;
import org.iso_relax.verifier.Verifier;
import org.iso_relax.verifier.VerifierConfigurationException;

import com.thaiopensource.relaxng.pattern.Pattern;
import com.thaiopensource.relaxng.pattern.SchemaPatternBuilder;
import com.thaiopensource.relaxng.pattern.ValidatorPatternBuilder;

class SchemaImpl implements Schema
{
  private final SchemaPatternBuilder spb;
  private final Pattern start;

  SchemaImpl (final Pattern start, final SchemaPatternBuilder spb)
  {
    this.start = start;
    this.spb = spb;
  }

  public Verifier newVerifier () throws VerifierConfigurationException
  {
    return new VerifierImpl (start, new ValidatorPatternBuilder (spb));
  }
}
