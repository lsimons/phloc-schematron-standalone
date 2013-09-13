package org.relaxng.datatype.helpers;

import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeBuilder;
import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;

/**
 * Dummy implementation of {@link DatatypeBuilder}. This implementation can be
 * used for Datatypes which have no parameters. Any attempt to add parameters
 * will be rejected.
 * <p>
 * Typical usage would be:
 * 
 * <PRE>
 * <XMP>
 * class MyDatatypeLibrary implements DatatypeLibrary {
 *     ....
 *     DatatypeBuilder createDatatypeBuilder( String typeName ) {
 *         return new ParameterleessDatatypeBuilder(createDatatype(typeName));
 *     }
 *     ....
 * }
 * </XMP>
 * </PRE>
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public final class ParameterlessDatatypeBuilder implements DatatypeBuilder
{

  /** This type object is returned for the derive method. */
  private final Datatype baseType;

  public ParameterlessDatatypeBuilder (final Datatype baseType)
  {
    this.baseType = baseType;
  }

  public void addParameter (final String name, final String strValue, final ValidationContext context) throws DatatypeException
  {
    throw new DatatypeException ();
  }

  public Datatype createDatatype () throws DatatypeException
  {
    return baseType;
  }
}
