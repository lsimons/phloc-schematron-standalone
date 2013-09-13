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
package com.phloc.commons.xml.transform;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.xml.transform.ErrorListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.annotations.CodingStyleguideUnaware;
import com.phloc.commons.error.IResourceError;
import com.phloc.commons.log.LogUtils;

/**
 * java.xml.transform error listener that simply logs data to a logger.
 * 
 * @author Philip Helger
 */
@Immutable
@CodingStyleguideUnaware ("logger too visible by purpose")
public class LoggingTransformErrorListener extends AbstractTransformErrorListener
{
  protected static final Logger s_aLogger = LoggerFactory.getLogger (LoggingTransformErrorListener.class);

  private final Locale m_aDisplayLocale;

  public LoggingTransformErrorListener (@Nonnull final Locale aDisplayLocale)
  {
    this (null, aDisplayLocale);
  }

  public LoggingTransformErrorListener (@Nullable final ErrorListener aWrappedErrorListener,
                                        @Nonnull final Locale aDisplayLocale)
  {
    super (aWrappedErrorListener);
    if (aDisplayLocale == null)
      throw new NullPointerException ("displayLocale");
    m_aDisplayLocale = aDisplayLocale;
  }

  @Override
  protected void internalLog (@Nonnull final IResourceError aResError)
  {
    LogUtils.log (s_aLogger, aResError.getErrorLevel (), aResError.getAsString (m_aDisplayLocale));
  }
}
