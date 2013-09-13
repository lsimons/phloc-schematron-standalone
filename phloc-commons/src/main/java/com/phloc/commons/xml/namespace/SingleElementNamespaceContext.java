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
package com.phloc.commons.xml.namespace;

import java.util.Iterator;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.xml.XMLConstants;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.collections.iterate.SingleElementIterator;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.string.ToStringGenerator;

/**
 * Represents a namespace context having exactly 1 item.
 * 
 * @author Philip Helger
 */
@Immutable
public class SingleElementNamespaceContext extends AbstractNamespaceContext
{
  private final String m_sPrefix;
  private final String m_sNamespaceURI;

  /**
   * Create a namespace context with the default (empty) prefix
   * 
   * @param sNamespaceURI
   *        The namespace URI to use. May neither be <code>null</code> nor
   *        empty.
   */
  public SingleElementNamespaceContext (@Nonnull @Nonempty final String sNamespaceURI)
  {
    this (XMLConstants.DEFAULT_NS_PREFIX, sNamespaceURI);
  }

  public SingleElementNamespaceContext (@Nonnull final String sPrefix, @Nonnull @Nonempty final String sNamespaceURI)
  {
    if (sPrefix == null)
      throw new NullPointerException ("prefix");
    if (StringHelper.hasNoText (sNamespaceURI))
      throw new IllegalArgumentException ("namespaceURI may not be empty");

    m_sPrefix = sPrefix;
    m_sNamespaceURI = sNamespaceURI;
  }

  @Override
  @Nullable
  public String getDefaultNamespaceURI ()
  {
    return m_sPrefix.equals (XMLConstants.DEFAULT_NS_PREFIX) ? m_sNamespaceURI : null;
  }

  @Override
  @Nullable
  protected Iterator <String> getCustomPrefixes (@Nullable final String sNamespaceURI)
  {
    return m_sNamespaceURI.equals (sNamespaceURI) ? SingleElementIterator.create (m_sPrefix) : null;
  }

  @Override
  @Nullable
  protected String getCustomPrefix (@Nullable final String sNamespaceURI)
  {
    return m_sNamespaceURI.equals (sNamespaceURI) ? m_sPrefix : null;
  }

  @Override
  @Nullable
  protected String getCustomNamespaceURI (@Nullable final String sPrefix)
  {
    return m_sPrefix.equals (sPrefix) ? m_sNamespaceURI : null;
  }

  @Nonnull
  @ReturnsMutableCopy
  public Map <String, String> getPrefixToNamespaceURIMap ()
  {
    return ContainerHelper.newMap (m_sPrefix, m_sNamespaceURI);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("prefix", m_sPrefix)
                                       .append ("namespaceURI", m_sNamespaceURI)
                                       .toString ();
  }
}
