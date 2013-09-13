package com.thaiopensource.relaxng.pattern;

import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.DatatypeStreamingValidator;
import org.relaxng.datatype.ValidationContext;
import org.relaxng.datatype.helpers.StreamingValidatorImpl;

import com.thaiopensource.datatype.Datatype2;

class StringDatatype implements Datatype2
{
  public boolean isValid (final String str, final ValidationContext vc)
  {
    return true;
  }

  public void checkValid (final String str, final ValidationContext vc) throws DatatypeException
  {
    if (!isValid (str, vc))
      throw new DatatypeException ();
  }

  public Object createValue (final String str, final ValidationContext vc)
  {
    return str;
  }

  public boolean isContextDependent ()
  {
    return false;
  }

  public boolean alwaysValid ()
  {
    return true;
  }

  public int getIdType ()
  {
    return ID_TYPE_NULL;
  }

  public boolean sameValue (final Object obj1, final Object obj2)
  {
    return obj1.equals (obj2);
  }

  public int valueHashCode (final Object obj)
  {
    return obj.hashCode ();
  }

  public DatatypeStreamingValidator createStreamingValidator (final ValidationContext vc)
  {
    return new StreamingValidatorImpl (this, vc);
  }
}
