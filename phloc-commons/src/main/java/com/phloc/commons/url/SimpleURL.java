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
package com.phloc.commons.url;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.ICloneable;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.string.StringHelper;

/**
 * Abstraction of the string parts of a URL but much simpler (and faster) than
 * {@link java.net.URL}.
 * 
 * @author Philip Helger
 */
public final class SimpleURL extends AbstractSimpleURL implements ICloneable <SimpleURL>
{
  public SimpleURL ()
  {
    super ();
  }

  public SimpleURL (@Nonnull final String sHref)
  {
    super (sHref);
  }

  public SimpleURL (@Nonnull final String sHref, @Nullable final Map <String, String> aParams)
  {
    super (sHref, aParams);
  }

  public SimpleURL (@Nonnull final String sHref,
                    @Nullable final Map <String, String> aParams,
                    @Nullable final String sAnchor)
  {
    super (sHref, aParams, sAnchor);
  }

  public SimpleURL (@Nonnull final IURLData aURL)
  {
    super (aURL);
  }

  @Nonnull
  public SimpleURL add (@Nonnull final String sKey, @Nonnull final String sValue)
  {
    if (StringHelper.hasNoText (sKey))
      throw new IllegalArgumentException ("key may not be empty!");
    if (sValue == null)
      throw new NullPointerException ("value");

    if (m_aParams == null)
      m_aParams = new LinkedHashMap <String, String> ();
    m_aParams.put (sKey, sValue);
    return this;
  }

  @Nonnull
  public SimpleURL add (@Nonnull final String sKey, final int nValue)
  {
    return add (sKey, Integer.toString (nValue));
  }

  @Nonnull
  public SimpleURL addIfNonNull (@Nonnull final String sKey, @Nullable final String sValue)
  {
    if (sValue != null)
      add (sKey, sValue);
    return this;
  }

  @Nonnull
  public SimpleURL addAll (@Nullable final Map <String, String> aParams)
  {
    if (ContainerHelper.isNotEmpty (aParams))
    {
      if (m_aParams == null)
        m_aParams = new LinkedHashMap <String, String> ();
      m_aParams.putAll (aParams);
    }
    return this;
  }

  /**
   * Remove the parameter with the given key.
   * 
   * @param sKey
   *        The key to remove
   * @return this
   */
  @Nonnull
  public SimpleURL remove (@Nullable final String sKey)
  {
    if (m_aParams != null)
      m_aParams.remove (sKey);
    return this;
  }

  @Nonnull
  public SimpleURL setAnchor (@Nullable final String sAnchor)
  {
    m_sAnchor = sAnchor;
    return this;
  }

  @Nonnull
  public SimpleURL getClone ()
  {
    return new SimpleURL (this);
  }
}
