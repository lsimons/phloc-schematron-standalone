package com.thaiopensource.relaxng.translate.util;

import com.thaiopensource.xml.util.Naming;

public abstract class NCNameParam extends AbstractParam
{
  @Override
  public void set (final String value) throws InvalidParamValueException
  {
    if (!Naming.isNcname (value))
      throw new ParamProcessor.LocalizedInvalidValueException ("invalid_ncname");
    setNCName (value);
  }

  protected abstract void setNCName (String value) throws InvalidParamValueException;
}
