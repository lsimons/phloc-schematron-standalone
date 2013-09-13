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
package com.phloc.commons.collections.flags;

import java.util.Collection;
import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.hash.HashCodeGenerator;
import com.phloc.commons.string.ToStringGenerator;

/**
 * Default implementation of the {@link IReadonlyFlagContainer} based on a hash
 * set.
 * 
 * @author Philip Helger
 */
@NotThreadSafe
public final class ReadonlyFlagContainer implements IReadonlyFlagContainer
{
  private final Set <String> m_aFlags;

  public ReadonlyFlagContainer (@Nonnull final Collection <String> aValues)
  {
    if (aValues == null)
      throw new NullPointerException ("Values");
    m_aFlags = ContainerHelper.newSet (aValues);
  }

  public ReadonlyFlagContainer (@Nonnull final String... aValues)
  {
    if (aValues == null)
      throw new NullPointerException ("Values");
    m_aFlags = ContainerHelper.newSet (aValues);
  }

  public ReadonlyFlagContainer (@Nonnull final IReadonlyFlagContainer aCont)
  {
    if (aCont == null)
      throw new NullPointerException ("cont");
    m_aFlags = aCont.getAllFlags ();
  }

  public boolean containsFlag (@Nullable final String sName)
  {
    return m_aFlags.contains (sName);
  }

  @Nonnull
  @ReturnsMutableCopy
  public Set <String> getAllFlags ()
  {
    return ContainerHelper.newSet (m_aFlags);
  }

  @Nonnegative
  public int getFlagCount ()
  {
    return m_aFlags.size ();
  }

  public boolean containsNoFlag ()
  {
    return m_aFlags.isEmpty ();
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!(o instanceof ReadonlyFlagContainer))
      return false;
    final ReadonlyFlagContainer rhs = (ReadonlyFlagContainer) o;
    return m_aFlags.equals (rhs.m_aFlags);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aFlags).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("flags", m_aFlags).toString ();
  }
}
