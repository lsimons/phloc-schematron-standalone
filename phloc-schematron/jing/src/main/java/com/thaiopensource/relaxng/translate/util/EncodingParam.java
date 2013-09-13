package com.thaiopensource.relaxng.translate.util;

import java.io.UnsupportedEncodingException;

import com.thaiopensource.xml.util.EncodingMap;

public abstract class EncodingParam extends AbstractParam
{
  @Override
  public void set (final String value) throws InvalidParamValueException
  {
    try
    {
      "x".getBytes (EncodingMap.getJavaName (value));
    }
    catch (final UnsupportedEncodingException e)
    {
      throw new ParamProcessor.LocalizedInvalidValueException ("unsupported_encoding");
    }
    setEncoding (value);
  }

  protected abstract void setEncoding (String encoding) throws InvalidParamValueException;
}
