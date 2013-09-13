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
import javax.annotation.concurrent.NotThreadSafe;

import org.w3c.dom.Node;

import com.phloc.commons.state.EContinue;
import com.phloc.commons.state.EValidity;
import com.phloc.schematron.pure.model.PSAssertReport;

/**
 * A simple implementation if {@link IPSValidationHandler} that stops validation
 * upon the first error (the first failed assert or the first successful
 * report). The final validation result can be retrieved by invoking
 * {@link #getValidity()}.
 * 
 * @author Philip Helger
 */
@NotThreadSafe
public class PSValidationHandlerBreakOnFirstError extends PSValidationHandlerDefault
{
  private EValidity m_eValidity = EValidity.VALID;

  @Override
  @Nonnull
  public EContinue onFailedAssert (@Nonnull final PSAssertReport aAssertReport,
                                   @Nonnull final String sTestExpression,
                                   @Nonnull final Node aRuleMatchingNode,
                                   @Nonnull final int nNodeIndex,
                                   @Nullable final Object aContext)
  {
    m_eValidity = EValidity.INVALID;
    return EContinue.BREAK;
  }

  @Override
  @Nonnull
  public EContinue onSuccessfulReport (@Nonnull final PSAssertReport aAssertReport,
                                       @Nonnull final String sTestExpression,
                                       @Nonnull final Node aRuleMatchingNode,
                                       @Nonnull final int nNodeIndex,
                                       @Nullable final Object aContext)
  {
    m_eValidity = EValidity.INVALID;
    return EContinue.BREAK;
  }

  /**
   * @return The validity of the XML file. {@link EValidity#VALID} if no failed
   *         assertion and no successful report occurred,
   *         {@link EValidity#INVALID} otherwise.
   */
  @Nonnull
  public EValidity getValidity ()
  {
    return m_eValidity;
  }
}
