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
package com.phloc.math.graph;

import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.annotations.MustImplementEqualsAndHashcode;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.state.EChange;

/**
 * Base interface for a single graph node.
 * 
 * @author Philip Helger
 */
@MustImplementEqualsAndHashcode
public interface IBaseGraphNode <N extends IBaseGraphNode <N, R>, R extends IBaseGraphRelation <N, R>> extends
                                                                                                       IBaseGraphObject
{
  /**
   * Check if this graph node is directly connected to the passed node, either
   * via an incoming or via an outgoing relation.<br>
   * This is the same as calling
   * <code>isFromNode(aNode) || isToNode(aNode)</code>
   * 
   * @param aNode
   *        The node to be checked. May be <code>null</code>.
   * @return <code>true</code> if is connected, <code>false</code> if not
   */
  boolean isConnectedWith (@Nullable N aNode);

  /**
   * Find the relation from this node to the passed node.
   * 
   * @param aNode
   *        The to node to use. May be <code>null</code>.
   * @return <code>null</code> if there exists no relation between this node and
   *         the passed node.
   */
  @Nullable
  R getRelation (@Nullable N aNode);

  /**
   * Check if this node has any relations.
   * 
   * @return <code>true</code> if this node has at least one incoming or
   *         outgoing relation.
   */
  boolean hasRelations ();

  /**
   * @return A non-negative amount of all incoming and outgoing relations.
   *         Always &ge; 0.
   */
  @Nonnegative
  int getRelationCount ();

  /**
   * @return A container with all incoming and outgoing relations. Never
   *         <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  Set <R> getAllRelations ();

  /**
   * @return A container with the IDs of all incoming and outgoing relations.
   *         Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  Set <String> getAllRelationIDs ();

  /**
   * @return A container with all nodes directly connected to this node's
   *         relations. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  Set <N> getAllRelatedNodes ();

  /**
   * @return A container with the IDs of all nodes directly connected to this
   *         node's relations. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  Set <String> getAllRelatedNodeIDs ();

  /**
   * Remove all relations of this node.
   * 
   * @return {@link EChange}
   */
  @Nonnull
  EChange removeAllRelations ();
}
