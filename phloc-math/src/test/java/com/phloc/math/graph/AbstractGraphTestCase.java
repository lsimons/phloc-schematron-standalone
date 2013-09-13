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

import javax.annotation.Nonnull;

import com.phloc.commons.mock.AbstractPhlocTestCase;
import com.phloc.math.graph.simple.SimpleDirectedGraph;
import com.phloc.math.graph.simple.SimpleGraph;

public abstract class AbstractGraphTestCase extends AbstractPhlocTestCase
{
  protected static final String ATTR_VALUE = "value";

  @Nonnull
  private static final IDirectedGraphNode _createDGN (final SimpleDirectedGraph aGraph, final int i)
  {
    final IDirectedGraphNode aNode = aGraph.createNode (Integer.toString (i));
    aNode.setAttribute (ATTR_VALUE, Integer.valueOf (i + 1));
    return aNode;
  }

  @Nonnull
  private static final IGraphNode _createGN (final SimpleGraph aGraph, final int i)
  {
    final IGraphNode aNode = aGraph.createNode (Integer.toString (i));
    aNode.setAttribute (ATTR_VALUE, Integer.valueOf (i + 1));
    return aNode;
  }

  protected static final int _getNodeValue (@Nonnull final IBaseGraphNode <?, ?> aGN)
  {
    return aGN.getAttributeAsInt (ATTR_VALUE);
  }

  @Nonnull
  protected SimpleDirectedGraph _buildDirectedGraph ()
  {
    final SimpleDirectedGraph aGraph = new SimpleDirectedGraph ();

    final IDirectedGraphNode node0 = _createDGN (aGraph, 0);
    final IDirectedGraphNode node1 = _createDGN (aGraph, 1);
    final IDirectedGraphNode node2 = _createDGN (aGraph, 2);
    final IDirectedGraphNode node3 = _createDGN (aGraph, 3);
    final IDirectedGraphNode node4 = _createDGN (aGraph, 4);
    final IDirectedGraphNode node5 = _createDGN (aGraph, 5);
    final IDirectedGraphNode node6 = _createDGN (aGraph, 6);
    aGraph.createRelation (node0.getID (), node1.getID ());
    aGraph.createRelation (node1, node2);
    aGraph.createRelation (node2, node3);
    aGraph.createRelation (node3, node4);
    aGraph.createRelation (node0, node5);
    aGraph.createRelation (node5, node3);
    aGraph.createRelation (node5, node6);
    aGraph.createRelation (node6, node3);

    return aGraph;
  }

  @Nonnull
  protected SimpleGraph _buildGraph ()
  {
    final SimpleGraph aGraph = new SimpleGraph ();

    final IGraphNode node0 = _createGN (aGraph, 0);
    final IGraphNode node1 = _createGN (aGraph, 1);
    final IGraphNode node2 = _createGN (aGraph, 2);
    final IGraphNode node3 = _createGN (aGraph, 3);
    final IGraphNode node4 = _createGN (aGraph, 4);
    final IGraphNode node5 = _createGN (aGraph, 5);
    final IGraphNode node6 = _createGN (aGraph, 6);
    aGraph.createRelation (node0.getID (), node1.getID ());
    aGraph.createRelation (node0, node5);
    aGraph.createRelation (node1, node2);
    aGraph.createRelation (node2, node3);
    aGraph.createRelation (node3, node4);
    aGraph.createRelation (node3, node5);
    aGraph.createRelation (node3, node6);
    aGraph.createRelation (node5, node6);

    return aGraph;
  }

  @Nonnull
  protected IReadonlyDirectedGraph _buildSimpleDirectedGraphCycle ()
  {
    final SimpleDirectedGraph aGraph = new SimpleDirectedGraph ();
    final IDirectedGraphNode node0 = _createDGN (aGraph, 0);
    final IDirectedGraphNode node1 = _createDGN (aGraph, 1);
    aGraph.createRelation (node0, node1);
    aGraph.createRelation (node1, node0);
    return aGraph;
  }

  @Nonnull
  protected IReadonlyDirectedGraph _buildSimpleDirectedGraphCycle2 ()
  {
    final SimpleDirectedGraph aGraph = new SimpleDirectedGraph ();
    final IDirectedGraphNode node0 = _createDGN (aGraph, 0);
    final IDirectedGraphNode node1 = _createDGN (aGraph, 1);
    final IDirectedGraphNode node2 = _createDGN (aGraph, 2);
    final IDirectedGraphNode node3 = _createDGN (aGraph, 3);
    aGraph.createRelation (node0, node1);
    aGraph.createRelation (node1, node2);
    aGraph.createRelation (node2, node3);
    aGraph.createRelation (node3, node0);
    return aGraph;
  }

  @Nonnull
  protected IReadonlyGraph _buildSimpleGraphCycle ()
  {
    final SimpleGraph aGraph = new SimpleGraph ();
    final IGraphNode node0 = _createGN (aGraph, 0);
    final IGraphNode node1 = _createGN (aGraph, 1);
    aGraph.createRelation (node0, node1);
    aGraph.createRelation (node1, node0);
    return aGraph;
  }

  @Nonnull
  protected IReadonlyGraph _buildSimpleGraphCycle2 ()
  {
    final SimpleGraph aGraph = new SimpleGraph ();
    final IGraphNode node0 = _createGN (aGraph, 0);
    final IGraphNode node1 = _createGN (aGraph, 1);
    final IGraphNode node2 = _createGN (aGraph, 2);
    final IGraphNode node3 = _createGN (aGraph, 3);
    aGraph.createRelation (node0, node1);
    aGraph.createRelation (node1, node2);
    aGraph.createRelation (node2, node3);
    aGraph.createRelation (node3, node0);
    return aGraph;
  }
}
