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
package com.phloc.schematron.pure.bound;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.w3c.dom.Node;

import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.state.EValidity;
import com.phloc.commons.xml.namespace.MapBasedNamespaceContext;
import com.phloc.schematron.CSchematron;
import com.phloc.schematron.pure.binding.IPSQueryBinding;
import com.phloc.schematron.pure.errorhandler.IPSErrorHandler;
import com.phloc.schematron.pure.errorhandler.LoggingPSErrorHandler;
import com.phloc.schematron.pure.model.IPSElement;
import com.phloc.schematron.pure.model.PSActive;
import com.phloc.schematron.pure.model.PSPattern;
import com.phloc.schematron.pure.model.PSPhase;
import com.phloc.schematron.pure.model.PSSchema;
import com.phloc.schematron.pure.validation.PSValidationHandlerBreakOnFirstError;
import com.phloc.schematron.pure.validation.SchematronValidationException;

/**
 * Base implementation of {@link IPSBoundSchema} with all common elements.
 * 
 * @author Philip Helger
 */
public abstract class AbstractPSBoundSchema implements IPSBoundSchema
{
  private final IPSQueryBinding m_aQueryBinding;
  private final PSSchema m_aOrigSchema;
  private final IPSErrorHandler m_aErrorHandler;
  private final MapBasedNamespaceContext m_aNamespaceContext;
  private final String m_sPhase;
  private final PSPhase m_aPhase;
  private final List <PSPattern> m_aPatterns = new ArrayList <PSPattern> ();

  public AbstractPSBoundSchema (@Nonnull final IPSQueryBinding aQueryBinding,
                                @Nonnull final PSSchema aOrigSchema,
                                @Nullable final String sPhase,
                                @Nullable final IPSErrorHandler aCustomErrorHandler)
  {
    if (aQueryBinding == null)
      throw new NullPointerException ("QueryBinding");
    if (aOrigSchema == null)
      throw new NullPointerException ("OrigSchema");
    m_aQueryBinding = aQueryBinding;
    m_aOrigSchema = aOrigSchema;
    m_aErrorHandler = aCustomErrorHandler != null ? aCustomErrorHandler : new LoggingPSErrorHandler ();

    // Determine all namespaces of the schema
    m_aNamespaceContext = aOrigSchema.getAsNamespaceContext ();

    // Determine the phase ID to use
    String sRealPhase = sPhase != null ? sPhase : CSchematron.PHASE_DEFAULT;
    if (sRealPhase.equals (CSchematron.PHASE_DEFAULT))
    {
      sRealPhase = aOrigSchema.getDefaultPhase ();
      if (sRealPhase == null)
        sRealPhase = CSchematron.PHASE_ALL;
    }
    if (!sRealPhase.equals (CSchematron.PHASE_ALL))
    {
      m_aPhase = aOrigSchema.getPhaseOfID (sRealPhase);
      if (m_aPhase == null)
        warn (aOrigSchema, "Failed to resolve phase with ID '" + sRealPhase + "' - default to all patterns");
    }
    else
      m_aPhase = null;
    m_sPhase = sRealPhase;

    // Determine all patterns of the phase to use
    if (m_aPhase == null)
      m_aPatterns.addAll (aOrigSchema.getAllPatterns ());
    else
    {
      for (final PSActive aActive : m_aPhase.getAllActives ())
      {
        final String sActivePatternID = aActive.getPattern ();
        final PSPattern aPattern = aOrigSchema.getPatternOfID (sActivePatternID);
        if (aPattern == null)
          warn (aOrigSchema, "Failed to resolve pattern with ID '" +
                             sActivePatternID +
                             "' - ignoring this pattern in phase '" +
                             sRealPhase +
                             "'");
        else
          m_aPatterns.add (aPattern);
      }
    }
    if (m_aPatterns.isEmpty ())
      if (m_aPhase == null)
        error (aOrigSchema, "No patterns found in schema!");
      else
        error (aOrigSchema, "No patterns found in schema for phase '" + m_aPhase.getID () + "!");
  }

  @Nonnull
  protected IPSErrorHandler getErrorHandler ()
  {
    return m_aErrorHandler;
  }

  @OverridingMethodsMustInvokeSuper
  protected void warn (@Nonnull final IPSElement aSourceElement, @Nonnull final String sMsg)
  {
    getErrorHandler ().warn (m_aOrigSchema.getResource (), aSourceElement, sMsg);
  }

  @OverridingMethodsMustInvokeSuper
  protected void error (@Nonnull final IPSElement aSourceElement, @Nonnull final String sMsg)
  {
    error (aSourceElement, sMsg, (Throwable) null);
  }

  @OverridingMethodsMustInvokeSuper
  protected void error (@Nonnull final IPSElement aSourceElement,
                        @Nonnull final String sMsg,
                        @Nullable final Throwable t)
  {
    getErrorHandler ().error (m_aOrigSchema.getResource (), aSourceElement, sMsg, t);
  }

  @Nonnull
  public final IPSQueryBinding getQueryBinding ()
  {
    return m_aQueryBinding;
  }

  @Nonnull
  public final PSSchema getOriginalSchema ()
  {
    return m_aOrigSchema;
  }

  @Nonnull
  public final MapBasedNamespaceContext getNamespaceContext ()
  {
    return m_aNamespaceContext;
  }

  @Nonnull
  public final String getPhaseID ()
  {
    return m_sPhase;
  }

  @Nullable
  public final PSPhase getPhase ()
  {
    return m_aPhase;
  }

  public final boolean isPhaseSpecified ()
  {
    return m_aPhase != null;
  }

  @Nonnull
  @ReturnsMutableCopy
  public final List <PSPattern> getAllRelevantPatterns ()
  {
    return ContainerHelper.newList (m_aPatterns);
  }

  @Nonnull
  public EValidity validatePartially (@Nonnull final Node aNode) throws SchematronValidationException
  {
    final PSValidationHandlerBreakOnFirstError aHandler = new PSValidationHandlerBreakOnFirstError ();
    validate (aNode, aHandler);
    return aHandler.getValidity ();
  }
}
