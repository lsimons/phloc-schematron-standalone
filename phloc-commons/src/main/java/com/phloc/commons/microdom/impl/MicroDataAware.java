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
package com.phloc.commons.microdom.impl;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.ICloneable;
import com.phloc.commons.equals.EqualsUtils;
import com.phloc.commons.hash.HashCodeGenerator;
import com.phloc.commons.microdom.IMicroDataAware;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.string.ToStringGenerator;

/**
 * Default implementation of the {@link IMicroDataAware} interface.
 * 
 * @author Philip Helger
 */
final class MicroDataAware implements IMicroDataAware, ICloneable <MicroDataAware>
{
  private final StringBuilder m_aSB;

  public MicroDataAware (@Nonnull final char [] aChars, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    m_aSB = new StringBuilder (nLen + 16).append (aChars, nOfs, nLen);
  }

  public MicroDataAware (@Nullable final CharSequence aText)
  {
    if (StringHelper.hasNoText (aText))
      m_aSB = new StringBuilder ();
    else
      m_aSB = new StringBuilder (aText);
  }

  @Nonnull
  public StringBuilder getData ()
  {
    return m_aSB;
  }

  public void setData (@Nullable final CharSequence aData)
  {
    m_aSB.setLength (0);
    m_aSB.append (aData);
  }

  public void appendData (@Nullable final CharSequence sData)
  {
    m_aSB.append (sData);
  }

  public void appendData (@Nonnull final char [] aChars, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    m_aSB.append (aChars, nOfs, nLen);
  }

  public void prependData (@Nullable final CharSequence sData)
  {
    m_aSB.insert (0, sData);
  }

  @Nonnull
  public MicroDataAware getClone ()
  {
    return new MicroDataAware (m_aSB);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!(o instanceof MicroDataAware))
      return false;
    final MicroDataAware rhs = (MicroDataAware) o;
    return EqualsUtils.equals (m_aSB, rhs.m_aSB);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aSB).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("data", m_aSB).toString ();
  }
}
