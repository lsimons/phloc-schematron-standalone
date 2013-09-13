package com.thaiopensource.relaxng.translate.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import com.thaiopensource.relaxng.output.common.ErrorReporter;

public class ParamProcessor
{
  private ErrorReporter er;
  private ParamFactory paramFactory;
  private final Map <String, Param> paramMap = new HashMap <String, Param> ();
  private final Set <String> processedParamNames = new HashSet <String> ();

  private static class BadParamException extends Exception
  {}

  static class LocalizedInvalidValueException extends InvalidParamValueException
  {
    private final String key;

    LocalizedInvalidValueException (final String key)
    {
      this.key = key;
    }
  }

  public void declare (final String name, final Param param)
  {
    paramMap.put (name, param);
  }

  public void setParamFactory (final ParamFactory factory)
  {
    this.paramFactory = factory;
  }

  public void process (final String [] params, final ErrorHandler eh) throws InvalidParamsException, SAXException
  {
    er = new ErrorReporter (eh, ParamProcessor.class);
    try
    {
      for (final String param : params)
        processParam (param);
      if (er.getHadError ())
        throw new InvalidParamsException ();
    }
    catch (final ErrorReporter.WrappedSAXException e)
    {
      throw e.getException ();
    }
    finally
    {
      processedParamNames.clear ();
      er = null;
    }
  }

  private void processParam (final String param)
  {
    final int off = param.indexOf ('=');
    String name = null;
    try
    {
      if (off < 0)
      {
        if (param.startsWith ("no-"))
        {
          name = param.substring (3);
          lookupParam (name).set (false);
        }
        else
        {
          name = param;
          lookupParam (name).set (true);
        }
      }
      else
      {
        name = param.substring (0, off);
        lookupParam (name).set (param.substring (off + 1));
      }
    }
    catch (final BadParamException e)
    {}
    catch (final LocalizedInvalidValueException e)
    {
      er.error ("invalid_param_value_detail", name, er.getLocalizer ().message (e.key), null);
    }
    catch (final InvalidParamValueException e)
    {
      final String detail = e.getMessage ();
      if (detail != null)
        er.error ("invalid_param_value_detail", name, detail, null);
      else
        if (off < 0)
          er.error (param.startsWith ("no-") ? "param_only_positive" : "param_only_negative", name, null);
        else
          er.error ("invalid_param_value", name, null);
    }
    catch (final ParamValuePresenceException e)
    {
      if (off < 0)
        er.error ("param_value_required", name, null);
      else
        er.error ("param_value_not_allowed", name, null);
    }
  }

  private Param lookupParam (final String name) throws BadParamException
  {
    Param p = paramMap.get (name);
    if (p == null && paramFactory != null)
      p = paramFactory.createParam (name);
    if (p == null)
    {
      er.error ("unrecognized_param", name, null);
      throw new BadParamException ();
    }
    if (processedParamNames.contains (name))
    {
      if (!p.allowRepeat ())
      {
        er.error ("duplicate_param", name, null);
        throw new BadParamException ();
      }
    }
    else
      processedParamNames.add (name);
    return p;
  }
}
