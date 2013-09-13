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
package com.phloc.commons.locale.country;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.junit.Test;

import com.phloc.commons.CGlobal;
import com.phloc.commons.locale.LocaleCache;
import com.phloc.commons.string.StringHelper;

/**
 * Test class for class {@link CountryCache}.
 * 
 * @author Philip Helger
 */
public final class CountryCacheTest
{
  @Test
  public void testGetCountry ()
  {
    assertNull (CountryCache.getCountry (""));
    assertNull (CountryCache.getCountry ((String) null));
    assertNull (CountryCache.getCountry ((Locale) null));
    assertNotNull (CountryCache.getCountry ("AT"));
    assertNotNull (CountryCache.getCountry ("at"));
    assertNotNull (CountryCache.getCountry ("pl"));
    // Returns a valid locale, but emits a warning:
    assertNotNull (CountryCache.getCountry ("xxx"));
    assertEquals (CountryCache.getCountry ("ch"), CountryCache.getCountry (new Locale ("de", "ch")));
    assertEquals (LocaleCache.getLocale ("", "AT", ""), CountryCache.getCountry ("_AT"));
    assertEquals (LocaleCache.getLocale ("", "AT", ""), CountryCache.getCountry ("de_AT"));
    assertEquals (CountryCache.getCountry ("AT"), CountryCache.getCountry (CountryCache.getCountry ("AT").toString ()));
    for (final String sLocale : CountryCache.getAllCountries ())
      assertTrue (CountryCache.containsCountry (sLocale));
    assertFalse (CountryCache.containsCountry ((String) null));
    assertFalse (CountryCache.containsCountry (CGlobal.LOCALE_ALL));
    assertFalse (CountryCache.containsCountry ((Locale) null));
  }

  @Test
  public void testNoConcurrentModification ()
  {
    final Set <Locale> aCountries = new HashSet <Locale> ();
    for (final String sCountry : CountryCache.getAllCountries ())
      aCountries.add (CountryCache.getCountry (sCountry));

    for (final Locale aCountry : aCountries)
    {
      assertTrue (StringHelper.hasNoText (aCountry.getLanguage ()));
      assertTrue (StringHelper.hasText (aCountry.getCountry ()));
      assertTrue (StringHelper.hasNoText (aCountry.getVariant ()));
    }
  }

  @Test
  public void testNoConcurrentModification2 ()
  {
    final Set <Locale> aCountries = new HashSet <Locale> ();
    for (final Locale aCountry : CountryCache.getAllCountryLocales ())
      aCountries.add (CountryCache.getCountry (aCountry));

    for (final Locale aCountry : aCountries)
    {
      assertTrue (StringHelper.hasNoText (aCountry.getLanguage ()));
      assertTrue (StringHelper.hasText (aCountry.getCountry ()));
      assertTrue (StringHelper.hasNoText (aCountry.getVariant ()));
    }
  }
}
