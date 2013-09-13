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
package com.phloc.math.graph.impl;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.state.EChange;
import com.phloc.commons.state.ETriState;
import com.phloc.math.graph.IGraph;
import com.phloc.math.graph.IGraphNode;
import com.phloc.math.graph.IGraphObjectFactory;
import com.phloc.math.graph.IGraphRelation;
import com.phloc.math.graph.iterate.GraphIterator;
import com.phloc.math.matrix.Matrix;

/**
 * A simple graph object that bidirectionally links graph nodes.
 * 
 * @author Philip Helger
 */
@NotThreadSafe
public class Graph extends AbstractBaseGraph <IGraphNode, IGraphRelation> implements IGraph
{
  private final IGraphObjectFactory m_aFactory;
  private ETriState m_eCacheHasCycles = ETriState.UNDEFINED;

  public Graph (@Nullable final String sID, @Nonnull final IGraphObjectFactory aFactory)
  {
    super (sID);
    if (aFactory == null)
      throw new NullPointerException ("factory");
    m_aFactory = aFactory;
  }

  public final boolean isDirected ()
  {
    return false;
  }

  private void _invalidateCache ()
  {
    // Reset the "has cycles" cached value
    m_eCacheHasCycles = ETriState.UNDEFINED;
  }

  @Nonnull
  public IGraphNode createNode ()
  {
    // Create node with new ID
    final IGraphNode aNode = m_aFactory.createNode ();
    if (addNode (aNode).isUnchanged ())
      throw new IllegalStateException ("The ID factory created the ID '" + aNode.getID () + "' that is already in use");
    return aNode;
  }

  @Nullable
  public IGraphNode createNode (@Nullable final String sID)
  {
    final IGraphNode aNode = m_aFactory.createNode (sID);
    return addNode (aNode).isChanged () ? aNode : null;
  }

  @Nonnull
  public EChange addNode (@Nonnull final IGraphNode aNode)
  {
    if (aNode == null)
      throw new NullPointerException ("node");

    if (!isChangingConnectedObjectsAllowed () && aNode.hasRelations ())
      throw new IllegalArgumentException ("The node to be added already has incoming and/or outgoing relations and this is not allowed!");

    final String sID = aNode.getID ();
    if (m_aNodes.containsKey (sID))
      return EChange.UNCHANGED;
    m_aNodes.put (sID, aNode);

    _invalidateCache ();
    return EChange.CHANGED;
  }

  @Nonnull
  public EChange removeNode (@Nonnull final IGraphNode aNode)
  {
    if (aNode == null)
      throw new NullPointerException ("node");

    if (!isChangingConnectedObjectsAllowed () && aNode.hasRelations ())
      throw new IllegalArgumentException ("The node to be removed already has incoming and/or outgoing relations and this is not allowed!");

    if (m_aNodes.remove (aNode.getID ()) == null)
      return EChange.UNCHANGED;

    _invalidateCache ();
    return EChange.CHANGED;
  }

  @Nonnull
  public EChange removeNodeAndAllRelations (@Nonnull final IGraphNode aNode)
  {
    if (aNode == null)
      throw new NullPointerException ("node");

    if (!m_aNodes.containsKey (aNode.getID ()))
      return EChange.UNCHANGED;

    // Remove all affected relations from all nodes
    for (final IGraphRelation aRelation : aNode.getAllRelations ())
      for (final IGraphNode aNode2 : aRelation.getAllConnectedNodes ())
        aNode2.removeRelation (aRelation);

    // Remove the node itself
    if (removeNode (aNode).isUnchanged ())
      throw new IllegalStateException ("Inconsistency removing node and all relations");
    return EChange.CHANGED;
  }

  @Nonnull
  private IGraphRelation _connect (@Nonnull final IGraphRelation aRelation)
  {
    EChange eChange = EChange.UNCHANGED;
    for (final IGraphNode aNode : aRelation.getAllConnectedNodes ())
      eChange = eChange.or (aNode.addRelation (aRelation));
    if (eChange.isChanged ())
      _invalidateCache ();
    return aRelation;
  }

  @Nonnull
  public IGraphRelation createRelation (@Nonnull final IGraphNode aFrom, @Nonnull final IGraphNode aTo)
  {
    return _connect (m_aFactory.createRelation (aFrom, aTo));
  }

  @Nonnull
  public IGraphRelation createRelation (@Nullable final String sID,
                                        @Nonnull final IGraphNode aFrom,
                                        @Nonnull final IGraphNode aTo)
  {
    return _connect (m_aFactory.createRelation (sID, aFrom, aTo));
  }

  @Nonnull
  public EChange removeRelation (@Nullable final IGraphRelation aRelation)
  {
    EChange ret = EChange.UNCHANGED;
    if (aRelation != null)
    {
      for (final IGraphNode aNode : aRelation.getAllConnectedNodes ())
        ret = ret.or (aNode.removeRelation (aRelation));
      if (ret.isChanged ())
        _invalidateCache ();
    }
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public Map <String, IGraphRelation> getAllRelations ()
  {
    final Map <String, IGraphRelation> ret = new LinkedHashMap <String, IGraphRelation> ();
    for (final IGraphNode aNode : m_aNodes.values ())
      for (final IGraphRelation aRelation : aNode.getAllRelations ())
        ret.put (aRelation.getID (), aRelation);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public Set <String> getAllRelationIDs ()
  {
    final Set <String> ret = new LinkedHashSet <String> ();
    for (final IGraphNode aNode : m_aNodes.values ())
      ret.addAll (aNode.getAllRelationIDs ());
    return ret;
  }

  @Override
  @Nonnull
  public EChange clear ()
  {
    if (m_aNodes.isEmpty ())
      return EChange.UNCHANGED;
    m_aNodes.clear ();

    _invalidateCache ();
    return EChange.CHANGED;
  }

  public boolean containsCycles ()
  {
    // Use cached result?
    if (m_eCacheHasCycles.isUndefined ())
    {
      m_eCacheHasCycles = ETriState.FALSE;
      // Check all nodes, in case we a small cycle and a set of other nodes (see
      // test case testCycles2)
      final List <IGraphNode> aAllNodes = ContainerHelper.newList (m_aNodes.values ());
      while (!aAllNodes.isEmpty ())
      {
        // Iterate from the first node
        final GraphIterator it = new GraphIterator (aAllNodes.remove (0));
        if (it.hasCycles ())
        {
          m_eCacheHasCycles = ETriState.TRUE;
          break;
        }
        while (it.hasNext ())
        {
          // Remove from remaining list, because node is reachable from some
          // other node
          aAllNodes.remove (it.next ());
        }
      }
    }

    // cannot be undefined here
    return m_eCacheHasCycles.getAsBooleanValue (true);
  }

  public boolean isSelfContained ()
  {
    for (final IGraphNode aNode : m_aNodes.values ())
      for (final IGraphRelation aRelation : aNode.getAllRelations ())
        for (final IGraphNode aRelNode : aRelation.getAllConnectedNodes ())
          if (!m_aNodes.containsKey (aRelNode.getID ()))
            return false;
    return true;
  }

  @Nonnull
  public Matrix createIncidenceMatrix ()
  {
    final int nNodeCount = getNodeCount ();
    final Matrix ret = new Matrix (nNodeCount, nNodeCount, 0);
    final IGraphNode [] aNodes = m_aNodes.values ().toArray (new IGraphNode [nNodeCount]);
    for (int nRow = 0; nRow < nNodeCount; ++nRow)
    {
      final IGraphNode aNodeRow = aNodes[nRow];
      for (int nCol = 0; nCol < nNodeCount; ++nCol)
        if (nRow != nCol)
          if (aNodeRow.isConnectedWith (aNodes[nCol]))
          {
            ret.set (nRow, nCol, 1);
            ret.set (nCol, nRow, 1);
          }
    }
    return ret;
  }

  @Override
  public boolean equals (final Object o)
  {
    return super.equals (o);
  }

  @Override
  public int hashCode ()
  {
    return super.hashCode ();
  }
}
