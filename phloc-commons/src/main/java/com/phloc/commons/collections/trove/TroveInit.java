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
package com.phloc.commons.collections.trove;

import javax.annotation.concurrent.Immutable;

import com.phloc.commons.SystemProperties;
import com.phloc.commons.annotations.PresentForCodeCoverage;

/**
 * Trove4J initialization helper.
 * 
 * @author Philip Helger
 */
@Immutable
public final class TroveInit
{
  @SuppressWarnings ("unused")
  @PresentForCodeCoverage
  private static final TroveInit s_aInstance = new TroveInit ();

  private TroveInit ()
  {}

  /**
   * Initialize all system properties for {@link gnu.trove.impl.Constants}. This
   * must be called, before the first Trove object is created!
   * 
   * @param bTroveVerbose
   *        <code>true</code> to log the created constants, <code>false</code>
   *        to do it silently.
   */
  public static void initTrove (final boolean bTroveVerbose)
  {
    if (bTroveVerbose)
      SystemProperties.setPropertyValue ("gnu.trove.verbose", "true");
    SystemProperties.setPropertyValue ("gnu.trove.no_entry.byte", "MIN_VALUE");
    SystemProperties.setPropertyValue ("gnu.trove.no_entry.char", "MAX_VALUE");
    SystemProperties.setPropertyValue ("gnu.trove.no_entry.float", "NEGATIVE_INFINITY");
    SystemProperties.setPropertyValue ("gnu.trove.no_entry.double", "NEGATIVE_INFINITY");
    SystemProperties.setPropertyValue ("gnu.trove.no_entry.int", "MIN_VALUE");
    SystemProperties.setPropertyValue ("gnu.trove.no_entry.long", "MIN_VALUE");
    SystemProperties.setPropertyValue ("gnu.trove.no_entry.short", "MIN_VALUE");
  }
}
