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

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.hash.HashCodeGenerator;
import com.phloc.commons.string.ToStringGenerator;
import com.phloc.math.graph.IGraphNode;
import com.phloc.math.graph.IGraphRelation;

/**
 * Default implementation of the {@link IGraphRelation} interface
 * 
 * @author Philip Helger
 */
@NotThreadSafe
public class GraphRelation extends AbstractBaseGraphObject implements IGraphRelation
{
  private final IGraphNode m_aNode1;
  private final IGraphNode m_aNode2;

  public GraphRelation (@Nonnull final IGraphNode aNode1, @Nonnull final IGraphNode aNode2)
  {
    this (null, aNode1, aNode2);
  }

  public GraphRelation (@Nullable final String sID, @Nonnull final IGraphNode aNode1, @Nonnull final IGraphNode aNode2)
  {
    super (sID);
    if (aNode1 == null)
      throw new NullPointerException ("node1");
    if (aNode2 == null)
      throw new NullPointerException ("node2");
    m_aNode1 = aNode1;
    m_aNode2 = aNode2;
  }

  public final boolean isDirected ()
  {
    return false;
  }

  public boolean isRelatedTo (@Nullable final IGraphNode aNode)
  {
    return aNode != null && (m_aNode1.equals (aNode) || m_aNode2.equals (aNode));
  }

  @Nonnull
  @ReturnsMutableCopy
  public Set <IGraphNode> getAllConnectedNodes ()
  {
    return ContainerHelper.newSet (m_aNode1, m_aNode2);
  }

  @Nonnull
  @ReturnsMutableCopy
  public Set <String> getAllConnectedNodeIDs ()
  {
    return ContainerHelper.newSet (m_aNode1.getID (), m_aNode2.getID ());
  }

  @Nonnull
  public IGraphNode getNode1 ()
  {
    return m_aNode1;
  }

  @Nonnull
  public String getNode1ID ()
  {
    return m_aNode1.getID ();
  }

  @Nonnull
  public IGraphNode getNode2 ()
  {
    return m_aNode2;
  }

  @Nonnull
  public String getNode2ID ()
  {
    return m_aNode2.getID ();
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!super.equals (o))
      return false;
    final GraphRelation rhs = (GraphRelation) o;
    return m_aNode1.equals (rhs.m_aNode1) && m_aNode2.equals (rhs.m_aNode2);
  }

  @Override
  public int hashCode ()
  {
    return HashCodeGenerator.getDerived (super.hashCode ()).append (m_aNode1).append (m_aNode2).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("node1", m_aNode1)
                            .append ("node2", m_aNode2)
                            .toString ();
  }
}
