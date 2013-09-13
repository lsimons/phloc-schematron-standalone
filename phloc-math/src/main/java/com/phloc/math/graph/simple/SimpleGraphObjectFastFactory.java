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
import javax.annotation.Nullable;

import com.phloc.math.graph.IGraphNode;
import com.phloc.math.graph.IGraphObjectFactory;
import com.phloc.math.graph.IGraphRelation;
import com.phloc.math.graph.impl.GraphNodeFast;
import com.phloc.math.graph.impl.GraphRelationFast;

/**
 * Default implementation of the {@link IGraphObjectFactory} with
 * {@link GraphNodeFast} and {@link GraphRelationFast}.
 * 
 * @author Philip Helger
 */
public class SimpleGraphObjectFastFactory implements IGraphObjectFactory
{
  @Nonnull
  public IGraphNode createNode ()
  {
    return new GraphNodeFast ();
  }

  @Nonnull
  public IGraphNode createNode (@Nullable final String sID)
  {
    return new GraphNodeFast (sID);
  }

  @Nonnull
  public IGraphRelation createRelation (@Nonnull final IGraphNode aFrom, @Nonnull final IGraphNode aTo)
  {
    return new GraphRelationFast (aFrom, aTo);
  }

  @Nonnull
  public IGraphRelation createRelation (@Nullable final String sID,
                                        @Nonnull final IGraphNode aFrom,
                                        @Nonnull final IGraphNode aTo)
  {
    return new GraphRelationFast (sID, aFrom, aTo);
  }
}
