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
package com.phloc.commons.log;

import java.util.Date;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.error.EErrorLevel;
import com.phloc.commons.error.IHasErrorLevel;
import com.phloc.commons.state.IErrorIndicator;
import com.phloc.commons.state.ISuccessIndicator;
import com.phloc.commons.string.ToStringGenerator;

/**
 * Represents a single log message.
 * 
 * @author Philip Helger
 */
@Immutable
public final class LogMessage implements IHasErrorLevel, ISuccessIndicator, IErrorIndicator
{
  private final Date m_aIssueDT;
  private final EErrorLevel m_eErrorLevel;
  private final Object m_aMsg;
  private final Throwable m_aThrowable;

  public LogMessage (@Nonnull final EErrorLevel eLevel, @Nonnull final Object aMsg, @Nullable final Throwable aThrowable)
  {
    if (eLevel == null)
      throw new NullPointerException ("level");
    if (aMsg == null)
      throw new NullPointerException ("msg");

    m_aIssueDT = new Date ();
    m_eErrorLevel = eLevel;
    m_aMsg = aMsg;
    m_aThrowable = aThrowable;
  }

  @Nonnull
  @ReturnsMutableCopy
  public Date getIssueDateTime ()
  {
    // Return a copy
    return (Date) m_aIssueDT.clone ();
  }

  @Nonnull
  public EErrorLevel getErrorLevel ()
  {
    return m_eErrorLevel;
  }

  @Nonnull
  public Object getMessage ()
  {
    return m_aMsg;
  }

  @Nullable
  public Throwable getThrowable ()
  {
    return m_aThrowable;
  }

  public boolean isSuccess ()
  {
    return m_eErrorLevel.isSuccess ();
  }

  public boolean isFailure ()
  {
    return m_eErrorLevel.isFailure ();
  }

  public boolean isError ()
  {
    return m_eErrorLevel.isError ();
  }

  public boolean isNoError ()
  {
    return m_eErrorLevel.isNoError ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("issueDT", m_aIssueDT)
                                       .append ("errorLevel", m_eErrorLevel)
                                       .append ("msg", m_aMsg)
                                       .appendIfNotNull ("throwable", m_aThrowable)
                                       .toString ();
  }
}
