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
import com.phloc.math.graph.IDirectedGraphNode;
import com.phloc.math.graph.IDirectedGraphRelation;

/**
 * Default implementation of the {@link IDirectedGraphRelation} interface
 * 
 * @author Philip Helger
 */
@NotThreadSafe
public class DirectedGraphRelation extends AbstractBaseGraphObject implements IDirectedGraphRelation
{
  private final IDirectedGraphNode m_aFrom;
  private final IDirectedGraphNode m_aTo;

  public DirectedGraphRelation (@Nonnull final IDirectedGraphNode aFrom, @Nonnull final IDirectedGraphNode aTo)
  {
    this (null, aFrom, aTo);
  }

  public DirectedGraphRelation (@Nullable final String sID,
                                @Nonnull final IDirectedGraphNode aFrom,
                                @Nonnull final IDirectedGraphNode aTo)
  {
    super (sID);
    if (aFrom == null)
      throw new NullPointerException ("from");
    if (aTo == null)
      throw new NullPointerException ("to");
    m_aFrom = aFrom;
    m_aTo = aTo;
  }

  public final boolean isDirected ()
  {
    return true;
  }

  public boolean isRelatedTo (@Nullable final IDirectedGraphNode aNode)
  {
    return m_aFrom.equals (aNode) || m_aTo.equals (aNode);
  }

  @Nonnull
  @ReturnsMutableCopy
  public Set <IDirectedGraphNode> getAllConnectedNodes ()
  {
    return ContainerHelper.newSet (m_aFrom, m_aTo);
  }

  @Nonnull
  @ReturnsMutableCopy
  public Set <String> getAllConnectedNodeIDs ()
  {
    return ContainerHelper.newSet (m_aFrom.getID (), m_aTo.getID ());
  }

  @Nonnull
  public IDirectedGraphNode getFrom ()
  {
    return m_aFrom;
  }

  @Nonnull
  public String getFromID ()
  {
    return m_aFrom.getID ();
  }

  @Nonnull
  public IDirectedGraphNode getTo ()
  {
    return m_aTo;
  }

  @Nonnull
  public String getToID ()
  {
    return m_aTo.getID ();
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!super.equals (o))
      return false;
    final DirectedGraphRelation rhs = (DirectedGraphRelation) o;
    return m_aFrom.equals (rhs.m_aFrom) && m_aTo.equals (rhs.m_aTo);
  }

  @Override
  public int hashCode ()
  {
    return HashCodeGenerator.getDerived (super.hashCode ()).append (m_aFrom).append (m_aTo).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("from", m_aFrom).append ("to", m_aTo).toString ();
  }
}
