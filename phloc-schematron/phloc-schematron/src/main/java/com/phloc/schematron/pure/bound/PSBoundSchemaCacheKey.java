/**
 * Copyright (C) 2013 phloc systems
 * http://www.phloc.com
 * office[at]phloc[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.phloc.schematron.pure.bound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.phloc.commons.annotations.OverrideOnDemand;
import com.phloc.commons.equals.EqualsUtils;
import com.phloc.commons.hash.HashCodeGenerator;
import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.string.ToStringGenerator;
import com.phloc.schematron.SchematronException;
import com.phloc.schematron.pure.binding.IPSQueryBinding;
import com.phloc.schematron.pure.binding.PSQueryBindingRegistry;
import com.phloc.schematron.pure.errorhandler.IPSErrorHandler;
import com.phloc.schematron.pure.exchange.PSReader;
import com.phloc.schematron.pure.model.PSSchema;
import com.phloc.schematron.pure.preprocess.PSPreprocessor;
import com.phloc.schematron.pure.preprocess.SchematronPreprocessException;

/**
 * This class represents keys for the {@link PSBoundSchemaCache}. It is a
 * combination of a resource and a phase. It is the responsible class for
 * reading and binding a Schematron resource.
 * 
 * @author Philip Helger
 */
@Immutable
public class PSBoundSchemaCacheKey
{
  private final IReadableResource m_aResource;
  private final String m_sPhase;
  private final IPSErrorHandler m_aErrorHandler;

  public PSBoundSchemaCacheKey (@Nonnull final IReadableResource aResource,
                                @Nullable final String sPhase,
                                @Nullable final IPSErrorHandler aErrorHandler)
  {
    if (aResource == null)
      throw new NullPointerException ("Resource");

    m_aResource = aResource;
    m_sPhase = sPhase;
    m_aErrorHandler = aErrorHandler;
  }

  /**
   * @return The resource passed in the constructor. Never <code>null</code>.
   */
  @Nonnull
  public final IReadableResource getResource ()
  {
    return m_aResource;
  }

  /**
   * @return The phase selected in the constructor. May be <code>null</code>.
   */
  @Nullable
  public final String getPhase ()
  {
    return m_sPhase;
  }

  /**
   * @return The error handler passed in the constructor. May be
   *         <code>null</code>.
   */
  @Nullable
  public final IPSErrorHandler getErrorHandler ()
  {
    return m_aErrorHandler;
  }

  /**
   * Read the specified schema from the passed resource.
   * 
   * @param aResource
   *        The resource to read from. Never <code>null</code>.
   * @param aErrorHandler
   *        The error handler to use. May be <code>null</code>.
   * @return The read schema. May not be <code>null</code>.
   * @throws SchematronException
   *         In case there is an error reading.
   */
  @Nonnull
  @OverrideOnDemand
  protected PSSchema readSchema (@Nonnull final IReadableResource aResource,
                                 @Nullable final IPSErrorHandler aErrorHandler) throws SchematronException
  {
    return new PSReader (aResource, aErrorHandler).readSchema ();
  }

  /**
   * Determine the query binding for the read schema.
   * 
   * @param aSchema
   *        The read schema. Never <code>null</code>.
   * @return The query binding to use. Never <code>null</code>.
   * @throws SchematronException
   *         In case the determination fails.
   */
  @Nonnull
  @OverrideOnDemand
  protected IPSQueryBinding getQueryBinding (@Nonnull final PSSchema aSchema) throws SchematronException
  {
    return PSQueryBindingRegistry.getQueryBindingOfNameOrThrow (aSchema.getQueryBinding ());
  }

  /**
   * Create the pre-processor to be used for
   * {@link #createPreprocessedSchema(PSSchema, IPSQueryBinding)}.
   * 
   * @param aQueryBinding
   *        The query binding to be determined from the read schema. Never
   *        <code>null</code>.
   * @return The pre-processor to be used.
   */
  @Nonnull
  @OverrideOnDemand
  protected PSPreprocessor createPreprocessor (@Nonnull final IPSQueryBinding aQueryBinding)
  {
    final PSPreprocessor aPreprocessor = new PSPreprocessor (aQueryBinding);
    aPreprocessor.setKeepTitles (true);
    return aPreprocessor;
  }

  /**
   * Pre-process the read schema, using the determined query binding.
   * 
   * @param aSchema
   *        The read schema. Never <code>null</code>.
   * @param aQueryBinding
   *        The determined query binding. Never <code>null</code>.
   * @return The pre-processed schema and never <code>null</code>.
   * @throws SchematronException
   *         In case pre-processing fails
   */
  @Nonnull
  @OverrideOnDemand
  protected PSSchema createPreprocessedSchema (@Nonnull final PSSchema aSchema,
                                               @Nonnull final IPSQueryBinding aQueryBinding) throws SchematronException
  {
    final PSPreprocessor aPreprocessor = createPreprocessor (aQueryBinding);
    final PSSchema aPreprocessedSchema = aPreprocessor.getAsPreprocessedSchema (aSchema);
    if (aPreprocessedSchema == null)
      throw new SchematronPreprocessException ("Failed to preprocess schema " + aSchema);
    return aPreprocessedSchema;
  }

  /**
   * The main routine to create a bound schema from the passed resource and
   * phase. The usual routine is to
   * <ol>
   * <li>read the schema from the resource - see
   * {@link #readSchema(IReadableResource, IPSErrorHandler)}</li>
   * <li>resolve the query binding - see {@link #getQueryBinding(PSSchema)}</li>
   * <li>pre-process the schema -
   * {@link #createPreprocessedSchema(PSSchema, IPSQueryBinding)}</li>
   * <li>and finally bind it -
   * {@link IPSQueryBinding#bind(PSSchema, String, IPSErrorHandler)}</li>
   * </ol>
   * 
   * @return The bound schema. Never <code>null</code>.
   * @throws SchematronException
   *         In case reading or binding fails.
   */
  @Nonnull
  public IPSBoundSchema createBoundSchema () throws SchematronException
  {
    // Read schema from resource
    final PSSchema aSchema = readSchema (getResource (), getErrorHandler ());

    // Resolve the query binding to be used
    final IPSQueryBinding aQueryBinding = getQueryBinding (aSchema);

    // Pre-process schema
    final PSSchema aPreprocessedSchema = createPreprocessedSchema (aSchema, aQueryBinding);

    // And finally bind the pre-processed schema
    return aQueryBinding.bind (aPreprocessedSchema, getPhase (), getErrorHandler ());
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final PSBoundSchemaCacheKey rhs = (PSBoundSchemaCacheKey) o;
    return m_aResource.equals (rhs.m_aResource) && EqualsUtils.equals (m_sPhase, rhs.m_sPhase);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aResource).append (m_sPhase).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("resource", m_aResource).append ("phase", m_sPhase).toString ();
  }
}
