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
package com.phloc.commons.tree.utils.sort;

import java.util.Comparator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.compare.AbstractPartComparatorComparable;
import com.phloc.commons.compare.ESortOrder;
import com.phloc.commons.tree.IBasicTreeItem;
import com.phloc.commons.tree.simple.ITreeItem;
import com.phloc.commons.tree.withid.ITreeItemWithID;

/**
 * Comparator for sorting {@link IBasicTreeItem} items by their value using an
 * comparable value types.<br>
 * Works for {@link ITreeItem} and {@link ITreeItemWithID}.
 * 
 * @author Philip Helger
 * @param <DATATYPE>
 *        tree item value type
 * @param <ITEMTYPE>
 *        tree item implementation type
 * @deprecated Use {@link ComparatorTreeItemDataComparable} instead
 */
@Deprecated
public class ComparatorTreeItemValueComparable <DATATYPE extends Comparable <? super DATATYPE>, ITEMTYPE extends IBasicTreeItem <DATATYPE, ITEMTYPE>> extends
                                                                                                                                                      AbstractPartComparatorComparable <ITEMTYPE, DATATYPE>
{
  /**
   * Comparator with default sort order.
   */
  public ComparatorTreeItemValueComparable ()
  {
    super ();
  }

  /**
   * Constructor with sort order.
   * 
   * @param eSortOrder
   *        The sort order to use. May not be <code>null</code>.
   */
  public ComparatorTreeItemValueComparable (@Nonnull final ESortOrder eSortOrder)
  {
    super (eSortOrder);
  }

  /**
   * Comparator with default sort order and a nested comparator.
   * 
   * @param aNestedComparator
   *        The nested comparator to be invoked, when the main comparison
   *        resulted in 0.
   */
  public ComparatorTreeItemValueComparable (@Nullable final Comparator <? super ITEMTYPE> aNestedComparator)
  {
    super (aNestedComparator);
  }

  /**
   * Comparator with sort order and a nested comparator.
   * 
   * @param eSortOrder
   *        The sort order to use. May not be <code>null</code>.
   * @param aNestedComparator
   *        The nested comparator to be invoked, when the main comparison
   *        resulted in 0.
   */
  public ComparatorTreeItemValueComparable (@Nonnull final ESortOrder eSortOrder,
                                            @Nullable final Comparator <? super ITEMTYPE> aNestedComparator)
  {
    super (eSortOrder, aNestedComparator);
  }

  @Override
  @Nullable
  protected DATATYPE getPart (@Nonnull final ITEMTYPE aTreeItem)
  {
    return aTreeItem.getData ();
  }
}
