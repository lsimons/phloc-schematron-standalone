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
package com.phloc.commons.format.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.format.IFormatter;

/**
 * A formatter that adds a prefix to a string.
 * 
 * @author Philip Helger
 */
public class StringPrefixFormatter extends StringPrefixAndSuffixFormatter
{
  public StringPrefixFormatter (@Nonnull final String sPrefix)
  {
    super (sPrefix, "");
  }

  /**
   * @deprecated Use {@link #StringPrefixFormatter(IFormatter,String)} instead
   */
  @Deprecated
  public StringPrefixFormatter (@Nonnull final String sPrefix, @Nullable final IFormatter aNestedFormatter)
  {
    this (aNestedFormatter, sPrefix);
  }

  public StringPrefixFormatter (@Nullable final IFormatter aNestedFormatter, @Nonnull final String sPrefix)
  {
    super (aNestedFormatter, sPrefix, "");
  }
}
