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
package com.phloc.commons.stats.visit;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.phloc.commons.annotations.PresentForCodeCoverage;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.stats.IStatisticsHandlerCache;
import com.phloc.commons.stats.IStatisticsHandlerCounter;
import com.phloc.commons.stats.IStatisticsHandlerKeyedCounter;
import com.phloc.commons.stats.IStatisticsHandlerKeyedSize;
import com.phloc.commons.stats.IStatisticsHandlerKeyedTimer;
import com.phloc.commons.stats.IStatisticsHandlerSize;
import com.phloc.commons.stats.IStatisticsHandlerTimer;
import com.phloc.commons.stats.StatisticsManager;

/**
 * Class for iterating all available statistics
 * 
 * @author Philip Helger
 */
@Immutable
public final class StatisticsWalker
{
  @PresentForCodeCoverage
  @SuppressWarnings ("unused")
  private static final StatisticsWalker s_aInstance = new StatisticsWalker ();

  private StatisticsWalker ()
  {}

  /**
   * Walk all available statistics elements with the passed statistics visitor.
   * 
   * @param aVisitor
   *        The visitor to use. May not be <code>null</code>.
   */
  public static void walkStatistics (@Nonnull final IStatisticsVisitor aVisitor)
  {
    if (aVisitor == null)
      throw new NullPointerException ("visitor");

    // For all cache handler
    List <String> aHandlers = ContainerHelper.getSorted (StatisticsManager.getAllCacheHandler ());
    for (final String sName : aHandlers)
    {
      final IStatisticsHandlerCache aHandler = StatisticsManager.getCacheHandler (sName);
      aVisitor.onCache (sName, aHandler);
    }

    // For all timer handler
    aHandlers = ContainerHelper.getSorted (StatisticsManager.getAllTimerHandler ());
    for (final String sName : aHandlers)
    {
      final IStatisticsHandlerTimer aHandler = StatisticsManager.getTimerHandler (sName);
      aVisitor.onTimer (sName, aHandler);
    }

    // For all keyed timer handler
    aHandlers = ContainerHelper.getSorted (StatisticsManager.getAllKeyedTimerHandler ());
    for (final String sName : aHandlers)
    {
      final IStatisticsHandlerKeyedTimer aHandler = StatisticsManager.getKeyedTimerHandler (sName);
      aVisitor.onKeyedTimer (sName, aHandler);
    }

    // For all size handler
    aHandlers = ContainerHelper.getSorted (StatisticsManager.getAllSizeHandler ());
    for (final String sName : aHandlers)
    {
      final IStatisticsHandlerSize aHandler = StatisticsManager.getSizeHandler (sName);
      aVisitor.onSize (sName, aHandler);
    }

    // For all keyed size handler
    aHandlers = ContainerHelper.getSorted (StatisticsManager.getAllKeyedSizeHandler ());
    for (final String sName : aHandlers)
    {
      final IStatisticsHandlerKeyedSize aHandler = StatisticsManager.getKeyedSizeHandler (sName);
      aVisitor.onKeyedSize (sName, aHandler);
    }

    // For all counter handler
    aHandlers = ContainerHelper.getSorted (StatisticsManager.getAllCounterHandler ());
    for (final String sName : aHandlers)
    {
      final IStatisticsHandlerCounter aHandler = StatisticsManager.getCounterHandler (sName);
      aVisitor.onCounter (sName, aHandler);
    }

    // For all keyed counter handler
    aHandlers = ContainerHelper.getSorted (StatisticsManager.getAllKeyedCounterHandler ());
    for (final String sName : aHandlers)
    {
      final IStatisticsHandlerKeyedCounter aHandler = StatisticsManager.getKeyedCounterHandler (sName);
      aVisitor.onKeyedCounter (sName, aHandler);
    }
  }
}
