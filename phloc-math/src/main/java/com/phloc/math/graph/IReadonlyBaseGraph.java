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

import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.math.matrix.Matrix;

/**
 * Base interface for a read-only graph.
 * 
 * @author Philip Helger
 */
public interface IReadonlyBaseGraph <N extends IBaseGraphNode <N, R>, R extends IBaseGraphRelation <N, R>> extends
                                                                                                           IBaseGraphObject
{
  /**
   * @return The number of nodes currently in the graph. Always &ge; 0.
   */
  @Nonnegative
  int getNodeCount ();

  /**
   * Find the graph node with the specified ID.
   * 
   * @param sID
   *        The ID to be searched. Maybe <code>null</code>.
   * @return <code>null</code> if no such graph node exists in this graph.
   */
  @Nullable
  N getNodeOfID (@Nullable String sID);

  /**
   * @return A non-<code>null</code> collection of the nodes in this graph, in
   *         arbitrary order!
   */
  @Nonnull
  @ReturnsMutableCopy
  Map <String, N> getAllNodes ();

  /**
   * @return A non-<code>null</code> set of all the node IDs in this graph, in
   *         arbitrary order!
   */
  @Nonnull
  @ReturnsMutableCopy
  Set <String> getAllNodeIDs ();

  /**
   * @return A non-<code>null</code> collection of the relations in this graph,
   *         in arbitrary order!
   */
  @Nonnull
  @ReturnsMutableCopy
  Map <String, R> getAllRelations ();

  /**
   * @return A non-<code>null</code> set of all the relation IDs in this graph,
   *         in arbitrary order!
   */
  @Nonnull
  @ReturnsMutableCopy
  Set <String> getAllRelationIDs ();

  /**
   * Check if this graph contains cycles. An example for a cycle is e.g. if
   * <code>NodeA</code> has an outgoing relation to <code>NodeB</code>,
   * <code>NodeB</code> has an outgoing relation to <code>NodeC</code> and
   * finally <code>NodeC</code> has an outgoing relation to <code>NodeA</code>.
   * 
   * @return <code>true</code> if this graph contains at least one cycle,
   *         <code>false</code> if this graph is cycle-free.
   */
  boolean containsCycles ();

  /**
   * Check if this graph is completely self contained. As relations between
   * nodes do not check whether both nodes belong to the same graph it is
   * possible to link different graphs together with relations. This method
   * returns true, if all nodes referenced from all relations link to objects
   * inside this graph.
   * 
   * @return <code>true</code> if this graph is self contained,
   *         <code>false</code> if not.
   */
  boolean isSelfContained ();

  /**
   * @return A new incidence matrix (Symmetric matrix where 1/-1 is set if a
   *         relation is present, 0 if no relation is present; Number of rows
   *         and columns is equal to the number of nodes).
   * @throws IllegalArgumentException
   *         If this graph contains no node
   */
  @Nonnull
  Matrix createIncidenceMatrix ();
}
