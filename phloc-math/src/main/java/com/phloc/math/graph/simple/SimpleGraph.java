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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.math.graph.IGraphNode;
import com.phloc.math.graph.IGraphObjectFactory;
import com.phloc.math.graph.IGraphRelation;
import com.phloc.math.graph.impl.Graph;

/**
 * A simple graph object that bidirectionally links graph nodes.
 * 
 * @author Philip Helger
 */
@NotThreadSafe
public class SimpleGraph extends Graph implements ISimpleGraph
{
  public SimpleGraph ()
  {
    this (new SimpleGraphObjectFactory ());
  }

  public SimpleGraph (@Nonnull final IGraphObjectFactory aFactory)
  {
    super (null, aFactory);
  }

  @Nonnull
  public IGraphRelation createRelation (@Nonnull final String sFromNodeID, @Nonnull final String sToNodeID)
  {
    final IGraphNode aFromNode = getNodeOfID (sFromNodeID);
    if (aFromNode == null)
      throw new IllegalArgumentException ("Failed to resolve from node ID '" + sFromNodeID + "'");
    final IGraphNode aToNode = getNodeOfID (sToNodeID);
    if (aToNode == null)
      throw new IllegalArgumentException ("Failed to resolve to node ID '" + sToNodeID + "'");
    return createRelation (aFromNode, aToNode);
  }

  @Nonnull
  public IGraphRelation createRelation (@Nonnull @Nonempty final String sRelationID,
                                        @Nonnull final String sFromNodeID,
                                        @Nonnull final String sToNodeID)
  {
    final IGraphNode aFromNode = getNodeOfID (sFromNodeID);
    if (aFromNode == null)
      throw new IllegalArgumentException ("Failed to resolve from node ID '" + sFromNodeID + "'");
    final IGraphNode aToNode = getNodeOfID (sToNodeID);
    if (aToNode == null)
      throw new IllegalArgumentException ("Failed to resolve to node ID '" + sToNodeID + "'");
    return createRelation (sRelationID, aFromNode, aToNode);
  }
}
