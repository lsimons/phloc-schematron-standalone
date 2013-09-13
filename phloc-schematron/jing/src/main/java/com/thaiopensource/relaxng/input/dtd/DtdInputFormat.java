package com.thaiopensource.relaxng.input.dtd;

import java.io.IOException;
import java.util.Map;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.thaiopensource.relaxng.edit.SchemaCollection;
import com.thaiopensource.relaxng.input.InputFormat;
import com.thaiopensource.relaxng.output.common.ErrorReporter;
import com.thaiopensource.relaxng.translate.util.AbsoluteUriParam;
import com.thaiopensource.relaxng.translate.util.AbstractParam;
import com.thaiopensource.relaxng.translate.util.InvalidParamValueException;
import com.thaiopensource.relaxng.translate.util.InvalidParamsException;
import com.thaiopensource.relaxng.translate.util.NCNameParam;
import com.thaiopensource.relaxng.translate.util.NmtokenParam;
import com.thaiopensource.relaxng.translate.util.Param;
import com.thaiopensource.relaxng.translate.util.ParamFactory;
import com.thaiopensource.relaxng.translate.util.ParamProcessor;
import com.thaiopensource.resolver.Resolver;
import com.thaiopensource.util.Localizer;
import com.thaiopensource.xml.dtd.om.Dtd;
import com.thaiopensource.xml.dtd.parse.DtdParserImpl;
import com.thaiopensource.xml.dtd.parse.ParseException;
import com.thaiopensource.xml.em.ResolverUriEntityManager;
import com.thaiopensource.xml.util.Naming;

public class DtdInputFormat implements InputFormat
{
  static private class NamespaceDeclParamFactory implements ParamFactory
  {
    private final Map <String, String> prefixMap;

    NamespaceDeclParamFactory (final Map <String, String> prefixMap)
    {
      this.prefixMap = prefixMap;
    }

    public Param createParam (final String name)
    {
      if (!name.startsWith ("xmlns:"))
        return null;
      final String prefix = name.substring (6);
      if (!Naming.isNcname (prefix))
        return null;
      return new AbsoluteUriParam ()
      {
        @Override
        public void setAbsoluteUri (final String uri)
        {
          prefixMap.put (prefix, uri);
        }
      };
    }
  }

  static private abstract class DeclPatternParam extends AbstractParam
  {
    private final Localizer localizer;

    DeclPatternParam (final Localizer localizer)
    {
      this.localizer = localizer;
    }

    @Override
    public void set (final String value) throws InvalidParamValueException
    {
      if (value.indexOf ('%') < 0)
        throw new InvalidParamValueException (localizer.message ("no_percent"));
      if (value.lastIndexOf ('%') != value.indexOf ('%'))
        throw new InvalidParamValueException (localizer.message ("multiple_percent"));
      if (!Naming.isNcname (value.replace ('%', 'x')))
        throw new InvalidParamValueException (localizer.message ("not_ncname_with_percent"));
      setDeclPattern (value);
    }

    abstract void setDeclPattern (String pattern);
  }

  public SchemaCollection load (final String uri,
                                final String [] params,
                                final String outputFormat,
                                final ErrorHandler eh,
                                final Resolver resolver) throws InvalidParamsException, IOException, SAXException
  {
    final ErrorReporter er = new ErrorReporter (eh, DtdInputFormat.class);
    final Converter.Options options = new Converter.Options ();
    if ("xsd".equals (outputFormat))
    {
      options.inlineAttlistDecls = true;
      options.generateStart = false;
    }
    final ParamProcessor pp = new ParamProcessor ();
    pp.declare ("inline-attlist", new AbstractParam ()
    {
      @Override
      public void set (final boolean value)
      {
        options.inlineAttlistDecls = value;
      }
    });
    pp.declare ("xmlns", new AbsoluteUriParam ()
    {
      @Override
      public void set (final String value) throws InvalidParamValueException
      {
        if (value.equals (""))
          setAbsoluteUri (value);
        else
          super.set (value);
      }

      @Override
      protected void setAbsoluteUri (final String value)
      {
        options.defaultNamespace = value;
      }
    });
    pp.declare ("any-name", new NCNameParam ()
    {
      @Override
      protected void setNCName (final String value)
      {
        options.anyName = value;
      }
    });
    pp.declare ("strict-any", new AbstractParam ()
    {
      @Override
      public void set (final boolean value)
      {
        options.strictAny = value;
      }
    });
    pp.declare ("annotation-prefix", new NCNameParam ()
    {
      @Override
      protected void setNCName (final String value)
      {
        options.annotationPrefix = value;
      }
    });
    pp.declare ("colon-replacement", new NmtokenParam ()
    {
      @Override
      protected void setNmtoken (final String value)
      {
        options.colonReplacement = value;
      }
    });
    pp.declare ("generate-start", new AbstractParam ()
    {
      @Override
      public void set (final boolean value)
      {
        options.generateStart = value;
      }
    });
    pp.declare ("element-define", new DeclPatternParam (er.getLocalizer ())
    {
      @Override
      void setDeclPattern (final String pattern)
      {
        options.elementDeclPattern = pattern;
      }
    });
    pp.declare ("attlist-define", new DeclPatternParam (er.getLocalizer ())
    {
      @Override
      void setDeclPattern (final String pattern)
      {
        options.attlistDeclPattern = pattern;
      }
    });
    pp.setParamFactory (new NamespaceDeclParamFactory (options.prefixMap));
    pp.process (params, eh);
    try
    {
      final Dtd dtd = new DtdParserImpl ().parse (uri, new ResolverUriEntityManager (resolver));
      try
      {
        return new Converter (dtd, er, options).convert ();
      }
      catch (final ErrorReporter.WrappedSAXException e)
      {
        throw e.getException ();
      }
    }
    catch (final ParseException e)
    {
      throw new SAXParseException (e.getMessageBody (),
                                   null,
                                   e.getLocation (),
                                   e.getLineNumber (),
                                   e.getColumnNumber ());
    }
  }

}
