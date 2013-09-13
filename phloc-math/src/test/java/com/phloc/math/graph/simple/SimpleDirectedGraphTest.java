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
package com.phloc.math.graph.simple;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.phloc.commons.mock.PhlocTestUtils;
import com.phloc.math.graph.AbstractGraphTestCase;
import com.phloc.math.graph.IDirectedGraphNode;
import com.phloc.math.graph.IReadonlyDirectedGraph;
import com.phloc.math.graph.impl.DirectedGraphNode;

/**
 * Test class for class {@link SimpleDirectedGraph}.
 * 
 * @author Philip Helger
 */
public final class SimpleDirectedGraphTest extends AbstractGraphTestCase
{
  @Test
  public void testCtor ()
  {
    final IReadonlyDirectedGraph sg = new SimpleDirectedGraph ();
    assertTrue (sg.getAllStartNodes ().isEmpty ());
    assertTrue (sg.getAllEndNodes ().isEmpty ());
    assertFalse (sg.containsCycles ());
    assertNotNull (sg.toString ());

    try
    {
      // no node contained
      sg.getSingleStartNode ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {}

    try
    {
      // no node contained
      sg.getSingleEndNode ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {}
  }

  @Test
  public void testAddNode ()
  {
    final SimpleDirectedGraph sg = new SimpleDirectedGraph ();
    assertEquals (0, sg.getNodeCount ());
    try
    {
      // null node not allowed
      sg.addNode ((DirectedGraphNode) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    final DirectedGraphNode n = new DirectedGraphNode ();
    assertTrue (sg.addNode (n).isChanged ());
    assertEquals (1, sg.getNodeCount ());
    assertFalse (n.hasRelations ());

    // node already contained
    assertFalse (sg.addNode (n).isChanged ());

    assertTrue (sg.getAllStartNodes ().contains (n));
    assertTrue (sg.getAllEndNodes ().contains (n));
    assertFalse (sg.containsCycles ());
    assertEquals (sg.getSingleStartNode (), n);
    assertEquals (sg.getSingleEndNode (), n);
    assertTrue (sg.getAllRelations ().isEmpty ());

    // Add a second node
    final DirectedGraphNode n2 = new DirectedGraphNode ();
    assertTrue (sg.addNode (n2).isChanged ());
    assertFalse (n2.hasRelations ());

    // node already contained
    assertFalse (sg.addNode (n2).isChanged ());
    assertTrue (sg.getAllRelations ().isEmpty ());

    assertTrue (sg.getAllStartNodes ().contains (n));
    assertTrue (sg.getAllStartNodes ().contains (n2));
    assertTrue (sg.getAllEndNodes ().contains (n));
    assertTrue (sg.getAllEndNodes ().contains (n2));
    assertFalse (sg.containsCycles ());
    try
    {
      sg.getSingleStartNode ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {}
    try
    {
      sg.getSingleEndNode ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {}

    assertNotNull (sg.createNode ());
    assertNotNull (sg.createNode ("id4711"));
    assertNull (sg.createNode ("id4711"));
  }

  @Test
  public void testClear ()
  {
    final SimpleDirectedGraph sg = new SimpleDirectedGraph ();

    final DirectedGraphNode n = new DirectedGraphNode ();
    assertTrue (sg.addNode (n).isChanged ());

    final DirectedGraphNode n2 = new DirectedGraphNode ();
    assertTrue (sg.addNode (n2).isChanged ());

    assertTrue (sg.clear ().isChanged ());
    assertFalse (sg.clear ().isChanged ());

    assertTrue (sg.getAllStartNodes ().isEmpty ());
    assertTrue (sg.getAllEndNodes ().isEmpty ());
    assertFalse (sg.containsCycles ());

    try
    {
      // no node contained
      sg.getSingleStartNode ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {}

    try
    {
      // no node contained
      sg.getSingleEndNode ();
      fail ();
    }
    catch (final IllegalStateException ex)
    {}
  }

  @Test
  public void testCycles ()
  {
    SimpleDirectedGraph sg = _buildDirectedGraph ();
    assertTrue (sg.isSelfContained ());
    assertFalse (sg.containsCycles ());
    assertFalse (sg.containsCycles ());

    sg = new SimpleDirectedGraph ();
    final IDirectedGraphNode n1 = sg.createNode (null);
    final IDirectedGraphNode n2 = sg.createNode (null);
    sg.createRelation (n1, n2);
    sg.createRelation (n2, n1);

    assertEquals (1, n1.getIncomingRelationCount ());
    assertEquals (1, n1.getOutgoingRelationCount ());
    assertTrue (n1.hasIncomingRelations ());
    assertTrue (n1.hasOutgoingRelations ());
    assertTrue (n1.hasRelations ());
    assertTrue (n1.hasIncomingAndOutgoingRelations ());
    assertTrue (n1.isConnectedWith (n2));
    assertTrue (n1.isFromNode (n2));
    assertTrue (n1.isToNode (n2));

    assertEquals (1, n2.getIncomingRelationCount ());
    assertEquals (1, n2.getOutgoingRelationCount ());
    assertTrue (n2.hasIncomingRelations ());
    assertTrue (n2.hasOutgoingRelations ());
    assertTrue (n2.hasRelations ());
    assertTrue (n2.hasIncomingAndOutgoingRelations ());
    assertTrue (n2.isConnectedWith (n1));
    assertTrue (n2.isFromNode (n1));
    assertTrue (n2.isToNode (n1));

    assertTrue (sg.isSelfContained ());
    assertTrue (sg.containsCycles ());
    assertTrue (sg.containsCycles ());

    PhlocTestUtils.testDefaultImplementationWithEqualContentObject (_buildDirectedGraph (), _buildDirectedGraph ());
    PhlocTestUtils.testDefaultImplementationWithEqualContentObject (new SimpleDirectedGraph (),
                                                                    new SimpleDirectedGraph ());
    PhlocTestUtils.testDefaultImplementationWithDifferentContentObject (_buildDirectedGraph (),
                                                                        new SimpleDirectedGraph ());
  }

  @Test
  public void testCycles2 ()
  {
    final SimpleDirectedGraph sg = new SimpleDirectedGraph ();
    for (int i = 1; i <= 6; ++i)
      sg.createNode (Integer.toString (i));

    // first cycle
    sg.createRelation ("1", "2");
    assertFalse (sg.containsCycles ());
    sg.createRelation ("2", "3");
    assertFalse (sg.containsCycles ());
    sg.createRelation ("3", "1");
    assertTrue (sg.containsCycles ());

    // Second cycle
    sg.createRelation ("4", "5");
    assertTrue (sg.containsCycles ());
    sg.createRelation ("5", "6");
    assertTrue (sg.containsCycles ());
    sg.createRelation ("6", "4");
    assertTrue (sg.containsCycles ());
  }

  @Test
  public void testSelfContained ()
  {
    final ISimpleDirectedGraph sg = new SimpleDirectedGraph ();
    assertTrue (sg.isSelfContained ());

    // n1 belongs to the graph
    IDirectedGraphNode n1 = sg.createNode ("any");
    assertTrue (sg.isSelfContained ());

    // n2 does not belong to the graph
    IDirectedGraphNode n2 = new DirectedGraphNode ("other");
    sg.createRelation (n1, n2);
    assertFalse (sg.isSelfContained ());

    // Get all relations
    assertEquals (1, sg.getAllRelations ().size ());

    // Remove nodes
    assertTrue (sg.removeNode (n1).isChanged ());
    assertFalse (sg.removeNode (n2).isChanged ());
    assertFalse (sg.clear ().isChanged ());

    // n1 does not belongs to the graph
    n1 = new DirectedGraphNode ("any");
    assertTrue (sg.isSelfContained ());

    // n2 belongs to the graph
    n2 = sg.createNode ("other");
    sg.createRelation (n1, n2);
    assertFalse (sg.isSelfContained ());
  }
}
