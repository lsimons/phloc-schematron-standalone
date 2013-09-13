/**
 * Copyright (C) 2013 phloc systems
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
package com.phloc.schematron.pure.bound.xpath;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.xml.xpath.XPathExpression;

import com.phloc.commons.string.ToStringGenerator;
import com.phloc.schematron.pure.model.IPSElement;

/**
 * This class represents a single XPath-bound text element that is contained
 * inside an assert- or report-element.
 * 
 * @author Philip Helger
 */
@Immutable
public class PSXPathBoundElement
{
  private final Object m_aElement;
  private final String m_sExpression;
  private final XPathExpression m_aBoundExpression;

  public PSXPathBoundElement (@Nonnull final Object aElement)
  {
    this (aElement, null, null);
  }

  public PSXPathBoundElement (@Nonnull final Object aElement,
                              @Nullable final String sExpression,
                              @Nullable final XPathExpression aBoundExpression)
  {
    if (aElement == null)
      throw new NullPointerException ("Element");
    m_aElement = aElement;
    m_sExpression = sExpression;
    m_aBoundExpression = aBoundExpression;
  }

  /**
   * @return {@link String} or {@link IPSElement} objects. May not be
   *         <code>null</code>.
   */
  @Nonnull
  public Object getElement ()
  {
    return m_aElement;
  }

  /**
   * @return The source expression that was compiled to an
   *         {@link XPathExpression}. It may differ from the XPath expression
   *         contained in the element because of replaced variables from
   *         &lt;let&gt; elements. May be <code>null</code> if
   *         {@link #getExpression()} is <code>null</code>.
   */
  @Nullable
  public String getExpression ()
  {
    return m_sExpression;
  }

  /**
   * @return The compiled {@link XPathExpression} - may be <code>null</code>.
   */
  @Nullable
  public XPathExpression getBoundExpression ()
  {
    return m_aBoundExpression;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("element", m_aElement)
                                       .appendIfNotNull ("expression", m_sExpression)
                                       .appendIfNotNull ("boundExpression", m_aBoundExpression)
                                       .toString ();
  }
}
