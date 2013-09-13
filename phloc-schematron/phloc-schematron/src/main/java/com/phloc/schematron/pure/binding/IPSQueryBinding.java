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
package com.phloc.schematron.pure.binding;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.schematron.SchematronException;
import com.phloc.schematron.pure.bound.IPSBoundSchema;
import com.phloc.schematron.pure.errorhandler.IPSErrorHandler;
import com.phloc.schematron.pure.model.PSAssertReport;
import com.phloc.schematron.pure.model.PSParam;
import com.phloc.schematron.pure.model.PSRule;
import com.phloc.schematron.pure.model.PSSchema;
import com.phloc.schematron.pure.model.PSValueOf;

/**
 * Base interface for a single query binding.
 * 
 * @author Philip Helger
 */
public interface IPSQueryBinding extends Serializable
{
  // --- requirements to create a minimal syntax/pre-process ---

  /**
   * Negate the passed test statement. This is required in the creation of a
   * minified Schematron, when report elements are converted to assert elements.
   * 
   * @param sTest
   *        The test expression.
   * @return The negated test expression
   */
  String getNegatedTestExpression (@Nonnull String sTest);

  /**
   * Convert the passed list of {@link PSParam} elements to a map suitable for
   * String replacement. This is needed to resolve placeholders in abstract
   * patterns. The default query binding e.g. adds a "$" in front of each
   * parameter name. The so created map is used to resolve abstract rule and
   * pattern data to real values.
   * 
   * @param aParams
   *        Source list. May not be <code>null</code>.
   * @return Non-<code>null</code> String replacement map.
   */
  @Nonnull
  @ReturnsMutableCopy
  Map <String, String> getStringReplacementMap (@Nonnull List <PSParam> aParams);

  /**
   * Apply the Map created by {@link #getNegatedTestExpression(String)} on a
   * single string.<br>
   * According to iso_abstract_expand.xsl, line 233 the text replacements happen
   * for the following attributes:
   * <ul>
   * <li>test - only in {@link PSAssertReport}</li>
   * <li>context - only in {@link PSRule}</li>
   * <li>select - only in {@link PSValueOf}</li>
   * </ul>
   * As an experimental option in line 244 the replacement is also applied to
   * all text nodes. This is currently not supported!
   * 
   * @param sText
   *        The original text. May be <code>null</code>.
   * @param aStringReplacements
   *        All replacements as map from source to target. The map should be
   *        ordered by longest keys first.
   * @return <code>null</code> if the input string was <code>null</code>.
   */
  @Nullable
  String getWithParamTextsReplaced (@Nullable String sText, @Nullable Map <String, String> aStringReplacements);

  // --- requirements for compilation ---

  /**
   * Create a bound schema, which is like a precompiled schema.
   * 
   * @param aSchema
   *        The schema to be bound. May not be <code>null</code>.
   * @param sPhase
   *        The phase to use. May be <code>null</code>. If it is
   *        <code>null</code> than the defaultPhase is used that is defined in
   *        the schema. If no defaultPhase is present, than all patterns are
   *        evaluated.
   * @param aCustomErrorHandler
   *        An optional custom error handler to use. May be <code>null</code>.
   * @return The precompiled, bound schema. Never <code>null</code>.
   * @throws SchematronException
   *         In case of a binding error
   */
  @Nonnull
  IPSBoundSchema bind (@Nonnull PSSchema aSchema, @Nullable String sPhase, @Nullable IPSErrorHandler aCustomErrorHandler) throws SchematronException;
}
