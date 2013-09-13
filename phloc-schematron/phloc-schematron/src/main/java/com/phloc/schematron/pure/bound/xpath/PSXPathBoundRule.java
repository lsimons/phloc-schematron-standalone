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

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.xml.xpath.XPathExpression;

import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.string.ToStringGenerator;
import com.phloc.schematron.pure.model.PSRule;

/**
 * This class represents a single XPath-bound rule-element.
 * 
 * @author Philip Helger
 */
@Immutable
public class PSXPathBoundRule
{
  private final PSRule m_aRule;
  private final String m_sRuleExpression;
  private final XPathExpression m_aBoundRuleExpression;
  private final List <PSXPathBoundAssertReport> m_aBoundAssertReports;

  public PSXPathBoundRule (@Nonnull final PSRule aRule,
                           @Nonnull final String sRuleExpression,
                           @Nonnull final XPathExpression aBoundRuleExpression,
                           @Nonnull final List <PSXPathBoundAssertReport> aBoundAssertReports)
  {
    if (aRule == null)
      throw new NullPointerException ("Rule");
    if (StringHelper.hasNoText (sRuleExpression))
      throw new IllegalArgumentException ("RuleExpression");
    if (aBoundRuleExpression == null)
      throw new NullPointerException ("BoundRuleExpression");
    if (aBoundAssertReports == null)
      throw new NullPointerException ("BoundAssertReports");
    m_aRule = aRule;
    m_sRuleExpression = sRuleExpression;
    m_aBoundRuleExpression = aBoundRuleExpression;
    m_aBoundAssertReports = aBoundAssertReports;
  }

  @Nonnull
  public PSRule getRule ()
  {
    return m_aRule;
  }

  @Nonnull
  public String getRuleExpression ()
  {
    return m_sRuleExpression;
  }

  @Nonnull
  public XPathExpression getBoundRuleExpression ()
  {
    return m_aBoundRuleExpression;
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <PSXPathBoundAssertReport> getAllBoundAssertReports ()
  {
    return ContainerHelper.newList (m_aBoundAssertReports);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("rule", m_aRule)
                                       .append ("ruleExpression", m_sRuleExpression)
                                       .append ("boundRuleExpression", m_aBoundRuleExpression)
                                       .append ("boundAssertReports", m_aBoundAssertReports)
                                       .toString ();
  }
}
