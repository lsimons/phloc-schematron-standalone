package com.thaiopensource.validate.rng.impl;

import java.io.IOException;

import org.relaxng.datatype.DatatypeLibraryFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.thaiopensource.datatype.DatatypeLibraryLoader;
import com.thaiopensource.relaxng.parse.BuildException;
import com.thaiopensource.relaxng.parse.IllegalSchemaException;
import com.thaiopensource.relaxng.parse.ParseReceiver;
import com.thaiopensource.relaxng.pattern.PatternFuture;
import com.thaiopensource.relaxng.pattern.SchemaBuilderImpl;
import com.thaiopensource.relaxng.pattern.SchemaPatternBuilder;
import com.thaiopensource.util.PropertyMap;
import com.thaiopensource.validate.IncorrectSchemaException;
import com.thaiopensource.validate.Schema;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.validate.auto.SchemaFuture;
import com.thaiopensource.validate.auto.SchemaReceiver;
import com.thaiopensource.validate.prop.rng.RngProperty;
import com.thaiopensource.validate.prop.wrap.WrapProperty;

public class SchemaReceiverImpl implements SchemaReceiver
{
  private final ParseReceiver parser;
  private final PropertyMap properties;

  public SchemaReceiverImpl (final ParseReceiver parser, final PropertyMap properties)
  {
    this.parser = parser;
    this.properties = properties;
  }

  public SchemaFuture installHandlers (final XMLReader xr) throws SAXException
  {
    final SchemaPatternBuilder pb = new SchemaPatternBuilder ();
    final ErrorHandler eh = properties.get (ValidateProperty.ERROR_HANDLER);
    DatatypeLibraryFactory dlf = properties.get (RngProperty.DATATYPE_LIBRARY_FACTORY);
    if (dlf == null)
      dlf = new DatatypeLibraryLoader ();
    final PatternFuture pf = SchemaBuilderImpl.installHandlers (parser, xr, eh, dlf, pb);
    return new SchemaFuture ()
    {
      public Schema getSchema () throws IncorrectSchemaException, SAXException, IOException
      {
        try
        {
          return SchemaReaderImpl.wrapPattern (pf.getPattern (properties.contains (WrapProperty.ATTRIBUTE_OWNER)),
                                               pb,
                                               properties);
        }
        catch (final IllegalSchemaException e)
        {
          throw new IncorrectSchemaException ();
        }
      }

      public RuntimeException unwrapException (final RuntimeException e) throws SAXException,
                                                                        IOException,
                                                                        IncorrectSchemaException
      {
        if (e instanceof BuildException)
        {
          try
          {
            return SchemaBuilderImpl.unwrapBuildException ((BuildException) e);
          }
          catch (final IllegalSchemaException ise)
          {
            throw new IncorrectSchemaException ();
          }
        }
        return e;
      }
    };
  }
}
