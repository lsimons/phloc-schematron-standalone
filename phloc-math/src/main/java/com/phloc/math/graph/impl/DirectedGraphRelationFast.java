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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.phloc.commons.hash.HashCodeGenerator;
import com.phloc.math.graph.IDirectedGraphNode;

/**
 * Implementation of {@link com.phloc.math.graph.IDirectedGraphRelation}
 * interface with quick and dirty equals and hashCode (on ID only)
 * 
 * @author Philip Helger
 */
@NotThreadSafe
public class DirectedGraphRelationFast extends DirectedGraphRelation
{
  private Integer m_aHashCode;

  public DirectedGraphRelationFast (@Nonnull final IDirectedGraphNode aFrom, @Nonnull final IDirectedGraphNode aTo)
  {
    super (aFrom, aTo);
  }

  public DirectedGraphRelationFast (@Nullable final String sID,
                                    @Nonnull final IDirectedGraphNode aFrom,
                                    @Nonnull final IDirectedGraphNode aTo)
  {
    super (sID, aFrom, aTo);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!(o instanceof DirectedGraphRelationFast))
      return false;
    final DirectedGraphRelationFast rhs = (DirectedGraphRelationFast) o;
    return getID ().equals (rhs.getID ());
  }

  @Override
  public int hashCode ()
  {
    if (m_aHashCode == null)
      m_aHashCode = new HashCodeGenerator (this).append (getID ()).getHashCodeObj ();
    return m_aHashCode.intValue ();
  }
}
