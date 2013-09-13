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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
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
import com.phloc.math.graph.IDirectedGraphNode;
import com.phloc.math.graph.IDirectedGraphRelation;

/**
 * Default implementation if the {@link IDirectedGraphNode} interface
 * 
 * @author Philip Helger
 */
@NotThreadSafe
public class DirectedGraphNode extends AbstractBaseGraphObject implements IDirectedGraphNode
{
  private Map <String, IDirectedGraphRelation> m_aIncoming;
  private Map <String, IDirectedGraphRelation> m_aOutgoing;

  public DirectedGraphNode ()
  {
    this (null);
  }

  public DirectedGraphNode (@Nullable final String sID)
  {
    super (sID);
  }

  public final boolean isDirected ()
  {
    return true;
  }

  public void addIncomingRelation (@Nonnull final IDirectedGraphRelation aNewRelation)
  {
    if (aNewRelation == null)
      throw new NullPointerException ("relation");
    if (aNewRelation.getTo () != this)
      throw new IllegalArgumentException ("Passed incoming relation is not based on this node");
    if (m_aIncoming != null)
    {
      if (m_aIncoming.containsKey (aNewRelation.getID ()))
        throw new IllegalArgumentException ("The passed relation (" +
                                            aNewRelation +
                                            ") is already contained as an incoming relation");

      // check if the relation from-node is already contained
      for (final IDirectedGraphRelation aRelation : m_aIncoming.values ())
        if (aRelation.getFrom () == aNewRelation.getFrom ())
          throw new IllegalArgumentException ("The from-node of the passed relation (" +
                                              aNewRelation +
                                              ") is already contained");
    }
    else
    {
      m_aIncoming = new LinkedHashMap <String, IDirectedGraphRelation> ();
    }

    // Add!
    m_aIncoming.put (aNewRelation.getID (), aNewRelation);
  }

  public boolean hasIncomingRelations ()
  {
    return ContainerHelper.isNotEmpty (m_aIncoming);
  }

  @Nonnegative
  public int getIncomingRelationCount ()
  {
    return ContainerHelper.getSize (m_aIncoming);
  }

  public boolean isIncomingRelation (@Nullable final IDirectedGraphRelation aRelation)
  {
    return m_aIncoming != null && aRelation != null && aRelation.equals (m_aIncoming.get (aRelation.getID ()));
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <IDirectedGraphRelation> getAllIncomingRelations ()
  {
    return m_aIncoming == null ? new ArrayList <IDirectedGraphRelation> ()
                              : ContainerHelper.newList (m_aIncoming.values ());
  }

  @Nonnull
  public EChange removeIncomingRelation (@Nonnull final IDirectedGraphRelation aRelation)
  {
    return aRelation == null || m_aIncoming == null ? EChange.UNCHANGED
                                                   : EChange.valueOf (m_aIncoming.remove (aRelation.getID ()) != null);
  }

  @Nonnull
  public EChange removeAllIncomingRelations ()
  {
    if (!hasIncomingRelations ())
      return EChange.UNCHANGED;
    m_aIncoming = null;
    return EChange.CHANGED;
  }

  public boolean isFromNode (@Nullable final IDirectedGraphNode aNode)
  {
    return getIncomingRelationFrom (aNode) != null;
  }

  @Nonnull
  @ReturnsMutableCopy
  public Set <IDirectedGraphNode> getAllFromNodes ()
  {
    final Set <IDirectedGraphNode> ret = new HashSet <IDirectedGraphNode> ();
    if (m_aIncoming != null)
      for (final IDirectedGraphRelation aRelation : m_aIncoming.values ())
        ret.add (aRelation.getFrom ());
    return ret;
  }

  @Nullable
  public IDirectedGraphRelation getIncomingRelationFrom (@Nullable final IDirectedGraphNode aFromNode)
  {
    if (m_aIncoming != null && aFromNode != null)
      for (final IDirectedGraphRelation aRelation : m_aIncoming.values ())
        if (aRelation.getFrom ().equals (aFromNode))
          return aRelation;
    return null;
  }

  public void addOutgoingRelation (@Nonnull final IDirectedGraphRelation aNewRelation)
  {
    if (aNewRelation == null)
      throw new NullPointerException ("relation");
    if (aNewRelation.getFrom () != this)
      throw new IllegalArgumentException ("Passed outgoing relation is not based on this node");
    if (m_aOutgoing != null)
    {
      if (m_aOutgoing.containsKey (aNewRelation.getID ()))
        throw new IllegalArgumentException ("The passed relation " +
                                            aNewRelation +
                                            " is already contained as an outgoing relation");
      // check if the relation to-node is already contained
      for (final IDirectedGraphRelation aRelation : m_aOutgoing.values ())
        if (aRelation.getTo () == aNewRelation.getTo ())
          throw new IllegalArgumentException ("The to-node of the passed relation " +
                                              aNewRelation +
                                              " is already contained");
    }
    else
    {
      m_aOutgoing = new LinkedHashMap <String, IDirectedGraphRelation> ();
    }

    // Add!
    m_aOutgoing.put (aNewRelation.getID (), aNewRelation);
  }

  public boolean hasOutgoingRelations ()
  {
    return ContainerHelper.isNotEmpty (m_aOutgoing);
  }

  @Nonnegative
  public int getOutgoingRelationCount ()
  {
    return ContainerHelper.getSize (m_aOutgoing);
  }

  public boolean isOutgoingRelation (@Nullable final IDirectedGraphRelation aRelation)
  {
    return m_aOutgoing != null && aRelation != null && aRelation.equals (m_aOutgoing.get (aRelation.getID ()));
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <IDirectedGraphRelation> getAllOutgoingRelations ()
  {
    return m_aOutgoing == null ? new ArrayList <IDirectedGraphRelation> ()
                              : ContainerHelper.newList (m_aOutgoing.values ());
  }

  @Nonnull
  @ReturnsMutableCopy
  public Set <IDirectedGraphNode> getAllToNodes ()
  {
    final Set <IDirectedGraphNode> ret = new HashSet <IDirectedGraphNode> ();
    if (m_aOutgoing != null)
      for (final IDirectedGraphRelation aRelation : m_aOutgoing.values ())
        ret.add (aRelation.getTo ());
    return ret;
  }

  @Nonnull
  public EChange removeOutgoingRelation (@Nonnull final IDirectedGraphRelation aRelation)
  {
    return aRelation == null || m_aOutgoing == null ? EChange.UNCHANGED
                                                   : EChange.valueOf (m_aOutgoing.remove (aRelation.getID ()) != null);
  }

  @Nonnull
  public EChange removeAllOutgoingRelations ()
  {
    if (!hasOutgoingRelations ())
      return EChange.UNCHANGED;
    m_aOutgoing = null;
    return EChange.CHANGED;
  }

  public boolean isToNode (@Nullable final IDirectedGraphNode aNode)
  {
    return getOutgoingRelationTo (aNode) != null;
  }

  @Nullable
  public IDirectedGraphRelation getOutgoingRelationTo (@Nullable final IDirectedGraphNode aToNode)
  {
    if (m_aOutgoing != null && aToNode != null)
      for (final IDirectedGraphRelation aRelation : m_aOutgoing.values ())
        if (aRelation.getTo ().equals (aToNode))
          return aRelation;
    return null;
  }

  public boolean isConnectedWith (@Nullable final IDirectedGraphNode aNode)
  {
    if (aNode == null)
      return false;
    return getIncomingRelationFrom (aNode) != null || getOutgoingRelationTo (aNode) != null;
  }

  @Nullable
  public IDirectedGraphRelation getRelation (@Nullable final IDirectedGraphNode aNode)
  {
    if (aNode == null)
      return null;
    final IDirectedGraphRelation aIncoming = getIncomingRelationFrom (aNode);
    final IDirectedGraphRelation aOutgoing = getOutgoingRelationTo (aNode);
    if (aIncoming != null && aOutgoing != null)
      throw new IllegalStateException ("Both incoming and outgoing relations between node '" +
                                       getID () +
                                       "' and '" +
                                       aNode.getID () +
                                       "' exist!");
    return aIncoming != null ? aIncoming : aOutgoing;
  }

  public boolean hasRelations ()
  {
    return hasIncomingOrOutgoingRelations ();
  }

  public boolean hasIncomingOrOutgoingRelations ()
  {
    return hasIncomingRelations () || hasOutgoingRelations ();
  }

  public boolean hasIncomingAndOutgoingRelations ()
  {
    return hasIncomingRelations () && hasOutgoingRelations ();
  }

  @Nonnegative
  public int getRelationCount ()
  {
    return getIncomingRelationCount () + getOutgoingRelationCount ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public Set <IDirectedGraphRelation> getAllRelations ()
  {
    final Set <IDirectedGraphRelation> ret = new LinkedHashSet <IDirectedGraphRelation> ();
    if (m_aIncoming != null)
      ret.addAll (m_aIncoming.values ());
    if (m_aOutgoing != null)
      ret.addAll (m_aOutgoing.values ());
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public Set <String> getAllRelationIDs ()
  {
    final Set <String> ret = new LinkedHashSet <String> ();
    if (m_aIncoming != null)
      ret.addAll (m_aIncoming.keySet ());
    if (m_aOutgoing != null)
      ret.addAll (m_aOutgoing.keySet ());
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public Set <IDirectedGraphNode> getAllRelatedNodes ()
  {
    final Set <IDirectedGraphNode> ret = new LinkedHashSet <IDirectedGraphNode> ();
    if (m_aIncoming != null)
      for (final IDirectedGraphRelation aRelation : m_aIncoming.values ())
        ret.add (aRelation.getFrom ());
    if (m_aOutgoing != null)
      for (final IDirectedGraphRelation aRelation : m_aOutgoing.values ())
        ret.add (aRelation.getTo ());
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public Set <String> getAllRelatedNodeIDs ()
  {
    final Set <String> ret = new LinkedHashSet <String> ();
    if (m_aIncoming != null)
      for (final IDirectedGraphRelation aRelation : m_aIncoming.values ())
        ret.add (aRelation.getFromID ());
    if (m_aOutgoing != null)
      for (final IDirectedGraphRelation aRelation : m_aOutgoing.values ())
        ret.add (aRelation.getToID ());
    return ret;
  }

  @Nonnull
  public EChange removeAllRelations ()
  {
    EChange ret = EChange.UNCHANGED;
    ret = ret.or (removeAllIncomingRelations ());
    ret = ret.or (removeAllOutgoingRelations ());
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
                            .append ("incomingIDs", m_aIncoming == null ? null : m_aIncoming.keySet ())
                            .append ("outgoingIDs", m_aOutgoing == null ? null : m_aOutgoing.keySet ())
                            .toString ();
  }
}
