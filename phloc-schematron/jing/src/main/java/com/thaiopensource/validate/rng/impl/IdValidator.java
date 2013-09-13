package com.thaiopensource.validate.rng.impl;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ErrorHandler;

import com.thaiopensource.relaxng.pattern.IdTypeMap;
import com.thaiopensource.relaxng.sax.IdContentHandler;
import com.thaiopensource.validate.Validator;

public class IdValidator extends IdContentHandler implements Validator
{
  public IdValidator (final IdTypeMap idTypeMap, final ErrorHandler eh)
  {
    super (idTypeMap, eh);
  }

  public ContentHandler getContentHandler ()
  {
    return this;
  }

  public DTDHandler getDTDHandler ()
  {
    return null;
  }

  @Override
  public void reset ()
  {
    super.reset ();
  }
}
