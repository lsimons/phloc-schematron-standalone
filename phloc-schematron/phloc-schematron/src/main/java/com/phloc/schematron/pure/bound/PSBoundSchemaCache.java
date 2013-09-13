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

import com.phloc.commons.annotations.IsLocked;
import com.phloc.commons.annotations.IsLocked.ELockType;
import com.phloc.commons.cache.AbstractNotifyingCache;
import com.phloc.schematron.SchematronException;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * A cache for {@link IPSBoundSchema} instances. Use {@link #getInstance()} to
 * retrieve a global singleton instance. Alternatively you may instantiate this
 * class regularly.
 * 
 * @author Philip Helger
 */
public class PSBoundSchemaCache extends AbstractNotifyingCache <PSBoundSchemaCacheKey, IPSBoundSchema>
{
  private static final class SingletonHolder
  {
    static final PSBoundSchemaCache s_aInstance = new PSBoundSchemaCache ();
  }

  /**
   * Default constructor for the singleton.
   */
  private PSBoundSchemaCache ()
  {
    super (PSBoundSchemaCache.class.getName ());
  }

  public PSBoundSchemaCache (@Nonnull final String sCacheName)
  {
    super (sCacheName);
  }

  @Nonnull
  public static PSBoundSchemaCache getInstance ()
  {
    return SingletonHolder.s_aInstance;
  }

  @Override
  @Nonnull
  @IsLocked (ELockType.WRITE)
  @SuppressFBWarnings ("NP_PARAMETER_MUST_BE_NONNULL_BUT_MARKED_AS_NULLABLE")
  protected IPSBoundSchema getValueToCache (@Nullable final PSBoundSchemaCacheKey aKey)
  {
    if (aKey == null)
      throw new NullPointerException ("key");

    try
    {
      return aKey.createBoundSchema ();
    }
    catch (final SchematronException ex)
    {
      // Convert to an uncheck exception :(
      throw new RuntimeException (ex);
    }
  }
}
