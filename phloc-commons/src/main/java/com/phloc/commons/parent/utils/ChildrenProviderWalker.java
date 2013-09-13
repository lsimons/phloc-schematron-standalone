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
package com.phloc.commons.parent.utils;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.phloc.commons.hierarchy.IHierarchyWalkerCallback;
import com.phloc.commons.parent.IChildrenProvider;

/**
 * Recursively visit all children provided by a given {@link IChildrenProvider}
 * and call a callback for each visited child.
 * 
 * @author Philip Helger
 * @param <CHILDTYPE>
 *        The type of children to walk
 */
@Immutable
public final class ChildrenProviderWalker <CHILDTYPE>
{
  private final IChildrenProvider <CHILDTYPE> m_aChildrenProvider;
  private final IHierarchyWalkerCallback <CHILDTYPE> m_aCallback;

  public ChildrenProviderWalker (@Nonnull final IChildrenProvider <CHILDTYPE> aChildrenProvider,
                                 @Nonnull final IHierarchyWalkerCallback <CHILDTYPE> aCallback)
  {
    if (aChildrenProvider == null)
      throw new NullPointerException ("childProvider");
    if (aCallback == null)
      throw new NullPointerException ("callback");
    m_aChildrenProvider = aChildrenProvider;
    m_aCallback = aCallback;
  }

  private void _walkRecursive (@Nonnull final CHILDTYPE aObject)
  {
    // Only the root level is null -> otherwise we end up in a potentially
    // endless recursion
    if (aObject == null)
      throw new NullPointerException ("object");

    // prefix insertion
    m_aCallback.onItemBeforeChildren (aObject);

    // call children only if mode is continue
    if (m_aChildrenProvider.hasChildren (aObject))
    {
      // iterate children
      m_aCallback.onLevelDown ();
      try
      {
        for (final CHILDTYPE aChildObject : m_aChildrenProvider.getChildren (aObject))
        {
          // recursive call
          _walkRecursive (aChildObject);
        }
      }
      finally
      {
        // callback
        m_aCallback.onLevelUp ();
      }
    }

    // postfix insertion even if prefix iteration failed
    m_aCallback.onItemAfterChildren (aObject);
  }

  public void walk ()
  {
    m_aCallback.begin ();
    try
    {
      // null == root level
      if (m_aChildrenProvider.hasChildren (null))
        for (final CHILDTYPE aRootChild : m_aChildrenProvider.getChildren (null))
          _walkRecursive (aRootChild);
    }
    finally
    {
      m_aCallback.end ();
    }
  }

  public void walkSub (@Nonnull final CHILDTYPE aObject)
  {
    if (aObject == null)
      throw new NullPointerException ("object");

    m_aCallback.begin ();
    try
    {
      _walkRecursive (aObject);
    }
    finally
    {
      m_aCallback.end ();
    }
  }

  public static <CHILDTYPE> void walkProvider (@Nonnull final IChildrenProvider <CHILDTYPE> aChildProvider,
                                               @Nonnull final IHierarchyWalkerCallback <CHILDTYPE> aCallback)
  {
    new ChildrenProviderWalker <CHILDTYPE> (aChildProvider, aCallback).walk ();
  }

  public static <CHILDTYPE> void walkSubProvider (@Nonnull final CHILDTYPE aObject,
                                                  @Nonnull final IChildrenProvider <CHILDTYPE> aChildProvider,
                                                  @Nonnull final IHierarchyWalkerCallback <CHILDTYPE> aCallback)
  {
    new ChildrenProviderWalker <CHILDTYPE> (aChildProvider, aCallback).walkSub (aObject);
  }
}
