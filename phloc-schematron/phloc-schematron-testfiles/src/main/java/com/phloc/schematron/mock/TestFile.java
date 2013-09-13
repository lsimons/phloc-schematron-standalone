/**
 * Copyright (C) 2013 phloc systems
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
package com.phloc.schematron.mock;

import javax.annotation.Nonnull;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.hash.HashCodeGenerator;
import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.string.ToStringGenerator;

/**
 * Represents a single Schematron test file
 * 
 * @author Philip Helger
 */
public class TestFile
{
  private final String m_sParentDirBaseName;
  private final IReadableResource m_aRes;
  // For easier usage only:
  private final String m_sFileBaseName;

  public TestFile (@Nonnull @Nonempty final String sParentDirBaseName,
                   @Nonnull final IReadableResource aRes,
                   @Nonnull @Nonempty final String sFileBaseName)
  {
    if (StringHelper.hasNoText (sParentDirBaseName))
      throw new IllegalArgumentException ("parentDirBaseName");
    if (aRes == null)
      throw new NullPointerException ("res");
    if (StringHelper.hasNoText (sFileBaseName))
      throw new IllegalArgumentException ("fileBaseName");
    m_sParentDirBaseName = sParentDirBaseName;
    m_aRes = aRes;
    m_sFileBaseName = sFileBaseName;
  }

  @Nonnull
  @Nonempty
  public String getParentDirBaseName ()
  {
    return m_sParentDirBaseName;
  }

  @Nonnull
  public IReadableResource getResource ()
  {
    return m_aRes;
  }

  @Nonnull
  @Nonempty
  public String getFileBaseName ()
  {
    return m_sFileBaseName;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final TestFile rhs = (TestFile) o;
    return m_sParentDirBaseName.equals (rhs.m_sParentDirBaseName) && m_aRes.equals (rhs.m_aRes);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sParentDirBaseName).append (m_aRes).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("parentDirBaseName", m_sParentDirBaseName)
                                       .append ("res", m_aRes)
                                       .toString ();
  }
}
