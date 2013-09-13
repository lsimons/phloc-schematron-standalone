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
package com.phloc.schematron.pure.errorhandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.error.EErrorLevel;
import com.phloc.commons.error.IResourceErrorGroup;
import com.phloc.commons.error.ResourceError;
import com.phloc.commons.error.ResourceErrorGroup;
import com.phloc.commons.error.ResourceLocation;
import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.lang.CGStringHelper;
import com.phloc.commons.state.EChange;
import com.phloc.commons.string.ToStringGenerator;
import com.phloc.schematron.pure.model.IPSElement;
import com.phloc.schematron.pure.model.IPSHasID;

/**
 * An implementation if {@link IPSErrorHandler} that collects all error
 * messages.
 * 
 * @author Philip Helger
 */
public class CollectingPSErrorHandler extends AbstractPSErrorHandler
{
  private final ResourceErrorGroup m_aErrors = new ResourceErrorGroup ();

  public CollectingPSErrorHandler ()
  {
    super ();
  }

  public CollectingPSErrorHandler (@Nullable final IPSErrorHandler aNestedErrorHandler)
  {
    super (aNestedErrorHandler);
  }

  @Override
  @Nonnull
  protected void handle (@Nullable final IReadableResource aRes,
                         @Nonnull final EErrorLevel eErrorLevel,
                         @Nonnull final IPSElement aSourceElement,
                         @Nonnull final String sMessage,
                         @Nullable final Throwable t)
  {
    String sField = CGStringHelper.getClassLocalName (aSourceElement);
    if (aSourceElement instanceof IPSHasID && ((IPSHasID) aSourceElement).hasID ())
      sField += " [ID=" + ((IPSHasID) aSourceElement).getID () + "]";
    m_aErrors.addResourceError (new ResourceError (new ResourceLocation (aRes == null ? null : aRes.getResourceID (),
                                                                         sField), eErrorLevel, sMessage, t));
  }

  @Nonnull
  @ReturnsMutableCopy
  public IResourceErrorGroup getResourceErrors ()
  {
    return m_aErrors.getClone ();
  }

  /**
   * Clear all currently stored errors.
   * 
   * @return {@link EChange#CHANGED} if at least one item was cleared.
   */
  @Nonnull
  public EChange clearResourceErrors ()
  {
    return m_aErrors.clear ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).appendIfNotNull ("errors", m_aErrors).toString ();
  }
}
