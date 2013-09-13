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
package com.phloc.commons.utils;

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.annotations.PresentForCodeCoverage;
import com.phloc.commons.callback.AdapterRunnableToCallable;
import com.phloc.commons.callback.IThrowingRunnable;
import com.phloc.commons.stats.IStatisticsHandlerTimer;
import com.phloc.commons.stats.StatisticsManager;
import com.phloc.commons.timing.StopWatch;

/**
 * A dummy main runner with some basic setup. Structured a bit similar to the
 * Thread class.
 * 
 * @author Philip Helger
 */
public final class MainRunner
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (MainRunner.class);
  private static final IStatisticsHandlerTimer s_aTimerHdl = StatisticsManager.getTimerHandler (MainRunner.class);

  @PresentForCodeCoverage
  @SuppressWarnings ("unused")
  private static final MainRunner s_aInstance = new MainRunner ();

  private MainRunner ()
  {}

  public static void run (@Nonnull final IThrowingRunnable aRunnable)
  {
    if (aRunnable == null)
      throw new NullPointerException ("runnable");

    final StopWatch aSW = new StopWatch (true);
    try
    {
      aRunnable.run ();
    }
    catch (final Throwable t)
    {
      s_aLogger.error ("Error running application", t);
    }
    finally
    {
      s_aTimerHdl.addTime (aSW.stopAndGetMillis ());
    }
  }

  public static void run (@Nonnull final Runnable aRunnable)
  {
    run (AdapterRunnableToCallable.createAdapter (aRunnable));
  }

  @Nullable
  public static <DATATYPE> DATATYPE run (@Nonnull final Callable <DATATYPE> aCallable)
  {
    if (aCallable == null)
      throw new NullPointerException ("runnable");

    final StopWatch aSW = new StopWatch (true);
    try
    {
      return aCallable.call ();
    }
    catch (final Throwable t)
    {
      s_aLogger.error ("Error running application", t);
      return null;
    }
    finally
    {
      s_aTimerHdl.addTime (aSW.stopAndGetMillis ());
    }
  }
}
