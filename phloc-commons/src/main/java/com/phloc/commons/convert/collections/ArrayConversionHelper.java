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
package com.phloc.commons.convert.collections;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.phloc.commons.annotations.PresentForCodeCoverage;
import com.phloc.commons.collections.ArrayHelper;
import com.phloc.commons.convert.IUnidirectionalConverter;

/**
 * This utility class provides conversions from array objects.
 * 
 * @author Philip Helger
 */
@Immutable
public final class ArrayConversionHelper
{
  @PresentForCodeCoverage
  @SuppressWarnings ("unused")
  private static final ArrayConversionHelper s_aInstance = new ArrayConversionHelper ();

  private ArrayConversionHelper ()
  {}

  @Nonnull
  public static <SRCTYPE, DSTTYPE> DSTTYPE [] getConverted (@Nonnull final SRCTYPE [] aArray,
                                                            @Nonnull final IUnidirectionalConverter <SRCTYPE, DSTTYPE> aConv,
                                                            @Nonnull final Class <DSTTYPE> aDstClass)
  {
    final DSTTYPE [] ret = ArrayHelper.newArray (aDstClass, aArray.length);
    for (int i = 0; i < aArray.length; ++i)
      ret[i] = aConv.convert (aArray[i]);
    return ret;
  }

  @Nonnull
  public static <SRCTYPE, DSTTYPE> DSTTYPE [] newArray (@Nonnull final Collection <? extends SRCTYPE> aList,
                                                        @Nonnull final IUnidirectionalConverter <SRCTYPE, DSTTYPE> aConv,
                                                        @Nonnull final Class <DSTTYPE> aDstClass)
  {
    if (aList == null)
      throw new NullPointerException ("list");
    if (aConv == null)
      throw new NullPointerException ("converter");

    final DSTTYPE [] ret = ArrayHelper.newArray (aDstClass, aList.size ());
    int i = 0;
    for (final SRCTYPE aObj : aList)
      ret[i++] = aConv.convert (aObj);
    return ret;
  }

  @Nonnull
  public static <SRCTYPE, DSTTYPE> DSTTYPE [] newArray (@Nullable final SRCTYPE [] aArray,
                                                        @Nonnull final IUnidirectionalConverter <SRCTYPE, DSTTYPE> aConv,
                                                        @Nonnull final Class <DSTTYPE> aDstClass)
  {
    if (aConv == null)
      throw new NullPointerException ("converter");

    final DSTTYPE [] ret = ArrayHelper.newArray (aDstClass, ArrayHelper.getSize (aArray));
    if (aArray != null)
    {
      int i = 0;
      for (final SRCTYPE aObj : aArray)
        ret[i++] = aConv.convert (aObj);
    }
    return ret;
  }
}
