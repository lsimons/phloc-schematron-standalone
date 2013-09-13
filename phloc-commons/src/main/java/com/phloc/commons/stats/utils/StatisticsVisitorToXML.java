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
package com.phloc.commons.stats.utils;

import javax.annotation.Nonnull;

import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.microdom.IMicroElement;
import com.phloc.commons.stats.IStatisticsHandlerCache;
import com.phloc.commons.stats.IStatisticsHandlerCounter;
import com.phloc.commons.stats.IStatisticsHandlerKeyedCounter;
import com.phloc.commons.stats.IStatisticsHandlerKeyedSize;
import com.phloc.commons.stats.IStatisticsHandlerKeyedTimer;
import com.phloc.commons.stats.IStatisticsHandlerSize;
import com.phloc.commons.stats.IStatisticsHandlerTimer;
import com.phloc.commons.stats.visit.IStatisticsVisitor;

/**
 * Special implementation of the {@link IStatisticsVisitor} interface that fills
 * a micro element with all current values
 * 
 * @author Philip Helger
 */
public final class StatisticsVisitorToXML implements IStatisticsVisitor
{
  private final IMicroElement m_eRoot;

  public StatisticsVisitorToXML (@Nonnull final IMicroElement eRoot)
  {
    if (eRoot == null)
      throw new NullPointerException ("root");
    m_eRoot = eRoot;
  }

  public void onCache (final String sName, final IStatisticsHandlerCache aHandler)
  {
    if (aHandler.getInvocationCount () > 0)
      m_eRoot.appendElement (StatisticsExporter.ELEMENT_CACHE)
             .setAttribute (StatisticsExporter.ATTR_NAME, sName)
             .setAttribute (StatisticsExporter.ATTR_INVOCATIONCOUNT, Integer.toString (aHandler.getInvocationCount ()))
             .setAttribute (StatisticsExporter.ATTR_HITS, Integer.toString (aHandler.getHits ()))
             .setAttribute (StatisticsExporter.ATTR_MISSES, Integer.toString (aHandler.getMisses ()));
  }

  public void onTimer (final String sName, final IStatisticsHandlerTimer aHandler)
  {
    if (aHandler.getInvocationCount () > 0)
      m_eRoot.appendElement (StatisticsExporter.ELEMENT_TIMER)
             .setAttribute (StatisticsExporter.ATTR_NAME, sName)
             .setAttribute (StatisticsExporter.ATTR_INVOCATIONCOUNT, Integer.toString (aHandler.getInvocationCount ()))
             .setAttribute (StatisticsExporter.ATTR_MIN, Long.toString (aHandler.getMin ()))
             .setAttribute (StatisticsExporter.ATTR_AVERAGE, Long.toString (aHandler.getAverage ()))
             .setAttribute (StatisticsExporter.ATTR_MAX, Long.toString (aHandler.getMax ()))
             .setAttribute (StatisticsExporter.ATTR_SUM, aHandler.getSum ().toString ());
  }

  public void onKeyedTimer (final String sName, final IStatisticsHandlerKeyedTimer aHandler)
  {
    if (aHandler.getInvocationCount () > 0)
    {
      final IMicroElement eKeyedTimer = m_eRoot.appendElement (StatisticsExporter.ELEMENT_KEYEDTIMER)
                                               .setAttribute (StatisticsExporter.ATTR_NAME, sName)
                                               .setAttribute (StatisticsExporter.ATTR_INVOCATIONCOUNT,
                                                              Integer.toString (aHandler.getInvocationCount ()));
      for (final String sKey : ContainerHelper.getSorted (aHandler.getAllKeys ()))
      {
        eKeyedTimer.appendElement (StatisticsExporter.ELEMENT_KEY)
                   .setAttribute (StatisticsExporter.ATTR_NAME, sKey)
                   .setAttribute (StatisticsExporter.ATTR_INVOCATIONCOUNT,
                                  Integer.toString (aHandler.getInvocationCount (sKey)))
                   .setAttribute (StatisticsExporter.ATTR_MIN, Long.toString (aHandler.getMin (sKey)))
                   .setAttribute (StatisticsExporter.ATTR_AVERAGE, Long.toString (aHandler.getAverage (sKey)))
                   .setAttribute (StatisticsExporter.ATTR_MAX, Long.toString (aHandler.getMax (sKey)))
                   .setAttribute (StatisticsExporter.ATTR_SUM, aHandler.getSum (sKey).toString ());
      }
    }
  }

  public void onSize (final String sName, final IStatisticsHandlerSize aHandler)
  {
    if (aHandler.getInvocationCount () > 0)
      m_eRoot.appendElement (StatisticsExporter.ELEMENT_SIZE)
             .setAttribute (StatisticsExporter.ATTR_NAME, sName)
             .setAttribute (StatisticsExporter.ATTR_INVOCATIONCOUNT, Integer.toString (aHandler.getInvocationCount ()))
             .setAttribute (StatisticsExporter.ATTR_MIN, Long.toString (aHandler.getMin ()))
             .setAttribute (StatisticsExporter.ATTR_AVERAGE, Long.toString (aHandler.getAverage ()))
             .setAttribute (StatisticsExporter.ATTR_MAX, Long.toString (aHandler.getMax ()))
             .setAttribute (StatisticsExporter.ATTR_SUM, aHandler.getSum ().toString ());
  }

  public void onKeyedSize (final String sName, final IStatisticsHandlerKeyedSize aHandler)
  {
    if (aHandler.getInvocationCount () > 0)
    {
      final IMicroElement eKeyedSize = m_eRoot.appendElement (StatisticsExporter.ELEMENT_KEYEDSIZE)
                                              .setAttribute (StatisticsExporter.ATTR_NAME, sName)
                                              .setAttribute (StatisticsExporter.ATTR_INVOCATIONCOUNT,
                                                             Integer.toString (aHandler.getInvocationCount ()));
      for (final String sKey : ContainerHelper.getSorted (aHandler.getAllKeys ()))
      {
        eKeyedSize.appendElement (StatisticsExporter.ELEMENT_KEY)
                  .setAttribute (StatisticsExporter.ATTR_NAME, sKey)
                  .setAttribute (StatisticsExporter.ATTR_INVOCATIONCOUNT,
                                 Integer.toString (aHandler.getInvocationCount (sKey)))
                  .setAttribute (StatisticsExporter.ATTR_MIN, Long.toString (aHandler.getMin (sKey)))
                  .setAttribute (StatisticsExporter.ATTR_AVERAGE, Long.toString (aHandler.getAverage (sKey)))
                  .setAttribute (StatisticsExporter.ATTR_MAX, Long.toString (aHandler.getMax (sKey)))
                  .setAttribute (StatisticsExporter.ATTR_SUM, aHandler.getSum (sKey).toString ());
      }
    }
  }

  public void onCounter (final String sName, final IStatisticsHandlerCounter aHandler)
  {
    if (aHandler.getInvocationCount () > 0)
      m_eRoot.appendElement (StatisticsExporter.ELEMENT_COUNTER)
             .setAttribute (StatisticsExporter.ATTR_NAME, sName)
             .setAttribute (StatisticsExporter.ATTR_INVOCATIONCOUNT, Integer.toString (aHandler.getInvocationCount ()))
             .setAttribute (StatisticsExporter.ATTR_COUNT, Long.toString (aHandler.getCount ()));
  }

  public void onKeyedCounter (final String sName, final IStatisticsHandlerKeyedCounter aHandler)
  {
    if (aHandler.getInvocationCount () > 0)
    {
      final IMicroElement eKeyedCounter = m_eRoot.appendElement (StatisticsExporter.ELEMENT_KEYEDCOUNTER)
                                                 .setAttribute (StatisticsExporter.ATTR_NAME, sName)
                                                 .setAttribute (StatisticsExporter.ATTR_INVOCATIONCOUNT,
                                                                Integer.toString (aHandler.getInvocationCount ()));
      for (final String sKey : ContainerHelper.getSorted (aHandler.getAllKeys ()))
      {
        eKeyedCounter.appendElement (StatisticsExporter.ELEMENT_KEY)
                     .setAttribute (StatisticsExporter.ATTR_NAME, sKey)
                     .setAttribute (StatisticsExporter.ATTR_INVOCATIONCOUNT,
                                    Integer.toString (aHandler.getInvocationCount (sKey)))
                     .setAttribute (StatisticsExporter.ATTR_COUNT, Long.toString (aHandler.getCount (sKey)));
      }
    }
  }
}
