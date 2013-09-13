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
package com.phloc.commons.typeconvert.rule;

import javax.annotation.Nonnull;

import com.phloc.commons.string.ToStringGenerator;
import com.phloc.commons.typeconvert.ITypeConverterRule;

/**
 * Abstract type converter rule implementation
 * 
 * @author Philip Helger
 */
public abstract class AbstractTypeConverterRule implements ITypeConverterRule
{
  private final ESubType m_eSubType;

  public AbstractTypeConverterRule (@Nonnull final ITypeConverterRule.ESubType eSubType)
  {
    if (eSubType == null)
      throw new NullPointerException ("subType");
    m_eSubType = eSubType;
  }

  @Nonnull
  public final ESubType getSubType ()
  {
    return m_eSubType;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("subType", m_eSubType).toString ();
  }
}
