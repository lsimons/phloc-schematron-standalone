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
package com.phloc.commons.collections.trove;

import gnu.trove.impl.Constants;

import javax.annotation.concurrent.Immutable;

import com.phloc.commons.annotations.PresentForCodeCoverage;

/**
 * Some utility methods for Trove.
 * 
 * @author helger
 */
@Immutable
public final class TroveUtils
{
  @SuppressWarnings ("unused")
  @PresentForCodeCoverage
  private static final TroveUtils s_aInstance = new TroveUtils ();

  private TroveUtils ()
  {}

  public static boolean isContained (final byte n)
  {
    return n != Constants.DEFAULT_BYTE_NO_ENTRY_VALUE;
  }

  public static boolean isNotContained (final byte n)
  {
    return n == Constants.DEFAULT_BYTE_NO_ENTRY_VALUE;
  }

  public static boolean isContained (final char c)
  {
    return c != Constants.DEFAULT_CHAR_NO_ENTRY_VALUE;
  }

  public static boolean isNotContained (final char c)
  {
    return c == Constants.DEFAULT_CHAR_NO_ENTRY_VALUE;
  }

  public static boolean isContained (final double d)
  {
    return d != Constants.DEFAULT_DOUBLE_NO_ENTRY_VALUE;
  }

  public static boolean isNotContained (final double d)
  {
    return d == Constants.DEFAULT_DOUBLE_NO_ENTRY_VALUE;
  }

  public static boolean isContained (final float f)
  {
    return f != Constants.DEFAULT_FLOAT_NO_ENTRY_VALUE;
  }

  public static boolean isNotContained (final float f)
  {
    return f == Constants.DEFAULT_FLOAT_NO_ENTRY_VALUE;
  }

  public static boolean isContained (final int n)
  {
    return n != Constants.DEFAULT_INT_NO_ENTRY_VALUE;
  }

  public static boolean isNotContained (final int n)
  {
    return n == Constants.DEFAULT_INT_NO_ENTRY_VALUE;
  }

  public static boolean isContained (final long n)
  {
    return n != Constants.DEFAULT_LONG_NO_ENTRY_VALUE;
  }

  public static boolean isNotContained (final long n)
  {
    return n == Constants.DEFAULT_BYTE_NO_ENTRY_VALUE;
  }

  public static boolean isContained (final short n)
  {
    return n != Constants.DEFAULT_SHORT_NO_ENTRY_VALUE;
  }

  public static boolean isNotContained (final short n)
  {
    return n == Constants.DEFAULT_SHORT_NO_ENTRY_VALUE;
  }
}
