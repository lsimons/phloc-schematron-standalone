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
package com.phloc.commons.parent.impl;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.parent.IChildrenProvider;
import com.phloc.commons.parent.IHasChildren;

/**
 * An implementation of the {@link IChildrenProvider} interface that works with
 * all types that implement {@link IHasChildren}. It automatically sorts the
 * returned children by the specified comparator.
 * 
 * @author Philip Helger
 * @param <CHILDTYPE>
 *        The data type of the child objects.
 */
@Immutable
public class ChildrenProviderHasChildrenSorting <CHILDTYPE extends IHasChildren <CHILDTYPE>> extends
                                                                                             ChildrenProviderHasChildren <CHILDTYPE>
{
  private final Comparator <? super CHILDTYPE> m_aComparator;

  public ChildrenProviderHasChildrenSorting (@Nonnull final Comparator <? super CHILDTYPE> aComparator)
  {
    if (aComparator == null)
      throw new NullPointerException ("comparator");
    m_aComparator = aComparator;
  }

  @Override
  @Nullable
  public List <? extends CHILDTYPE> getChildren (@Nullable final CHILDTYPE aCurrent)
  {
    // Get all children of the passed element
    final Collection <? extends CHILDTYPE> ret = aCurrent == null ? null : aCurrent.getChildren ();

    // If there is anything to sort do it now
    return ret == null ? null : ContainerHelper.getSorted (ret, m_aComparator);
  }
}
