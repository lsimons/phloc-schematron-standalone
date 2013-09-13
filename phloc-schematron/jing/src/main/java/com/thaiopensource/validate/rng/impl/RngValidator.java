package com.thaiopensource.validate.rng.impl;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ErrorHandler;

import com.thaiopensource.relaxng.pattern.Pattern;
import com.thaiopensource.relaxng.pattern.ValidatorPatternBuilder;
import com.thaiopensource.relaxng.sax.PatternValidator;
import com.thaiopensource.validate.Validator;

public class RngValidator extends PatternValidator implements Validator
{
  public RngValidator (final Pattern pattern, final ValidatorPatternBuilder builder, final ErrorHandler eh)
  {
    super (pattern, builder, eh);
  }

  public ContentHandler getContentHandler ()
  {
    return this;
  }

  public DTDHandler getDTDHandler ()
  {
    return this;
  }

  @Override
  public void reset ()
  {
    super.reset ();
  }
}
