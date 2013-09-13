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
package com.phloc.commons.name;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.collections.ArrayHelper;
import com.phloc.commons.string.ToStringGenerator;
import com.phloc.commons.text.impl.TextFormatter;

/**
 * A special implementation of {@link IHasDisplayText} that encapsulates
 * arguments to be put into the message.
 * 
 * @author Philip Helger
 */
public final class HasDisplayTextWithArgs implements IHasDisplayText
{
  private final IHasDisplayText m_aParentText;
  private final Object [] m_aArgs;

  public HasDisplayTextWithArgs (@Nonnull final IHasDisplayText aParentText, @Nonnull @Nonempty final Object... aArgs)
  {
    if (aParentText == null)
      throw new NullPointerException ("parentText");
    if (ArrayHelper.isEmpty (aArgs))
      throw new IllegalArgumentException ("arguments may not be empty");
    m_aParentText = aParentText;
    m_aArgs = aArgs;
  }

  @Nullable
  public String getDisplayText (@Nonnull final Locale aContentLocale)
  {
    final String sText = m_aParentText.getDisplayText (aContentLocale);
    return TextFormatter.getFormattedText (sText, m_aArgs);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("parentText", m_aParentText).append ("args", m_aArgs).toString ();
  }
}
