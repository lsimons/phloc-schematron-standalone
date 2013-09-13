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

/**
 * Abstract type converter than can convert from a base source class to a
 * destination class. Example from String.class to specific Enum.class
 * 
 * @author Philip Helger
 */
public abstract class AbstractTypeConverterRuleFixedSourceAssignableDestination extends AbstractTypeConverterRule
{
  private final Class <?> m_aSrcClass;
  private final Class <?> m_aDstClass;

  public AbstractTypeConverterRuleFixedSourceAssignableDestination (@Nonnull final Class <?> aSrcClass,
                                                                    @Nonnull final Class <?> aDstClass)
  {
    super (ESubType.FIXED_SRC_ASSIGNABLE_DST);
    if (aSrcClass == null)
      throw new NullPointerException ("srcClass");
    if (aDstClass == null)
      throw new NullPointerException ("dstClass");
    m_aSrcClass = aSrcClass;
    m_aDstClass = aDstClass;
  }

  public final boolean canConvert (@Nonnull final Class <?> aSrcClass, @Nonnull final Class <?> aDstClass)
  {
    return m_aSrcClass.equals (aSrcClass) && m_aDstClass.isAssignableFrom (aDstClass);
  }

  @Nonnull
  public final Class <?> getSourceClass ()
  {
    return m_aSrcClass;
  }

  @Nonnull
  public final Class <?> getDestinationClass ()
  {
    return m_aDstClass;
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("srcClass", m_aSrcClass.getName ())
                            .append ("dstClass", m_aDstClass.getName ())
                            .toString ();
  }
}
