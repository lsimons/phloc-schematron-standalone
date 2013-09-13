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
package com.phloc.commons.gfx;

import java.io.Serializable;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.phloc.commons.hash.HashCodeGenerator;
import com.phloc.commons.math.IHasWidthAndHeight;
import com.phloc.commons.math.MathHelper;
import com.phloc.commons.string.ToStringGenerator;

/**
 * This class represents an object having width and height.
 *
 * @author Philip Helger
 */
@Immutable
public final class ScalableSize implements IHasWidthAndHeight, Serializable
{
  private final int m_nWidth;
  private final int m_nHeight;

  public ScalableSize (@Nonnull final IHasWidthAndHeight aObj)
  {
    this (aObj.getWidth (), aObj.getHeight ());
  }

  public ScalableSize (@Nonnegative final int nWidth, @Nonnegative final int nHeight)
  {
    if (nWidth < 0)
      throw new IllegalArgumentException ("Width may not be < 0: " + nWidth);
    if (nHeight < 0)
      throw new IllegalArgumentException ("Height may not be < 0: " + nHeight);
    m_nWidth = nWidth;
    m_nHeight = nHeight;
  }

  @Nonnegative
  public int getWidth ()
  {
    return m_nWidth;
  }

  @Nonnegative
  public int getHeight ()
  {
    return m_nHeight;
  }

  /**
   * Return the scaled width and height relative to a maximum size.
   *
   * @param nMaxWidth
   *        Maximum width. Must be &gt; 0.
   * @param nMaxHeight
   *        Maximum height. Must be &gt; 0.
   * @return An array with 2 elements, where the first element is the width, and
   *         the second is the height.
   */
  @Nonnull
  @CheckReturnValue
  public ScalableSize getBestMatchingSize (@Nonnegative final int nMaxWidth, @Nonnegative final int nMaxHeight)
  {
    if (nMaxWidth < 1)
      throw new IllegalArgumentException ("Passed width value is not allowed: " + nMaxWidth);
    if (nMaxHeight < 1)
      throw new IllegalArgumentException ("Passed height value is not allowed: " + nMaxHeight);

    final double dRelWidth = MathHelper.getDividedDouble (m_nWidth, nMaxWidth);
    final double dRelHeight = MathHelper.getDividedDouble (m_nHeight, nMaxHeight);
    if (dRelWidth > dRelHeight)
    {
      if (m_nWidth > nMaxWidth)
        return new ScalableSize (nMaxWidth, (int) (m_nHeight / dRelWidth));
    }
    else
    {
      if (m_nHeight > nMaxHeight)
        return new ScalableSize ((int) (m_nWidth / dRelHeight), nMaxHeight);
    }
    return this;
  }

  @Nonnull
  @CheckReturnValue
  public ScalableSize getScaledToWidth (@Nonnegative final int nNewWidth)
  {
    if (nNewWidth < 1)
      throw new IllegalArgumentException ("Passed width value is not allowed: " + nNewWidth);
    if (m_nWidth == nNewWidth)
      return this;
    final double dMultFactory = MathHelper.getDividedDouble (nNewWidth, m_nWidth);
    return new ScalableSize (nNewWidth, (int) (m_nHeight * dMultFactory));
  }

  @Nonnull
  @CheckReturnValue
  public ScalableSize getScaledToHeight (@Nonnegative final int nNewHeight)
  {
    if (nNewHeight < 1)
      throw new IllegalArgumentException ("Passed height value is not allowed: " + nNewHeight);
    if (m_nHeight == nNewHeight)
      return this;
    final double dMultFactory = MathHelper.getDividedDouble (nNewHeight, m_nHeight);
    return new ScalableSize ((int) (m_nWidth * dMultFactory), nNewHeight);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!(o instanceof ScalableSize))
      return false;
    final ScalableSize rhs = (ScalableSize) o;
    return m_nWidth == rhs.m_nWidth && m_nHeight == rhs.m_nHeight;
  }

  @Override
  public int hashCode ()
  {
    //noinspection SuspiciousNameCombination
    return new HashCodeGenerator (this).append (m_nWidth).append (m_nHeight).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("width", m_nWidth).append ("height", m_nHeight).toString ();
  }
}
