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
package com.phloc.commons.parent;

import com.phloc.commons.hash.HashCodeGenerator;
import com.phloc.commons.id.IHasID;
import com.phloc.commons.string.ToStringGenerator;

public final class MockHasParent implements IHasParent <MockHasParent>, IHasID <String>
{
  private final String m_sID;

  public MockHasParent (final String sID)
  {
    m_sID = sID;
  }

  public String getID ()
  {
    return m_sID;
  }

  public MockHasParent getParent ()
  {
    return m_sID.length () <= 1 ? null : new MockHasParent (m_sID.substring (0, m_sID.length () - 1));
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!(o instanceof MockHasParent))
      return false;
    final MockHasParent rhs = (MockHasParent) o;
    return m_sID.equals (rhs.m_sID);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sID).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("id", m_sID).toString ();
  }
}
