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

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.annotations.MustImplementEqualsAndHashcode;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.state.EChange;

/**
 * Base interface for graph node implementations.
 * 
 * @author Philip Helger
 */
@MustImplementEqualsAndHashcode
public interface IDirectedGraphNode extends IBaseGraphNode <IDirectedGraphNode, IDirectedGraphRelation>
{
  /**
   * Add a new incoming relation to this node
   * 
   * @param aRelation
   *        The relation to be added. May not be <code>null</code>.
   */
  void addIncomingRelation (@Nonnull IDirectedGraphRelation aRelation);

  /**
   * @return <code>true</code> if this node has at least one incoming relation.
   */
  boolean hasIncomingRelations ();

  /**
   * @return The number of incoming relations. Always &ge; 0.
   */
  @Nonnegative
  int getIncomingRelationCount ();

  /**
   * Check if this node has the passed relation as an incoming relations.
   * 
   * @param aRelation
   *        The relation to be checked. May be <code>null</code>.
   * @return <code>true</code> if the passed relation is an incoming relation,
   *         <code>false</code> if not
   */
  boolean isIncomingRelation (@Nullable IDirectedGraphRelation aRelation);

  /**
   * @return All incoming relations and never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  List <IDirectedGraphRelation> getAllIncomingRelations ();

  /**
   * Remove the passed relation from the set of incoming relations.
   * 
   * @param aRelation
   *        The relation to be removed. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if the passed relation was successfully
   *         removed from the incoming relations.
   */
  @Nonnull
  EChange removeIncomingRelation (@Nonnull IDirectedGraphRelation aRelation);

  /**
   * Remove all incoming relations.
   * 
   * @return {@link EChange#CHANGED} if the at least one relation was
   *         successfully removed from the incoming relations.
   */
  @Nonnull
  EChange removeAllIncomingRelations ();

  /**
   * Check if this graph node is directly connected to the passed node via an
   * incoming relation.
   * 
   * @param aNode
   *        The node to be checked. May be <code>null</code>.
   * @return <code>true</code> if is connected, <code>false</code> if not
   */
  boolean isFromNode (@Nullable IDirectedGraphNode aNode);

  /**
   * @return All nodes that are connected via incoming relations.
   */
  @Nonnull
  @ReturnsMutableCopy
  Set <IDirectedGraphNode> getAllFromNodes ();

  /**
   * Find the incoming relation from the passed node to this node.
   * 
   * @param aFromNode
   *        The from node to use. May be <code>null</code>.
   * @return <code>null</code> if there exists no incoming relation from the
   *         passed node to this node.
   */
  @Nullable
  IDirectedGraphRelation getIncomingRelationFrom (@Nullable IDirectedGraphNode aFromNode);

  // --- outgoing ---

  /**
   * Add a new outgoing relation from this node
   * 
   * @param aRelation
   *        The relation to be added. May not be <code>null</code>.
   */
  void addOutgoingRelation (@Nonnull IDirectedGraphRelation aRelation);

  /**
   * @return <code>true</code> if this node has at least one outgoing relation.
   */
  boolean hasOutgoingRelations ();

  /**
   * @return The number of outgoing relations. Always &ge; 0.
   */
  @Nonnegative
  int getOutgoingRelationCount ();

  /**
   * Check if this node has the passed relation as an outgoing relations.
   * 
   * @param aRelation
   *        The relation to be checked. May be <code>null</code>.
   * @return <code>true</code> if the passed relation is an outgoing relation,
   *         <code>false</code> if not
   */
  boolean isOutgoingRelation (@Nullable IDirectedGraphRelation aRelation);

  /**
   * @return All outgoing relations and never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  List <IDirectedGraphRelation> getAllOutgoingRelations ();

  /**
   * Remove the passed relation from the set of outgoing relations.
   * 
   * @param aRelation
   *        The relation to be removed. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if the passed relation was successfully
   *         removed from the outgoing relations.
   */
  @Nonnull
  EChange removeOutgoingRelation (@Nonnull IDirectedGraphRelation aRelation);

  /**
   * Remove all outgoing relations.
   * 
   * @return {@link EChange#CHANGED} if the at least one relation was
   *         successfully removed from the outgoing relations.
   */
  @Nonnull
  EChange removeAllOutgoingRelations ();

  /**
   * Check if this graph node is directly connected to the passed node via an
   * outgoing relation.
   * 
   * @param aNode
   *        The node to be checked. May be <code>null</code>.
   * @return <code>true</code> if is connected, <code>false</code> if not
   */
  boolean isToNode (@Nullable IDirectedGraphNode aNode);

  /**
   * @return All nodes that are connected via outgoing relations.
   */
  @Nonnull
  @ReturnsMutableCopy
  Set <IDirectedGraphNode> getAllToNodes ();

  /**
   * Find the incoming relation from this node to the passed node.
   * 
   * @param aToNode
   *        The to node to use. May be <code>null</code>.
   * @return <code>null</code> if there exists no incoming relation from this
   *         node to the passed node.
   */
  @Nullable
  IDirectedGraphRelation getOutgoingRelationTo (@Nullable IDirectedGraphNode aToNode);

  // --- incoming and/or outgoing

  /**
   * Check if this node has incoming <b>or</b> outgoing relations. This is equal
   * to calling <code>hasIncomingRelations() || hasOutgoingRelations()</code>
   * 
   * @return <code>true</code> if this node has at least one incoming or
   *         outgoing relation.
   */
  boolean hasIncomingOrOutgoingRelations ();

  /**
   * Check if this node has incoming <b>and</b> outgoing relations. This is
   * equal to calling
   * <code>hasIncomingRelations() && hasOutgoingRelations()</code>
   * 
   * @return <code>true</code> if this node has at least one incoming and at
   *         least one outgoing relation.
   */
  boolean hasIncomingAndOutgoingRelations ();
}
