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
package com.phloc.commons.text.resource;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.hash.HashCodeGenerator;
import com.phloc.commons.string.ToStringGenerator;
import com.phloc.commons.text.impl.AbstractTextProvider;

/**
 * This class encapsulates the resource bundle handling.
 * 
 * @author Philip Helger
 */
public final class ResourceBundleTextProvider extends AbstractTextProvider
{
  private final ResourceBundleKey m_aResBundleKey;

  public ResourceBundleTextProvider (@Nonnull final String sBundleName, @Nonnull final String sKey)
  {
    this (new ResourceBundleKey (sBundleName, sKey));
  }

  public ResourceBundleTextProvider (@Nonnull final ResourceBundleKey aResBundleKey)
  {
    if (aResBundleKey == null)
      throw new NullPointerException ("resBundleKey");
    m_aResBundleKey = aResBundleKey;
  }

  @Override
  @Nullable
  protected Locale internalGetLocaleToUseWithFallback (@Nullable final Locale aContentLocale)
  {
    return aContentLocale;
  }

  @Override
  @Nullable
  protected String internalGetText (@Nonnull final Locale aContentLocale)
  {
    return m_aResBundleKey.getString (aContentLocale);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!(o instanceof ResourceBundleTextProvider))
      return false;
    final ResourceBundleTextProvider rhs = (ResourceBundleTextProvider) o;
    return m_aResBundleKey.equals (rhs.m_aResBundleKey);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aResBundleKey).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("resBundleKey", m_aResBundleKey).toString ();
  }
}
