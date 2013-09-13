package com.thaiopensource.validate.jarv;

import java.io.IOException;

import javax.xml.transform.sax.SAXSource;

import org.iso_relax.verifier.VerifierConfigurationException;
import org.iso_relax.verifier.VerifierFactory;
import org.xml.sax.SAXException;

import com.thaiopensource.util.PropertyMap;
import com.thaiopensource.validate.AbstractSchema;
import com.thaiopensource.validate.AbstractSchemaReader;
import com.thaiopensource.validate.IncorrectSchemaException;
import com.thaiopensource.validate.Option;
import com.thaiopensource.validate.Schema;
import com.thaiopensource.validate.Validator;

public class VerifierFactorySchemaReader extends AbstractSchemaReader
{
  private final VerifierFactory vf;

  static private class SchemaImpl extends AbstractSchema
  {
    final org.iso_relax.verifier.Schema schema;

    private SchemaImpl (final org.iso_relax.verifier.Schema schema)
    {
      this.schema = schema;
    }

    public Validator createValidator (final PropertyMap properties)
    {
      try
      {
        return new VerifierValidator (schema.newVerifier (), properties);
      }
      catch (final VerifierConfigurationException e)
      {
        final Exception cause = e.getCauseException ();
        if (cause instanceof RuntimeException &&
            (e.getMessage () == null || e.getMessage ().equals (cause.getMessage ())))
          throw (RuntimeException) cause;
        throw new JarvConfigurationException (e);
      }
    }
  }

  public VerifierFactorySchemaReader (final VerifierFactory vf)
  {
    this.vf = vf;
  }

  public Schema createSchema (final SAXSource source, final PropertyMap properties) throws IOException,
                                                                                   SAXException,
                                                                                   IncorrectSchemaException
  {
    try
    {
      return new SchemaImpl (vf.compileSchema (source.getInputSource ()));
    }
    catch (final SAXException e)
    {
      System.err.println ("compileSchema threw a SAXException class " + e.getClass ().toString ());
      if (e.getException () != null)
        System.err.println ("cause has class " + e.getException ().getClass ().toString ());
      throw e;
    }
    catch (VerifierConfigurationException e)
    {
      for (;;)
      {
        final Exception cause = e.getCauseException ();
        String message = e.getMessage ();
        if (cause != null && message != null && message.equals (cause.getMessage ()))
          message = null; // don't really have a message
        if (message == null)
        {
          if (cause instanceof RuntimeException)
            throw (RuntimeException) cause;
          if (cause instanceof SAXException)
            throw (SAXException) cause;
          if (cause instanceof IOException)
            throw (IOException) cause;
          if (cause instanceof VerifierConfigurationException)
          {
            e = (VerifierConfigurationException) cause;
            continue;
          }
        }
        throw new SAXException (message, cause);
      }
    }

  }

  public Option getOption (final String uri)
  {
    return null;
  }
}
