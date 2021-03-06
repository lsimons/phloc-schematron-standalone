package com.thaiopensource.datatype.xsd;

import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeBuilder;
import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;

import com.thaiopensource.datatype.xsd.regex.RegexSyntaxException;
import com.thaiopensource.util.Localizer;

class DatatypeBuilderImpl implements DatatypeBuilder
{
  static final Localizer localizer = new Localizer (DatatypeBuilderImpl.class);

  private DatatypeBase base;
  private final DatatypeLibraryImpl library;

  DatatypeBuilderImpl (final DatatypeLibraryImpl library, final DatatypeBase base) throws DatatypeException
  {
    this.library = library;
    this.base = base;
  }

  public void addParameter (final String name, final String value, final ValidationContext context) throws DatatypeException
  {
    if (name.equals ("pattern"))
      addPatternParam (value);
    else
      if (name.equals ("minInclusive"))
        addMinInclusiveParam (value, context);
      else
        if (name.equals ("maxInclusive"))
          addMaxInclusiveParam (value, context);
        else
          if (name.equals ("minExclusive"))
            addMinExclusiveParam (value, context);
          else
            if (name.equals ("maxExclusive"))
              addMaxExclusiveParam (value, context);
            else
              if (name.equals ("length"))
                addLengthParam (value);
              else
                if (name.equals ("minLength"))
                  addMinLengthParam (value);
                else
                  if (name.equals ("maxLength"))
                    addMaxLengthParam (value);
                  else
                    if (name.equals ("fractionDigits"))
                      addScaleParam (value);
                    else
                      if (name.equals ("totalDigits"))
                        addPrecisionParam (value);
                      else
                        if (name.equals ("enumeration"))
                          error ("enumeration_param");
                        else
                          if (name.equals ("whiteSpace"))
                            error ("whiteSpace_param");
                          else
                            error ("unrecognized_param", name);
  }

  private void addPatternParam (final String value) throws DatatypeException
  {
    try
    {
      base = new PatternRestrictDatatype (base, library.getRegexEngine ().compile (value), value);
    }
    catch (final RegexSyntaxException e)
    {
      int pos = e.getPosition ();
      if (pos == RegexSyntaxException.UNKNOWN_POSITION)
        pos = DatatypeException.UNKNOWN;
      error ("invalid_regex", e.getMessage (), pos);
    }
  }

  private void addMinInclusiveParam (final String value, final ValidationContext context) throws DatatypeException
  {
    base = new MinInclusiveRestrictDatatype (base, getLimit (value, context), value);
  }

  private void addMaxInclusiveParam (final String value, final ValidationContext context) throws DatatypeException
  {
    base = new MaxInclusiveRestrictDatatype (base, getLimit (value, context), value);
  }

  private void addMinExclusiveParam (final String value, final ValidationContext context) throws DatatypeException
  {
    base = new MinExclusiveRestrictDatatype (base, getLimit (value, context), value);
  }

  private void addMaxExclusiveParam (final String value, final ValidationContext context) throws DatatypeException
  {
    base = new MaxExclusiveRestrictDatatype (base, getLimit (value, context), value);
  }

  private Object getLimit (String str, final ValidationContext context) throws DatatypeException
  {
    if (base.getOrderRelation () == null)
      error ("not_ordered");
    str = base.normalizeWhiteSpace (str);
    try
    {
      base.checkLexicallyAllows (str);
      return base.getValue (str, context);
    }
    catch (final DatatypeException e)
    {
      throw new DatatypeException (localizer.message ("invalid_limit", str, e.getMessage ()));
    }
  }

  private void addLengthParam (final String value) throws DatatypeException
  {
    base = new LengthRestrictDatatype (base, getLength (value));
  }

  private void addMinLengthParam (final String value) throws DatatypeException
  {
    base = new MinLengthRestrictDatatype (base, getLength (value));
  }

  private void addMaxLengthParam (final String value) throws DatatypeException
  {
    base = new MaxLengthRestrictDatatype (base, getLength (value));
  }

  private int getLength (final String str) throws DatatypeException
  {
    if (base.getMeasure () == null)
      error ("no_length");
    final int len = convertNonNegativeInteger (str);
    if (len < 0)
      error ("length_not_non_negative_integer");
    return len;
  }

  private void addScaleParam (final String str) throws DatatypeException
  {
    if (!(base.getPrimitive () instanceof DecimalDatatype))
      error ("scale_not_derived_from_decimal");
    final int scale = convertNonNegativeInteger (str);
    if (scale < 0)
      error ("scale_not_non_negative_integer");
    base = new ScaleRestrictDatatype (base, scale);
  }

  private void addPrecisionParam (final String str) throws DatatypeException
  {
    if (!(base.getPrimitive () instanceof DecimalDatatype))
      error ("precision_not_derived_from_decimal");
    final int scale = convertNonNegativeInteger (str);
    if (scale <= 0)
      error ("precision_not_positive_integer");
    base = new PrecisionRestrictDatatype (base, scale);
  }

  public Datatype createDatatype ()
  {
    return base;
  }

  private static void error (final String key) throws DatatypeException
  {
    throw new DatatypeException (localizer.message (key));
  }

  private static void error (final String key, final String arg) throws DatatypeException
  {
    throw new DatatypeException (localizer.message (key, arg));
  }

  private static void error (final String key, final String arg, final int pos) throws DatatypeException
  {
    throw new DatatypeException (pos, localizer.message (key, arg));
  }

  // Return -1 for anything that is not a nonNegativeInteger
  // Return Integer.MAX_VALUE for values that are too big

  private int convertNonNegativeInteger (String str)
  {
    str = str.trim ();
    final DecimalDatatype decimal = new DecimalDatatype ();
    if (!decimal.lexicallyAllows (str))
      return -1;
    // Canonicalize the value
    str = decimal.getValue (str, null).toString ();
    // Reject negative and fractional numbers
    if (str.charAt (0) == '-' || str.indexOf ('.') >= 0)
      return -1;
    try
    {
      return Integer.parseInt (str);
    }
    catch (final NumberFormatException e)
    {
      // Map out of range integers to MAX_VALUE
      return Integer.MAX_VALUE;
    }
  }
}
