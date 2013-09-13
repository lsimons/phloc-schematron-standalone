package com.thaiopensource.relaxng.jaxp;

import com.thaiopensource.relaxng.pattern.Pattern;
import com.thaiopensource.relaxng.pattern.SchemaPatternBuilder;
import com.thaiopensource.relaxng.pattern.ValidatorPatternBuilder;
import com.thaiopensource.validation.Schema2;
import com.thaiopensource.validation.ValidatorHandler2;

class SchemaImpl extends Schema2
{
  private final SchemaFactoryImpl factory;
  private final SchemaPatternBuilder spb;
  private final Pattern start;

  SchemaImpl (final SchemaFactoryImpl factory, final SchemaPatternBuilder spb, final Pattern start)
  {
    this.factory = factory;
    this.spb = spb;
    this.start = start;
  }

  @Override
  public ValidatorHandler2 newValidatorHandler ()
  {
    return new ValidatorHandlerImpl (factory, start, new ValidatorPatternBuilder (spb));
  }
}
