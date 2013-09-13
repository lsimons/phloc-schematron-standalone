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
package com.phloc.math.graph.algo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.GlobalDebug;
import com.phloc.commons.IHasStringRepresentation;
import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.lang.GenericReflection;
import com.phloc.math.graph.IBaseGraph;
import com.phloc.math.graph.IBaseGraphNode;
import com.phloc.math.graph.IBaseGraphRelation;
import com.phloc.math.graph.IDirectedGraphNode;
import com.phloc.math.graph.IDirectedGraphRelation;

/**
 * Find the shortest path between 2 graph nodes, using Dijsktra's algorithm
 * 
 * @author Philip Helger
 */
public final class Dijkstra
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (Dijkstra.class);

  private static final class WorkElement <N extends IBaseGraphNode <N, ?>> implements IHasStringRepresentation
  {
    private final N m_aFromNode;
    private final int m_nDistance;
    private final N m_aToNode;

    /**
     * Special constructor for the initial state
     * 
     * @param nDistance
     *        Distance to use
     * @param aToNode
     *        to-node to use
     */
    public WorkElement (@Nonnegative final int nDistance, @Nonnull final N aToNode)
    {
      this (null, nDistance, aToNode);
    }

    public WorkElement (@Nullable final N aFromNode, @Nonnegative final int nDistance, @Nonnull final N aToNode)
    {
      if (nDistance < 0)
        throw new IllegalArgumentException ("Distance may not be negative: " + nDistance);
      if (aToNode == null)
        throw new NullPointerException ("toNode");
      m_aFromNode = aFromNode;
      m_nDistance = nDistance;
      m_aToNode = aToNode;
    }

    @Nullable
    public N getFromNode ()
    {
      return m_aFromNode;
    }

    @Nullable
    public String getFromNodeID ()
    {
      return m_aFromNode == null ? null : m_aFromNode.getID ();
    }

    @Nonnegative
    public int getDistance ()
    {
      return m_nDistance;
    }

    @Nonnull
    public N getToNode ()
    {
      return m_aToNode;
    }

    @Nonnull
    public String getToNodeID ()
    {
      return m_aToNode.getID ();
    }

    @Nonnull
    @Nonempty
    public String getAsString ()
    {
      return "{" +
             (m_aFromNode == null ? "" : "'" + m_aFromNode.getID () + "',") +
             (m_nDistance == Integer.MAX_VALUE ? "Inf" : Integer.toString (m_nDistance)) +
             ",'" +
             m_aToNode.getID () +
             "'}";
    }
  }

  private static final class WorkRow <N extends IBaseGraphNode <N, ?>>
  {
    private final Map <String, WorkElement <N>> m_aElements;

    public WorkRow (@Nonnegative final int nElements)
    {
      if (nElements <= 0)
        throw new IllegalArgumentException ("Elements must be positive: " + nElements);
      m_aElements = new LinkedHashMap <String, WorkElement <N>> (nElements);
    }

    public void add (@Nonnull final WorkElement <N> aElement)
    {
      if (aElement == null)
        throw new NullPointerException ("element");

      m_aElements.put (aElement.getToNodeID (), aElement);
    }

    @Nullable
    public WorkElement <N> getElement (@Nullable final String sNodeID)
    {
      return m_aElements.get (sNodeID);
    }

    /**
     * @return The element with the smallest distance!
     */
    @Nonnull
    public WorkElement <N> getClosestElement ()
    {
      WorkElement <N> ret = null;
      for (final WorkElement <N> aElement : m_aElements.values ())
        if (ret == null || aElement.getDistance () < ret.getDistance ())
          ret = aElement;
      if (ret == null)
        throw new IllegalStateException ("Cannot call this method without an element!");
      return ret;
    }

    @Nonnull
    @ReturnsMutableCopy
    public List <WorkElement <N>> getAllElements ()
    {
      return ContainerHelper.newList (m_aElements.values ());
    }
  }

  @Immutable
  public static final class Result <N extends IBaseGraphNode <N, ?>> implements IHasStringRepresentation
  {
    private final List <N> m_aResultNodes;
    private final int m_nResultDistance;

    public Result (@Nonnull @Nonempty final List <N> aResultNodes, @Nonnegative final int nResultDistance)
    {
      if (ContainerHelper.isEmpty (aResultNodes))
        throw new IllegalArgumentException ("resultNodes");
      if (nResultDistance < 0)
        throw new IllegalArgumentException ("Distance negative: " + nResultDistance);
      m_aResultNodes = aResultNodes;
      m_nResultDistance = nResultDistance;
    }

    @Nonnull
    @ReturnsMutableCopy
    public List <N> getAllResultNodes ()
    {
      return ContainerHelper.newList (m_aResultNodes);
    }

    @Nonnull
    public int getResultNodeCount ()
    {
      return m_aResultNodes.size ();
    }

    @Nonnegative
    public int getResultDistance ()
    {
      return m_nResultDistance;
    }

    @Nonnull
    @Nonempty
    public String getAsString ()
    {
      final StringBuilder aSB = new StringBuilder ();
      aSB.append ("Distance ").append (m_nResultDistance).append (" for route {");
      int nIndex = 0;
      for (final N aNode : m_aResultNodes)
      {
        if (nIndex++ > 0)
          aSB.append (',');
        aSB.append ('\'').append (aNode.getID ()).append ('\'');
      }
      return aSB.append ('}').toString ();
    }
  }

  private Dijkstra ()
  {}

  @Nullable
  private static <N extends IBaseGraphNode <N, R>, R extends IBaseGraphRelation <N, R>> R _getRelationFromLastMatch (@Nonnull final WorkElement <N> aLastMatch,
                                                                                                                     @Nonnull final N aNode)
  {
    if (aNode.isDirected ())
    {
      // Directed

      // Cast to Object required for JDK command line compiler
      final Object aDirectedFromNode = aLastMatch.getToNode ();
      final Object aDirectedToNode = aNode;
      final IDirectedGraphRelation r = ((IDirectedGraphNode) aDirectedFromNode).getOutgoingRelationTo ((IDirectedGraphNode) aDirectedToNode);
      return GenericReflection.<IDirectedGraphRelation, R> uncheckedCast (r);
    }

    // Undirected
    return aLastMatch.getToNode ().getRelation (aNode);
  }

  // ESCA-JAVA0285:
  @Nonnull
  public static <N extends IBaseGraphNode <N, R>, R extends IBaseGraphRelation <N, R>> Dijkstra.Result <N> applyDijkstra (@Nonnull final IBaseGraph <N, R> aGraph,
                                                                                                                          @Nonnull @Nonempty final String sFromID,
                                                                                                                          @Nonnull @Nonempty final String sToID,
                                                                                                                          @Nonnull @Nonempty final String sRelationCostAttr)
  {
    final N aStartNode = aGraph.getNodeOfID (sFromID);
    if (aStartNode == null)
      throw new IllegalArgumentException ("From ID: " + sFromID);
    final N aEndNode = aGraph.getNodeOfID (sToID);
    if (aEndNode == null)
      throw new IllegalArgumentException ("To ID: " + sToID);

    // Ordered set for deterministic results
    final Set <N> aAllRemainingNodes = ContainerHelper.newOrderedSet (aGraph.getAllNodes ().values ());

    if (GlobalDebug.isDebugMode ())
      s_aLogger.info ("Starting Dijkstra on directed graph with " +
                      aAllRemainingNodes.size () +
                      " nodes starting from '" +
                      sFromID +
                      "' and up to '" +
                      sToID +
                      "'");

    // Map from to-node-id to element
    final Map <String, WorkElement <N>> aAllMatches = new LinkedHashMap <String, WorkElement <N>> ();
    WorkElement <N> aLastMatch = null;
    WorkRow <N> aLastRow = null;
    int nIteration = 0;
    do
    {
      final WorkRow <N> aRow = new WorkRow <N> (aAllRemainingNodes.size ());
      if (aLastRow == null)
      {
        // Initial row - no from node
        for (final N aNode : aAllRemainingNodes)
          if (aNode.equals (aStartNode))
          {
            // Start node has distance 0 to itself
            aRow.add (new WorkElement <N> (0, aNode));
          }
          else
          {
            // All other elements have infinite distance to the start node (for
            // now)
            aRow.add (new WorkElement <N> (Integer.MAX_VALUE, aNode));
          }
      }
      else
      {
        // All following rows
        for (final N aNode : aAllRemainingNodes)
        {
          // Find distance to last match
          final WorkElement <N> aPrevElement = aLastRow.getElement (aNode.getID ());

          // Get the relation from the last match to this node (may be null if
          // nodes are not connected)
          final R aRelation = Dijkstra.<N, R> _getRelationFromLastMatch (aLastMatch, aNode);
          if (aRelation != null)
          {
            // Nodes are related - check weight
            final int nNewDistance = aLastMatch.getDistance () +
                                     aRelation.getAttributeAsInt (sRelationCostAttr, Integer.MIN_VALUE);

            // Use only, if distance is shorter (=better) than before!
            if (nNewDistance < aPrevElement.getDistance ())
              aRow.add (new WorkElement <N> (aLastMatch.getToNode (), nNewDistance, aNode));
            else
              aRow.add (aPrevElement);
          }
          else
          {
            // Nodes are not related - use result from previous row
            aRow.add (aPrevElement);
          }
        }
      }

      // Get the closest element of the current row
      final WorkElement <N> aClosest = aRow.getClosestElement ();

      if (GlobalDebug.isDebugMode ())
      {
        final StringBuilder aSB = new StringBuilder ("Iteration[").append (nIteration).append ("]: ");
        for (final WorkElement <N> e : aRow.getAllElements ())
          aSB.append (e.getAsString ());
        aSB.append (" ==> ").append (aClosest.getAsString ());
        s_aLogger.info (aSB.toString ());
      }

      aAllRemainingNodes.remove (aClosest.getToNode ());
      aAllMatches.put (aClosest.getToNodeID (), aClosest);
      aLastMatch = aClosest;
      aLastRow = aRow;
      ++nIteration;

      if (aClosest.getToNode ().equals (aEndNode))
      {
        // We found the shortest way to the end node!
        break;
      }
    } while (true);

    // Now get the result path from back to front
    final int nResultDistance = aLastMatch.getDistance ();
    final List <N> aResultNodes = new ArrayList <N> ();
    while (true)
    {
      aResultNodes.add (0, aLastMatch.getToNode ());
      // Are we at the start node?
      if (aLastMatch.getFromNode () == null)
        break;
      aLastMatch = aAllMatches.get (aLastMatch.getFromNodeID ());
      if (aLastMatch == null)
        throw new IllegalStateException ("Inconsistency!");
    }

    // Results
    return new Dijkstra.Result <N> (aResultNodes, nResultDistance);
  }
}
