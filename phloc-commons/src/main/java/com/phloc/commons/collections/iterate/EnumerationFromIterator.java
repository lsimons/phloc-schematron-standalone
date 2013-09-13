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
package com.phloc.commons.collections.iterate;

import java.util.Enumeration;
import java.util.Iterator;

import javax.annotation.Nonnull;

import com.phloc.commons.string.ToStringGenerator;

/**
 * This is a helper class to create an {@link Enumeration} from an existing
 * {@link Iterator}
 * 
 * @author Philip Helger
 * @param <ELEMENTTYPE>
 *        Type to be enumerated
 */
public final class EnumerationFromIterator <ELEMENTTYPE> implements Enumeration <ELEMENTTYPE>
{
  private final Iterator <ELEMENTTYPE> m_aIter;

  public EnumerationFromIterator (@Nonnull final Iterable <ELEMENTTYPE> aCont)
  {
    this (aCont.iterator ());
  }

  public EnumerationFromIterator (@Nonnull final Iterator <ELEMENTTYPE> aIter)
  {
    if (aIter == null)
      throw new NullPointerException ("iterator");
    m_aIter = aIter;
  }

  public boolean hasMoreElements ()
  {
    return m_aIter.hasNext ();
  }

  public ELEMENTTYPE nextElement ()
  {
    return m_aIter.next ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("iter", m_aIter).toString ();
  }

  @Nonnull
  public static <ELEMENTTYPE> EnumerationFromIterator <ELEMENTTYPE> create (@Nonnull final Iterator <ELEMENTTYPE> aIter)
  {
    return new EnumerationFromIterator <ELEMENTTYPE> (aIter);
  }

  @Nonnull
  public static <ELEMENTTYPE> EnumerationFromIterator <ELEMENTTYPE> create (@Nonnull final Iterable <ELEMENTTYPE> aCont)
  {
    return new EnumerationFromIterator <ELEMENTTYPE> (aCont);
  }
}
