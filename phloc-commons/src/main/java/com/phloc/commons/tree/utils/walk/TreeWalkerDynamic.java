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
import com.phloc.commons.hierarchy.EHierarchyCallbackReturn;
import com.phloc.commons.hierarchy.IHierarchyWalkerDynamicCallback;
import com.phloc.commons.parent.IChildrenProvider;
import com.phloc.commons.parent.impl.ChildrenProviderHasChildren;
import com.phloc.commons.tree.IBasicTree;
import com.phloc.commons.tree.IBasicTreeItem;

/**
 * A specialized walker that iterates all elements in a tree and calls a
 * callback method.
 * 
 * @author Philip Helger
 */
@Immutable
public final class TreeWalkerDynamic
{
  @PresentForCodeCoverage
  @SuppressWarnings ("unused")
  private static final TreeWalkerDynamic s_aInstance = new TreeWalkerDynamic ();

  private TreeWalkerDynamic ()
  {}

  @Nonnull
  private static <DATATYPE, ITEMTYPE extends IBasicTreeItem <DATATYPE, ITEMTYPE>> EHierarchyCallbackReturn _walkTree (@Nonnull final ITEMTYPE aTreeItem,
                                                                                                                      @Nonnull final IChildrenProvider <ITEMTYPE> aChildrenProvider,
                                                                                                                      @Nonnull final IHierarchyWalkerDynamicCallback <? super ITEMTYPE> aCallback)
  {
    // prefix insertion
    final EHierarchyCallbackReturn eRetPrefix = aCallback.onItemBeforeChildren (aTreeItem);

    // call children only if mode is continue
    EHierarchyCallbackReturn eRetChildren = EHierarchyCallbackReturn.CONTINUE;
    if (eRetPrefix == EHierarchyCallbackReturn.CONTINUE && aChildrenProvider.hasChildren (aTreeItem))
    {
      // iterate children
      aCallback.onLevelDown ();
      try
      {
        for (final ITEMTYPE aChildItem : aChildrenProvider.getChildren (aTreeItem))
        {
          // recursive call
          eRetChildren = _walkTree (aChildItem, aChildrenProvider, aCallback);
          if (eRetChildren == EHierarchyCallbackReturn.USE_PARENTS_NEXT_SIBLING)
          {
            // If we don't want the children to be enumerated, break this loop
            // and continue as normal
            eRetChildren = EHierarchyCallbackReturn.CONTINUE;
            break;
          }

          if (eRetChildren == EHierarchyCallbackReturn.STOP_ITERATION)
          {
            // stop iterating and propagate the return code to the root
            break;
          }
        }
      }
      finally
      {
        // callback
        aCallback.onLevelUp ();
      }
    }

    // postfix insertion even if prefix iteration failed
    final EHierarchyCallbackReturn eRetPostfix = aCallback.onItemAfterChildren (aTreeItem);

    // most stringent first
    if (eRetPrefix == EHierarchyCallbackReturn.STOP_ITERATION ||
        eRetChildren == EHierarchyCallbackReturn.STOP_ITERATION ||
        eRetPostfix == EHierarchyCallbackReturn.STOP_ITERATION)
    {
      // stop complete iteration
      return EHierarchyCallbackReturn.STOP_ITERATION;
    }
    if (eRetPrefix == EHierarchyCallbackReturn.USE_PARENTS_NEXT_SIBLING ||
        eRetChildren == EHierarchyCallbackReturn.USE_PARENTS_NEXT_SIBLING ||
        eRetPostfix == EHierarchyCallbackReturn.USE_PARENTS_NEXT_SIBLING)
    {
      // skip children and siblings
      return EHierarchyCallbackReturn.USE_PARENTS_NEXT_SIBLING;
    }

    // continue
    return EHierarchyCallbackReturn.CONTINUE;
  }

  public static <DATATYPE, ITEMTYPE extends IBasicTreeItem <DATATYPE, ITEMTYPE>> void walkTree (@Nonnull final IBasicTree <DATATYPE, ITEMTYPE> aTree,
                                                                                                @Nonnull final IHierarchyWalkerDynamicCallback <? super ITEMTYPE> aCallback)
  {
    walkTree (aTree, new ChildrenProviderHasChildren <ITEMTYPE> (), aCallback);
  }

  public static <DATATYPE, ITEMTYPE extends IBasicTreeItem <DATATYPE, ITEMTYPE>> void walkTree (@Nonnull final IBasicTree <DATATYPE, ITEMTYPE> aTree,
                                                                                                @Nonnull final IChildrenProvider <ITEMTYPE> aChildrenResolver,
                                                                                                @Nonnull final IHierarchyWalkerDynamicCallback <? super ITEMTYPE> aCallback)
  {
    if (aTree == null)
      throw new NullPointerException ("tree");

    walkSubTree (aTree.getRootItem (), aChildrenResolver, aCallback);
  }

  public static <DATATYPE, ITEMTYPE extends IBasicTreeItem <DATATYPE, ITEMTYPE>> void walkSubTree (@Nonnull final ITEMTYPE aTreeItem,
                                                                                                   @Nonnull final IHierarchyWalkerDynamicCallback <? super ITEMTYPE> aCallback)
  {
    walkSubTree (aTreeItem, new ChildrenProviderHasChildren <ITEMTYPE> (), aCallback);
  }

  public static <DATATYPE, ITEMTYPE extends IBasicTreeItem <DATATYPE, ITEMTYPE>> void walkSubTree (@Nonnull final ITEMTYPE aTreeItem,
                                                                                                   @Nonnull final IChildrenProvider <ITEMTYPE> aChildrenProvider,
                                                                                                   @Nonnull final IHierarchyWalkerDynamicCallback <? super ITEMTYPE> aCallback)
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
