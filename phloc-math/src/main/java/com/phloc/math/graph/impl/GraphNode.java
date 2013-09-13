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
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.state.EChange;
import com.phloc.commons.string.ToStringGenerator;
import com.phloc.math.graph.IGraphNode;
import com.phloc.math.graph.IGraphRelation;

/**
 * Default implementation if the {@link IGraphNode} interface
 * 
 * @author Philip Helger
 */
@NotThreadSafe
public class GraphNode extends AbstractBaseGraphObject implements IGraphNode
{
  private Map <String, IGraphRelation> m_aRelations;

  public GraphNode ()
  {
    this (null);
  }

  public GraphNode (@Nullable final String sID)
  {
    super (sID);
  }

  public final boolean isDirected ()
  {
    return false;
  }

  @Nonnull
  public EChange addRelation (@Nullable final IGraphRelation aRelation)
  {
    if (aRelation == null)
      return EChange.UNCHANGED;
    if (!aRelation.isRelatedTo (this))
      throw new IllegalArgumentException ("Relation is not suitable for this node!");

    final String sRelationID = aRelation.getID ();
    if (m_aRelations == null)
      m_aRelations = new LinkedHashMap <String, IGraphRelation> ();
    else
      if (m_aRelations.containsKey (sRelationID))
        return EChange.UNCHANGED;

    m_aRelations.put (sRelationID, aRelation);
    return EChange.CHANGED;
  }

  @Nonnull
  public EChange removeRelation (@Nullable final IGraphRelation aRelation)
  {
    if (aRelation == null || m_aRelations == null)
      return EChange.UNCHANGED;
    return EChange.valueOf (m_aRelations.remove (aRelation.getID ()) != null);
  }

  @Nonnull
  public EChange removeAllRelations ()
  {
    if (!hasRelations ())
      return EChange.UNCHANGED;
    m_aRelations = null;
    return EChange.CHANGED;
  }

  public boolean isConnectedWith (@Nullable final IGraphNode aNode)
  {
    return getRelation (aNode) != null;
  }

  @Nullable
  public IGraphRelation getRelation (@Nullable final IGraphNode aNode)
  {
    if (m_aRelations != null && aNode != null && aNode != this)
    {
      for (final IGraphRelation aRelation : m_aRelations.values ())
        if (aRelation.isRelatedTo (aNode))
          return aRelation;
    }
    return null;
  }

  public boolean hasRelations ()
  {
    return ContainerHelper.isNotEmpty (m_aRelations);
  }

  @Nonnegative
  public int getRelationCount ()
  {
    return ContainerHelper.getSize (m_aRelations);
  }

  @Nonnull
  @ReturnsMutableCopy
  public Set <IGraphRelation> getAllRelations ()
  {
    final Set <IGraphRelation> ret = new LinkedHashSet <IGraphRelation> ();
    if (m_aRelations != null)
      ret.addAll (m_aRelations.values ());
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public Set <String> getAllRelationIDs ()
  {
    final Set <String> ret = new LinkedHashSet <String> ();
    if (m_aRelations != null)
      ret.addAll (m_aRelations.keySet ());
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public Set <IGraphNode> getAllRelatedNodes ()
  {
    final Set <IGraphNode> ret = new LinkedHashSet <IGraphNode> ();
    if (m_aRelations != null)
      for (final IGraphRelation aRelation : m_aRelations.values ())
      {
        ret.add (aRelation.getNode1 ());
        ret.add (aRelation.getNode2 ());
      }
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public Set <String> getAllRelatedNodeIDs ()
  {
    final Set <String> ret = new LinkedHashSet <String> ();
    if (m_aRelations != null)
      for (final IGraphRelation aRelation : m_aRelations.values ())
      {
        ret.add (aRelation.getNode1ID ());
        ret.add (aRelation.getNode2ID ());
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

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("relationIDs", m_aRelations == null ? null : m_aRelations.keySet ())
                            .toString ();
  }
}
