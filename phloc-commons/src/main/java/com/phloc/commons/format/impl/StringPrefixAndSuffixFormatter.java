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
package com.phloc.commons.format.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.format.IFormatter;
import com.phloc.commons.hash.HashCodeGenerator;
import com.phloc.commons.string.ToStringGenerator;

/**
 * A formatter that adds a prefix and/or a suffix to a string.
 * 
 * @author Philip Helger
 */
public class StringPrefixAndSuffixFormatter extends AbstractStringFormatter
{
  private final String m_sPrefix;
  private final String m_sSuffix;

  public StringPrefixAndSuffixFormatter (@Nonnull final String sPrefix, @Nonnull final String sSuffix)
  {
    this (null, sPrefix, sSuffix);
  }

  /**
   * @deprecated Use
   *             {@link #StringPrefixAndSuffixFormatter(IFormatter,String,String)}
   *             instead
   */
  @Deprecated
  public StringPrefixAndSuffixFormatter (@Nonnull final String sPrefix,
                                         @Nonnull final String sSuffix,
                                         @Nullable final IFormatter aNestedFormatter)
  {
    this (aNestedFormatter, sPrefix, sSuffix);
  }

  public StringPrefixAndSuffixFormatter (@Nullable final IFormatter aNestedFormatter,
                                         @Nonnull final String sPrefix,
                                         @Nonnull final String sSuffix)
  {
    super (aNestedFormatter);
    if (sPrefix == null)
      throw new NullPointerException ("prefix");
    if (sSuffix == null)
      throw new NullPointerException ("suffix");
    m_sPrefix = sPrefix;
    m_sSuffix = sSuffix;
  }

  @Override
  protected final String getFormattedValueAsString (@Nullable final Object aValue)
  {
    return m_sPrefix + getValueAsString (aValue) + m_sSuffix;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!(o instanceof StringPrefixAndSuffixFormatter))
      return false;
    final StringPrefixAndSuffixFormatter rhs = (StringPrefixAndSuffixFormatter) o;
    return m_sPrefix.equals (rhs.m_sPrefix) && m_sSuffix.equals (rhs.m_sSuffix);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sPrefix).append (m_sSuffix).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("prefix", m_sPrefix)
                            .append ("suffix", m_sSuffix)
                            .toString ();
  }
}
