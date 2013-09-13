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
package com.phloc.commons.format;

import javax.annotation.Nonnull;

import com.phloc.commons.string.ToStringGenerator;

/**
 * Implementation of the {@link IFormatterProvider} interface always returning a
 * constant value.
 * 
 * @author Philip Helger
 */
public final class FormatterProviderConstant implements IFormatterProvider
{
  private final IFormatter m_aFormatter;

  public FormatterProviderConstant (@Nonnull final IFormatter aFormatter)
  {
    if (aFormatter == null)
      throw new NullPointerException ("formatter");
    m_aFormatter = aFormatter;
  }

  @Nonnull
  public IFormatter getFormatter ()
  {
    return m_aFormatter;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("formatter", m_aFormatter).toString ();
  }
}
