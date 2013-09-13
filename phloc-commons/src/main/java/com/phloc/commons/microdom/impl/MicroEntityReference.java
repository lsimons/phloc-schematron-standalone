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
package com.phloc.commons.microdom.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.microdom.EMicroNodeType;
import com.phloc.commons.microdom.IMicroEntityReference;
import com.phloc.commons.microdom.IMicroNode;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.string.ToStringGenerator;

/**
 * Default implementation of the {@link IMicroEntityReference} interface.
 * 
 * @author Philip Helger
 */
@Immutable
public final class MicroEntityReference extends AbstractMicroNode implements IMicroEntityReference
{
  private final String m_sName;

  public MicroEntityReference (@Nonnull @Nonempty final String sName)
  {
    if (StringHelper.hasNoText (sName))
      throw new IllegalArgumentException ("The passed entity reference name is invalid");
    m_sName = sName;
  }

  @Nonnull
  public EMicroNodeType getType ()
  {
    return EMicroNodeType.ENTITY_REFERENCE;
  }

  @Nonnull
  @Nonempty
  public String getNodeName ()
  {
    return '&' + m_sName + ';';
  }

  @Override
  @Nonnull
  @Nonempty
  public String getNodeValue ()
  {
    return m_sName;
  }

  @Nonnull
  @Nonempty
  public String getName ()
  {
    return m_sName;
  }

  @Nonnull
  public IMicroEntityReference getClone ()
  {
    return new MicroEntityReference (m_sName);
  }

  public boolean isEqualContent (@Nullable final IMicroNode o)
  {
    if (o == this)
      return true;
    if (!(o instanceof MicroEntityReference))
      return false;
    final MicroEntityReference rhs = (MicroEntityReference) o;
    return m_sName.equals (rhs.m_sName);
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("name", m_sName).toString ();
  }
}
