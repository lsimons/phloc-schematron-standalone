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
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.hash.HashCodeGenerator;
import com.phloc.commons.string.ToStringGenerator;
import com.phloc.math.graph.IBaseGraph;
import com.phloc.math.graph.IBaseGraphNode;
import com.phloc.math.graph.IBaseGraphRelation;

/**
 * A simple graph object that bidirectionally links graph nodes.
 * 
 * @author Philip Helger
 */
@NotThreadSafe
public abstract class AbstractBaseGraph <N extends IBaseGraphNode <N, R>, R extends IBaseGraphRelation <N, R>> extends AbstractBaseGraphObject implements IBaseGraph <N, R>
{
  /** By default this is allowed */
  public static final boolean DEFAULT_CHANGING_CONNECTED_OBJECTS_ALLOWED = true;

  protected final Map <String, N> m_aNodes = new LinkedHashMap <String, N> ();
  private boolean m_bIsChangingConnectedObjectsAllowed = DEFAULT_CHANGING_CONNECTED_OBJECTS_ALLOWED;

  public AbstractBaseGraph (@Nullable final String sID)
  {
    super (sID);
  }

  public void setChangingConnectedObjectsAllowed (final boolean bIsChangingConnectedObjectsAllowed)
  {
    m_bIsChangingConnectedObjectsAllowed = bIsChangingConnectedObjectsAllowed;
  }

  public boolean isChangingConnectedObjectsAllowed ()
  {
    return m_bIsChangingConnectedObjectsAllowed;
  }

  @Nullable
  public N getNodeOfID (@Nullable final String sID)
  {
    return m_aNodes.get (sID);
  }

  @Nonnegative
  public int getNodeCount ()
  {
    return m_aNodes.size ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public Map <String, N> getAllNodes ()
  {
    return ContainerHelper.newOrderedMap (m_aNodes);
  }

  @Nonnull
  @ReturnsMutableCopy
  public Set <String> getAllNodeIDs ()
  {
    return ContainerHelper.newOrderedSet (m_aNodes.keySet ());
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!(o instanceof AbstractBaseGraph <?, ?>))
      return false;
    // Do not use m_eHasCycles because this is just a state variable
    final AbstractBaseGraph <?, ?> rhs = (AbstractBaseGraph <?, ?>) o;
    return m_aNodes.equals (rhs.m_aNodes);
  }

  @Override
  public int hashCode ()
  {
    // Do not use m_eHasCycles because this is just a state variable
    return new HashCodeGenerator (this).append (m_aNodes).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("nodes", m_aNodes).toString ();
  }
}
