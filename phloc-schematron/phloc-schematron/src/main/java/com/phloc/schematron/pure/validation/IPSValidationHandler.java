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
package com.phloc.schematron.pure.validation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.w3c.dom.Node;

import com.phloc.commons.state.EContinue;
import com.phloc.schematron.pure.model.PSAssertReport;
import com.phloc.schematron.pure.model.PSPattern;
import com.phloc.schematron.pure.model.PSPhase;
import com.phloc.schematron.pure.model.PSRule;
import com.phloc.schematron.pure.model.PSSchema;

/**
 * Base interface for a Schematron validation callback handler. It is only
 * invoked when validating an XML against a Schematron file.
 * 
 * @see com.phloc.schematron.pure.bound.IPSBoundSchema#validate(Node,
 *      IPSValidationHandler)
 * @author Philip Helger
 */
public interface IPSValidationHandler
{
  /**
   * This is the first method called.
   * 
   * @param aSchema
   *        The Schematron to be validated. Never <code>null</code>.
   * @param aActivePhase
   *        The selected phase, if any special phase was selected. May be
   *        <code>null</code>.
   * @see #onEnd(PSSchema, PSPhase)
   */
  void onStart (@Nonnull PSSchema aSchema, @Nullable PSPhase aActivePhase) throws SchematronValidationException;

  /**
   * This method is called for every pattern inside the schema.
   * 
   * @param aPattern
   *        The current pattern. Never <code>null</code>.
   */
  void onPattern (@Nonnull PSPattern aPattern) throws SchematronValidationException;

  /**
   * This method is called for every rule inside the current pattern.
   * 
   * @param aRule
   *        The current rule. Never <code>null</code>.
   * @param sContext
   *        The real context to be used in validation. May differ from the
   *        result of {@link PSRule#getContext()} because of replaced variables
   *        from &lt;let&gt; elements.
   */
  void onRule (@Nonnull PSRule aRule, @Nonnull String sContext) throws SchematronValidationException;

  /**
   * This method is called for every failed assert.
   * 
   * @param aAssertReport
   *        The current assert element. Never <code>null</code>.
   * @param sTestExpression
   *        The source XPath expression that was evaluated for this node. It may
   *        be different from the test expression contained in the passed
   *        assert/report element because of replaced &lt;let&gt; elements.
   *        Never <code>null</code>.
   * @param aRuleMatchingNode
   *        The XML node of the document to be validated.
   * @param nNodeIndex
   *        The index of the matched node, relative to the current rule.
   * @param aContext
   *        A context object - implementation dependent. For the default query
   *        binding this is e.g. an
   *        {@link com.phloc.schematron.pure.bound.xpath.PSXPathBoundAssertReport}
   *        object.
   * @return {@link EContinue#BREAK} to stop validating immediately.
   */
  @Nonnull
  EContinue onFailedAssert (@Nonnull PSAssertReport aAssertReport,
                            @Nonnull String sTestExpression,
                            @Nonnull Node aRuleMatchingNode,
                            @Nonnull int nNodeIndex,
                            @Nullable Object aContext) throws SchematronValidationException;

  /**
   * This method is called for every failed assert.
   * 
   * @param aAssertReport
   *        The current assert element. Never <code>null</code>.
   * @param sTestExpression
   *        The source XPath expression that was evaluated for this node. It may
   *        be different from the test expression contained in the passed
   *        assert/report element because of replaced &lt;let&gt; elements.
   *        Never <code>null</code>.
   * @param aRuleMatchingNode
   *        The XML node of the document to be validated.
   * @param nNodeIndex
   *        The index of the matched node, relative to the current rule.
   * @param aContext
   *        A context object - implementation dependent. For the default query
   *        binding this is e.g. an
   *        {@link com.phloc.schematron.pure.bound.xpath.PSXPathBoundAssertReport}
   *        object.
   * @return {@link EContinue#BREAK} to stop validating immediately.
   */
  @Nonnull
  EContinue onSuccessfulReport (@Nonnull PSAssertReport aAssertReport,
                                @Nonnull String sTestExpression,
                                @Nonnull Node aRuleMatchingNode,
                                @Nonnull int nNodeIndex,
                                @Nullable Object aContext) throws SchematronValidationException;

  /**
   * This is the last method called. It indicates that the validation for the
   * current scheme ended.
   * 
   * @param aSchema
   *        The Schematron that was be validated. Never <code>null</code>.
   * @param aActivePhase
   *        The selected phase, if any special phase was selected. May be
   *        <code>null</code>.
   * @see #onStart(PSSchema, PSPhase)
   */
  void onEnd (@Nonnull PSSchema aSchema, @Nullable PSPhase aActivePhase) throws SchematronValidationException;
}
