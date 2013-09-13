package com.thaiopensource.relaxng.translate.util;

public abstract class IntegerParam extends AbstractParam
{
  private final int minValue;
  private final int maxValue;

  public IntegerParam (final int minValue, final int maxValue)
  {
    this.minValue = minValue;
    this.maxValue = maxValue;
  }

  public IntegerParam ()
  {
    this (Integer.MIN_VALUE, Integer.MAX_VALUE);
  }

  @Override
  public void set (final String value) throws InvalidParamValueException
  {
    try
    {
      final int n = Integer.parseInt (value);
      if (n < minValue || n > maxValue)
        throw new ParamProcessor.LocalizedInvalidValueException ("out_of_range_integer");
      setInteger (n);
    }
    catch (final NumberFormatException e)
    {
      throw new ParamProcessor.LocalizedInvalidValueException ("not_an_integer");
    }
  }

  protected abstract void setInteger (int value) throws InvalidParamValueException;
}
