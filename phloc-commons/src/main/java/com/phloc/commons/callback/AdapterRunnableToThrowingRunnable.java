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
package com.phloc.commons.callback;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.phloc.commons.string.ToStringGenerator;

/**
 * A helper class that converts a {@link Runnable} into an
 * {@link IThrowingRunnable}.
 * 
 * @author Philip Helger
 */
@Immutable
public class AdapterRunnableToThrowingRunnable implements IThrowingRunnable
{
  private final Runnable m_aRunnable;

  public AdapterRunnableToThrowingRunnable (@Nonnull final Runnable aRunnable)
  {
    if (aRunnable == null)
      throw new NullPointerException ("runnable");
    m_aRunnable = aRunnable;
  }

  public void run () throws Exception
  {
    m_aRunnable.run ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("runnable", m_aRunnable).toString ();
  }
}
