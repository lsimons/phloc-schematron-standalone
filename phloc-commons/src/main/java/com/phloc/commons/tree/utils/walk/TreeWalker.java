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
package com.phloc.commons.tree.utils.walk;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.phloc.commons.annotations.PresentForCodeCoverage;
import com.phloc.commons.hierarchy.IHierarchyWalkerCallback;
import com.phloc.commons.parent.IChildrenProvider;
import com.phloc.commons.parent.impl.ChildrenProviderHasChildren;
import com.phloc.commons.tree.IBasicTree;
import com.phloc.commons.tree.IBasicTreeItem;

/**
 * Iterate all nodes of a tree, or a tree element using a custom callback
 * mechanism.
 * 
 * @author Philip Helger
 */
@Immutable
public final class TreeWalker
{
  @PresentForCodeCoverage
  @SuppressWarnings ("unused")
  private static final TreeWalker s_aInstance = new TreeWalker ();

  private TreeWalker ()
  {}

  private static <DATATYPE, ITEMTYPE extends IBasicTreeItem <DATATYPE, ITEMTYPE>> void _walkTree (@Nonnull final ITEMTYPE aTreeItem,
                                                                                                  @Nonnull final IChildrenProvider <ITEMTYPE> aChildrenProvider,
                                                                                                  @Nonnull final IHierarchyWalkerCallback <? super ITEMTYPE> aCallback)
  {
    aCallback.onItemBeforeChildren (aTreeItem);
    if (aChildrenProvider.hasChildren (aTreeItem))
      for (final ITEMTYPE aChildItem : aChildrenProvider.getChildren (aTreeItem))
      {
        aCallback.onLevelDown ();
        // recursive call
        _walkTree (aChildItem, aChildrenProvider, aCallback);
        aCallback.onLevelUp ();
      }
    aCallback.onItemAfterChildren (aTreeItem);
  }

  public static <DATATYPE, ITEMTYPE extends IBasicTreeItem <DATATYPE, ITEMTYPE>> void walkTree (@Nonnull final IBasicTree <DATATYPE, ITEMTYPE> aTree,
                                                                                                @Nonnull final IHierarchyWalkerCallback <? super ITEMTYPE> aCallback)
  {
    walkTree (aTree, new ChildrenProviderHasChildren <ITEMTYPE> (), aCallback);
  }

  public static <DATATYPE, ITEMTYPE extends IBasicTreeItem <DATATYPE, ITEMTYPE>> void walkTree (@Nonnull final IBasicTree <DATATYPE, ITEMTYPE> aTree,
                                                                                                @Nonnull final IChildrenProvider <ITEMTYPE> aChildrenProvider,
                                                                                                @Nonnull final IHierarchyWalkerCallback <? super ITEMTYPE> aCallback)
  {
    if (aTree == null)
      throw new NullPointerException ("tree");

    walkSubTree (aTree.getRootItem (), aChildrenProvider, aCallback);
  }

  public static <DATATYPE, ITEMTYPE extends IBasicTreeItem <DATATYPE, ITEMTYPE>> void walkSubTree (@Nonnull final ITEMTYPE aTreeItem,
                                                                                                   @Nonnull final IHierarchyWalkerCallback <? super ITEMTYPE> aCallback)
  {
    walkSubTree (aTreeItem, new ChildrenProviderHasChildren <ITEMTYPE> (), aCallback);
  }

  public static <DATATYPE, ITEMTYPE extends IBasicTreeItem <DATATYPE, ITEMTYPE>> void walkSubTree (@Nonnull final ITEMTYPE aTreeItem,
                                                                                                   @Nonnull final IChildrenProvider <ITEMTYPE> aChildrenProvider,
                                                                                                   @Nonnull final IHierarchyWalkerCallback <? super ITEMTYPE> aCallback)
  {
    if (aTreeItem == null)
      throw new NullPointerException ("treeItem");
    if (aChildrenProvider == null)
      throw new NullPointerException ("childrenProvider");
    if (aCallback == null)
      throw new NullPointerException ("callback");

    aCallback.begin ();
    try
    {
      if (aChildrenProvider.hasChildren (aTreeItem))
        for (final ITEMTYPE aChildItem : aChildrenProvider.getChildren (aTreeItem))
          _walkTree (aChildItem, aChildrenProvider, aCallback);
    }
    finally
    {
      aCallback.end ();
    }
  }
}
