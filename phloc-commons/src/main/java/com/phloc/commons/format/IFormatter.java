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
import javax.annotation.Nullable;

/**
 * A simple formatter interface that may be used to format arbitrary objects to
 * a string.
 * 
 * @author Philip Helger
 */
public interface IFormatter
{
  /**
   * Convert the passed value to a formatted string according to the pattern.
   * 
   * @param aValue
   *        The source value to be formatted. May be <code>null</code>.
   * @return The formatted string. Never <code>null</code>.
   * @throws IllegalArgumentException
   *         if the formatter does not understand the object
   */
  @Nonnull
  String getFormattedValue (@Nullable Object aValue);
}
