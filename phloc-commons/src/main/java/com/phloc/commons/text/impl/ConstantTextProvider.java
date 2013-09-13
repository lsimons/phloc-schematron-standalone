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
package com.phloc.commons.text.impl;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.equals.EqualsUtils;
import com.phloc.commons.hash.HashCodeGenerator;
import com.phloc.commons.name.IHasDisplayName;
import com.phloc.commons.name.IHasDisplayText;
import com.phloc.commons.name.IHasName;
import com.phloc.commons.string.ToStringGenerator;
import com.phloc.commons.text.ITextProvider;

/**
 * An implementation of the {@link ITextProvider} interface that always returns
 * a constant string. Use this only for texts that never need to be translated!<br>
 * {@link IHasDisplayText}, {@link IHasDisplayName} and {@link IHasName} are
 * only implemented, so that instances of this class can also be used as valid
 * substitutes for these interfaces.
 * 
 * @author Philip Helger
 */
public final class ConstantTextProvider extends AbstractTextProvider implements IHasDisplayText, IHasDisplayName, IHasName
{
  private final String m_sFixedText;

  public ConstantTextProvider (@Nonnull final String sFixedText)
  {
    if (sFixedText == null)
      throw new NullPointerException ("fixedText");
    m_sFixedText = sFixedText;
  }

  @Override
  protected Locale internalGetLocaleToUseWithFallback (@Nonnull final Locale aContentLocale)
  {
    return aContentLocale;
  }

  @Override
  @Nullable
  protected String internalGetText (@Nonnull final Locale aContentLocale)
  {
    return m_sFixedText;
  }

  @Nonnull
  public String getDisplayText (@Nonnull final Locale aContentLocale)
  {
    return m_sFixedText;
  }

  @Nonnull
  public String getDisplayName ()
  {
    return m_sFixedText;
  }

  @Nonnull
  public String getName ()
  {
    return m_sFixedText;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!(o instanceof ConstantTextProvider))
      return false;
    final ConstantTextProvider rhs = (ConstantTextProvider) o;
    return EqualsUtils.equals (m_sFixedText, rhs.m_sFixedText);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sFixedText).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("text", m_sFixedText).toString ();
  }
}
