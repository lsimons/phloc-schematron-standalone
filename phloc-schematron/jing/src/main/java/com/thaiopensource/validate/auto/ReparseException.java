package com.thaiopensource.validate.auto;

import java.io.IOException;

import javax.xml.transform.sax.SAXSource;

import org.xml.sax.SAXException;

import com.thaiopensource.validate.IncorrectSchemaException;
import com.thaiopensource.validate.Schema;

public abstract class ReparseException extends SAXException
{
  public ReparseException ()
  {
    super ((Exception) null);
  }

  public abstract Schema reparse (SAXSource source) throws IncorrectSchemaException, SAXException, IOException;
}
