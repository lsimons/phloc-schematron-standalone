package com.thaiopensource.validate.rng.impl;

import org.xml.sax.ErrorHandler;

import com.thaiopensource.relaxng.pattern.Pattern;
import com.thaiopensource.relaxng.pattern.SchemaPatternBuilder;
import com.thaiopensource.relaxng.pattern.ValidatorPatternBuilder;
import com.thaiopensource.util.PropertyMap;
import com.thaiopensource.validate.AbstractSchema;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.validate.Validator;

public class PatternSchema extends AbstractSchema
{
  private final SchemaPatternBuilder spb;
  private final Pattern start;

  public PatternSchema (final SchemaPatternBuilder spb, final Pattern start, final PropertyMap properties)
  {
    super (properties);
    this.spb = spb;
    this.start = start;
  }

  public Validator createValidator (final PropertyMap properties)
  {
    final ErrorHandler eh = properties.get (ValidateProperty.ERROR_HANDLER);
    return new RngValidator (start, new ValidatorPatternBuilder (spb), eh);
  }
}
