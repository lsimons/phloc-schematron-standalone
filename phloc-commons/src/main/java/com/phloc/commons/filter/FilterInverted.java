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
package com.phloc.commons.filter;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.phloc.commons.hash.HashCodeGenerator;
import com.phloc.commons.string.ToStringGenerator;

/**
 * A filter implementation that inverts the result of another filter.
 * 
 * @author Philip Helger
 * @param <DATATYPE>
 *        The data type to filter
 */
@Immutable
public final class FilterInverted <DATATYPE> implements IFilter <DATATYPE>
{
  private final IFilter <DATATYPE> m_aOriginalFilter;

  public FilterInverted (@Nonnull final IFilter <DATATYPE> aOriginalFilter)
  {
    if (aOriginalFilter == null)
      throw new NullPointerException ("originalFilter");
    m_aOriginalFilter = aOriginalFilter;
  }

  public boolean matchesFilter (final DATATYPE aValue)
  {
    return !m_aOriginalFilter.matchesFilter (aValue);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!(o instanceof FilterInverted <?>))
      return false;
    final FilterInverted <?> rhs = (FilterInverted <?>) o;
    return m_aOriginalFilter.equals (rhs.m_aOriginalFilter);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aOriginalFilter).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("originalFilter", m_aOriginalFilter).toString ();
  }
}
