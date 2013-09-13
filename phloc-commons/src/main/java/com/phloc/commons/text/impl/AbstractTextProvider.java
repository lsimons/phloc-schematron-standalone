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

import com.phloc.commons.text.ITextProvider;

/**
 * An abstract implementation of the {@link ITextProvider} that has default
 * implementations for the formatting things.
 * 
 * @author Philip Helger
 */
public abstract class AbstractTextProvider implements ITextProvider
{
  protected AbstractTextProvider ()
  {}

  /**
   * Main text resolving
   * 
   * @param aContentLocale
   *        locale to use
   * @return <code>null</code> if no such text present in the passed locale
   */
  @Nullable
  protected abstract String internalGetText (@Nonnull Locale aContentLocale);

  /**
   * Determine the locale to use.
   * 
   * @param aContentLocale
   *        Requested locale
   * @return The locale to use. May be <code>null</code>.
   */
  @Nullable
  protected abstract Locale internalGetLocaleToUseWithFallback (@Nonnull Locale aContentLocale);

  @Nullable
  public final String getText (@Nonnull final Locale aContentLocale)
  {
    if (aContentLocale == null)
      throw new NullPointerException ("locale");
    return internalGetText (aContentLocale);
  }

  @Nullable
  public final String getTextWithLocaleFallback (@Nonnull final Locale aContentLocale)
  {
    if (aContentLocale == null)
      throw new NullPointerException ("locale");
    final Locale aLocaleToUse = internalGetLocaleToUseWithFallback (aContentLocale);
    return aLocaleToUse == null ? null : internalGetText (aLocaleToUse);
  }

  @Nullable
  public final String getTextWithArgs (@Nonnull final Locale aContentLocale, @Nullable final Object... aArgs)
  {
    final String sText = getText (aContentLocale);
    return TextFormatter.getFormattedText (sText, aArgs);
  }

  @Nullable
  public final String getTextWithLocaleFallbackAndArgs (@Nonnull final Locale aContentLocale,
                                                        @Nullable final Object... aArgs)
  {
    final String sText = getTextWithLocaleFallback (aContentLocale);
    return TextFormatter.getFormattedText (sText, aArgs);
  }
}
