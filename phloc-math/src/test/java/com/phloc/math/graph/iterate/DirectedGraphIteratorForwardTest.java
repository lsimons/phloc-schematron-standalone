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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.NoSuchElementException;

import org.junit.Test;

import com.phloc.math.graph.AbstractGraphTestCase;
import com.phloc.math.graph.IDirectedGraphNode;
import com.phloc.math.graph.IReadonlyDirectedGraph;

/**
 * Test class for class {@link DirectedGraphIteratorForward}.
 * 
 * @author Philip Helger
 */
public final class DirectedGraphIteratorForwardTest extends AbstractGraphTestCase
{
  @Test
  public void testGraphIterator ()
  {
    try
    {
      // null node not allowed
      new DirectedGraphIteratorForward (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // null node not allowed
      new DirectedGraphIteratorForward (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    final IReadonlyDirectedGraph aGraph = _buildDirectedGraph ();
    final IDirectedGraphNode aStart = aGraph.getSingleStartNode ();
    assertEquals (aStart.getID (), "0");
    final DirectedGraphIteratorForward it = new DirectedGraphIteratorForward (aStart);

    try
    {
      it.remove ();
      fail ();
    }
    catch (final UnsupportedOperationException ex)
    {
      // expected exception
    }

    // one of the following assertEquals may fail if the hashmap is sorted in a
    // different way
    for (int i = 0; i < 100; ++i)
      assertTrue (it.hasNext ());
    assertEquals (1, _getNodeValue (it.next ()));
    for (int i = 0; i < 100; ++i)
      assertTrue (it.hasNext ());
    assertEquals (2, _getNodeValue (it.next ()));
    for (int i = 0; i < 100; ++i)
      assertTrue (it.hasNext ());
    assertEquals (3, _getNodeValue (it.next ()));
    assertTrue (it.hasNext ());
    assertEquals (4, _getNodeValue (it.next ()));
    assertTrue (it.hasNext ());
    assertEquals (5, _getNodeValue (it.next ()));
    assertTrue (it.hasNext ());
    assertEquals (6, _getNodeValue (it.next ()));
    assertTrue (it.hasNext ());
    assertEquals (7, _getNodeValue (it.next ()));
    assertFalse (it.hasNext ());
  }

  @Test
  public void testStartIteratingInTheMiddleOneWay ()
  {
    final IReadonlyDirectedGraph aGraph = _buildDirectedGraph ();
    final IDirectedGraphNode aStartNode = aGraph.getNodeOfID ("1");
    final DirectedGraphIteratorForward it = new DirectedGraphIteratorForward (aStartNode);
    assertTrue (it.hasNext ());
    assertEquals (2, _getNodeValue (it.next ()));
    assertTrue (it.hasNext ());
    assertEquals (3, _getNodeValue (it.next ()));
    assertTrue (it.hasNext ());
    assertEquals (4, _getNodeValue (it.next ()));
    assertTrue (it.hasNext ());
    assertEquals (5, _getNodeValue (it.next ()));
    assertFalse (it.hasNext ());
  }

  @Test
  public void testStartIteratingInTheMiddleTwoWays ()
  {
    final IReadonlyDirectedGraph aGraph = _buildDirectedGraph ();
    final IDirectedGraphNode aStartNode = aGraph.getNodeOfID ("5");
    final DirectedGraphIteratorForward it = new DirectedGraphIteratorForward (aStartNode);
    assertTrue (it.hasNext ());
    assertEquals (6, _getNodeValue (it.next ()));
    assertTrue (it.hasNext ());
    assertEquals (4, _getNodeValue (it.next ()));
    assertTrue (it.hasNext ());
    assertEquals (5, _getNodeValue (it.next ()));
    assertTrue (it.hasNext ());
    assertEquals (7, _getNodeValue (it.next ()));
    assertFalse (it.hasNext ());
  }

  @Test
  public void testCycleIterate1 ()
  {
    final IReadonlyDirectedGraph aGraph = _buildSimpleDirectedGraphCycle ();
    final DirectedGraphIteratorForward it = new DirectedGraphIteratorForward (aGraph.getNodeOfID ("0"));
    assertTrue (it.hasNext ());
    // first item has ID 0 and value 1
    assertEquals (1, _getNodeValue (it.next ()));
    assertTrue (it.hasNext ());
    // second item has ID 1 and value 2
    assertEquals (2, _getNodeValue (it.next ()));
    assertFalse (it.hasNext ());
  }

  @Test
  public void testCycleIterate2 ()
  {
    final IReadonlyDirectedGraph aGraph = _buildSimpleDirectedGraphCycle2 ();
    final DirectedGraphIteratorForward it = new DirectedGraphIteratorForward (aGraph.getNodeOfID ("0"));
    assertNotNull (it.iterator ());
    assertTrue (it.hasNext ());
    assertEquals (1, _getNodeValue (it.next ()));
    assertTrue (it.hasNext ());
    assertEquals (2, _getNodeValue (it.next ()));
    assertTrue (it.hasNext ());
    assertEquals (3, _getNodeValue (it.next ()));
    assertTrue (it.hasNext ());
    assertEquals (4, _getNodeValue (it.next ()));
    assertFalse (it.hasNext ());

    try
    {
      it.next ();
      fail ();
    }
    catch (final NoSuchElementException ex)
    {}
  }
}
