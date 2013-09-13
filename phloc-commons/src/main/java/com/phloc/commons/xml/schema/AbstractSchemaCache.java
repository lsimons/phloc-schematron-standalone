/**
 * Copyright (C) 2006-2013 phloc systems
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
package com.phloc.commons.xml.schema;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.cache.convert.SimpleCacheWithConversion;
import com.phloc.commons.collections.ArrayHelper;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.convert.IUnidirectionalConverter;
import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.string.ToStringGenerator;
import com.phloc.commons.xml.sax.LoggingSAXErrorHandler;
import com.phloc.commons.xml.transform.TransformSourceFactory;

/**
 * Abstract base class for caching JAXP validation scheme elements. This class
 * is deprecated in favour of {@link DefaultSchemaCache}.
 *
 * @author Philip Helger
 */
@Deprecated
@ThreadSafe
public abstract class AbstractSchemaCache extends SimpleCacheWithConversion <String, Schema>
{
  private static final String PREFIX_SYNTHETIC = "synthetic:";

  private final String m_sSchemaTypeName;

  public AbstractSchemaCache (@Nonnull final String sSchemaTypeName)
  {
    super (AbstractSchemaCache.class.getName () + "$" + sSchemaTypeName);
    m_sSchemaTypeName = sSchemaTypeName;
  }

  @Nonnull
  protected abstract SchemaFactory internalGetSchemaFactory ();

  @Nonnull
  private Schema _getSchema (@Nonnull final String sResourceID, @Nonnull final Source [] aSources)
  {
    return getFromCache (sResourceID, new IUnidirectionalConverter <String, Schema> ()
    {
      @Nonnull
      public Schema convert (final String sMyResourceID)
      {
        // Note: sMyResourceID == sResourceID
        try
        {
          final Schema ret = internalGetSchemaFactory ().newSchema (aSources);
          if (ret == null)
            throw new IllegalStateException ("Failed to create " +
                                             m_sSchemaTypeName +
                                             " schema from " +
                                             Arrays.toString (aSources));
          return ret;
        }
        catch (final SAXException ex)
        {
          throw new IllegalArgumentException ("Failed to parse " +
                                              m_sSchemaTypeName +
                                              " from " +
                                              Arrays.toString (aSources), ex);
        }
      }
    });
  }

  @Nonnull
  private Schema _getSchemaFromMultipleSources (@Nonnull @Nonempty final Set <IReadableResource> aRealResources)
  {
    if (aRealResources.size () == 1)
    {
      // In reality it'sonly one resource...
      return getSchema (ContainerHelper.getFirstElement (aRealResources));
    }

    // Collect all sources
    final Source [] aSources = new Source [aRealResources.size ()];

    // Create the unique synthetic ID for the passed resources
    int nIndex = 0;
    final StringBuilder aResourceID = new StringBuilder (PREFIX_SYNTHETIC);
    for (final IReadableResource aResource : aRealResources)
    {
      if (nIndex > 0)
        aResourceID.append (';');
      aResourceID.append (aResource.getResourceID ());

      aSources[nIndex++] = TransformSourceFactory.create (aResource);
    }

    return _getSchema (aResourceID.toString (), aSources);
  }

  @Nonnull
  public final Schema getSchema (@Nonnull final IReadableResource aResource)
  {
    if (aResource == null)
      throw new NullPointerException ("resources");

    return _getSchema (aResource.getResourceID (), new Source [] { TransformSourceFactory.create (aResource) });
  }

  @Nonnull
  public final Schema getSchema (@Nonnull @Nonempty final IReadableResource... aResources)
  {
    if (ArrayHelper.isEmpty (aResources))
      throw new IllegalArgumentException ("no resources provided!");

    // Remove all duplicates while maintaining the order
    return _getSchemaFromMultipleSources (ContainerHelper.newOrderedSet (aResources));
  }

  @Nonnull
  public final Schema getSchema (@Nonnull @Nonempty final List <? extends IReadableResource> aResources)
  {
    if (ContainerHelper.isEmpty (aResources))
      throw new IllegalArgumentException ("no resources provided!");

    // Remove all duplicates while maintaining the order
    return _getSchemaFromMultipleSources (ContainerHelper.newOrderedSet (aResources));
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("schemaTypeName", m_sSchemaTypeName).toString ();
  }

  /**
   * Utility method to get the validator for a given schema using the standard
   * logging error handler.
   *
   * @param aSchema
   *        The schema for which the validator is to be retrieved. May not be
   *        <code>null</code>.
   * @return The validator and never <code>null</code>.
   */
  @Nonnull
  public static Validator getValidatorFromSchema (@Nonnull final Schema aSchema)
  {
    if (aSchema == null)
      throw new NullPointerException ("schema");

    final Validator aValidator = aSchema.newValidator ();
    aValidator.setErrorHandler (LoggingSAXErrorHandler.getInstance ());
    return aValidator;
  }
}
