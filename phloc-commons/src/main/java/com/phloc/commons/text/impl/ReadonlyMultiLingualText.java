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
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.phloc.commons.text.IReadonlyMultiLingualText;
import com.phloc.commons.text.ISimpleMultiLingualText;

/**
 * This class represents a multilingual text. It is internally represented as a
 * map from {@link Locale} to the language dependent string.
 * 
 * @author Philip Helger
 */
@Immutable
public final class ReadonlyMultiLingualText extends TextProvider implements IReadonlyMultiLingualText
{
  /**
   * Create an empty read-only multilingual text. Handle with care, as this type
   * does not allow for public modification!
   */
  public ReadonlyMultiLingualText ()
  {}

  public ReadonlyMultiLingualText (@Nonnull final Map <Locale, String> aContent)
  {
    if (aContent == null)
      throw new NullPointerException ("content");

    for (final Map.Entry <Locale, String> aEntry : aContent.entrySet ())
      internalAddText (aEntry.getKey (), aEntry.getValue ());
  }

  public ReadonlyMultiLingualText (@Nonnull final ISimpleMultiLingualText aSimpleMLT)
  {
    if (aSimpleMLT == null)
      throw new NullPointerException ("textProvider");

    for (final Locale aLocale : aSimpleMLT.getAllLocales ())
      internalAddText (aLocale, aSimpleMLT.getText (aLocale));
  }

  public ReadonlyMultiLingualText (@Nonnull final IReadonlyMultiLingualText aMLT)
  {
    if (aMLT == null)
      throw new NullPointerException ("MLT");

    for (final Map.Entry <Locale, String> aEntry : aMLT.getMap ().entrySet ())
      internalAddText (aEntry.getKey (), aEntry.getValue ());
  }
}
