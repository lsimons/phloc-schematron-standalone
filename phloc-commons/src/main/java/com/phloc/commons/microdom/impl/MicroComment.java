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

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.microdom.EMicroNodeType;
import com.phloc.commons.microdom.IMicroComment;
import com.phloc.commons.microdom.IMicroNode;
import com.phloc.commons.string.ToStringGenerator;

/**
 * Default implementation of the {@link IMicroComment} interface.
 * 
 * @author Philip Helger
 */
public final class MicroComment extends AbstractMicroNode implements IMicroComment
{
  private final MicroDataAware m_aData;

  public MicroComment (@Nonnull final char [] aChars, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    m_aData = new MicroDataAware (aChars, nOfs, nLen);
  }

  public MicroComment (@Nullable final CharSequence sText)
  {
    m_aData = new MicroDataAware (sText);
  }

  private MicroComment (@Nonnull final MicroDataAware aData)
  {
    m_aData = aData.getClone ();
  }

  @Nonnull
  public EMicroNodeType getType ()
  {
    return EMicroNodeType.COMMENT;
  }

  @Nonnull
  @Nonempty
  public String getNodeName ()
  {
    return "#comment";
  }

  @Override
  @Nonnull
  public String getNodeValue ()
  {
    return getData ().toString ();
  }

  @Nonnull
  public CharSequence getData ()
  {
    return m_aData.getData ();
  }

  public void appendData (@Nullable final CharSequence sData)
  {
    m_aData.appendData (sData);
  }

  public void appendData (@Nonnull final char [] aChars, @Nonnegative final int nOfs, @Nonnegative final int nLen)
  {
    m_aData.appendData (aChars, nOfs, nLen);
  }

  public void prependData (@Nullable final CharSequence sData)
  {
    m_aData.prependData (sData);
  }

  public void setData (@Nullable final CharSequence sData)
  {
    m_aData.setData (sData);
  }

  @Nonnull
  public IMicroComment getClone ()
  {
    return new MicroComment (m_aData);
  }

  public boolean isEqualContent (@Nullable final IMicroNode o)
  {
    if (o == this)
      return true;
    if (!(o instanceof MicroComment))
      return false;
    final MicroComment rhs = (MicroComment) o;
    return m_aData.equals (rhs.m_aData);
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("text", getData ()).toString ();
  }
}
