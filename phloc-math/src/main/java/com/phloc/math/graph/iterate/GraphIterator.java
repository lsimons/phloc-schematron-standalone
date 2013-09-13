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
package com.phloc.math.graph.iterate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.phloc.commons.annotations.UnsupportedOperation;
import com.phloc.commons.collections.iterate.IIterableIterator;
import com.phloc.math.graph.IGraphNode;
import com.phloc.math.graph.IGraphRelation;

/**
 * A simple iterator for undirected graphs.
 * 
 * @author Philip Helger
 */
@NotThreadSafe
public final class GraphIterator implements IIterableIterator <IGraphNode>
{
  /**
   * Maps node IDs to node states
   */
  private final Set <String> m_aHandledObjects = new HashSet <String> ();

  private final Iterator <IGraphNode> m_aIter;

  /**
   * Does the graph have cycles?
   */
  private boolean m_bHasCycles = false;

  public GraphIterator (@Nonnull final IGraphNode aStartNode)
  {
    if (aStartNode == null)
      throw new NullPointerException ("startNode");

    // Collect all nodes, depth first
    final List <IGraphNode> aList = new ArrayList <IGraphNode> ();
    _traverseDFS (aStartNode, aList);
    m_aIter = aList.iterator ();
  }

  private void _traverseDFS (@Nonnull final IGraphNode aStartNode, @Nonnull final List <IGraphNode> aList)
  {
    m_aHandledObjects.add (aStartNode.getID ());
    aList.add (aStartNode);
    for (final IGraphRelation aRelation : aStartNode.getAllRelations ())
    {
      final boolean bNewRelation = m_aHandledObjects.add (aRelation.getID ());
      for (final IGraphNode aNode : aRelation.getAllConnectedNodes ())
        if (aNode != aStartNode)
        {
          if (!m_aHandledObjects.contains (aNode.getID ()))
            _traverseDFS (aNode, aList);
          else
          {
            // If an unexplored edge leads to a node visited before, then the
            // graph contains a cycle.
            if (bNewRelation)
              m_bHasCycles = true;
          }
        }
    }
  }

  public boolean hasNext ()
  {
    return m_aIter.hasNext ();
  }

  @Nullable
  public IGraphNode next ()
  {
    return m_aIter.next ();
  }

  /**
   * @return <code>true</code> if the iterator determined a cycle while
   *         iterating the graph
   */
  public boolean hasCycles ()
  {
    return m_bHasCycles;
  }

  /**
   * @throws UnsupportedOperationException
   *         every time!
   */
  @UnsupportedOperation
  public void remove ()
  {
    throw new UnsupportedOperationException ("This iterator has no remove!");
  }

  @Nonnull
  public Iterator <IGraphNode> iterator ()
  {
    return this;
  }
}
