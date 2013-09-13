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

import static org.junit.Assert.fail;

import java.util.concurrent.Callable;

import org.junit.Test;

import com.phloc.commons.callback.IThrowingRunnable;
import com.phloc.commons.mock.MockException;
import com.phloc.commons.mock.MockRuntimeException;

/**
 * Test class for {@link MainRunner}
 * 
 * @author Philip Helger
 */
public final class MainRunnerTest
{
  @Test
  public void testRunnable ()
  {
    MainRunner.run (new Runnable ()
    {
      public void run ()
      {}
    });

    MainRunner.run (new Runnable ()
    {
      public void run ()
      {
        throw new MockRuntimeException ();
      }
    });

    try
    {
      MainRunner.run ((Runnable) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      MainRunner.run ((Callable <Object>) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testThrowingRunnable ()
  {
    MainRunner.run (new IThrowingRunnable ()
    {
      public void run ()
      {}
    });

    MainRunner.run (new IThrowingRunnable ()
    {
      public void run () throws MockException
      {
        throw new MockException ();
      }
    });

    try
    {
      MainRunner.run ((IThrowingRunnable) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}
