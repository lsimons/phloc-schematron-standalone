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
package com.phloc.schematron.pure.exchange;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.io.IReadableResource;
import com.phloc.schematron.SchematronException;

/**
 * Exception when reading Schematron fails.
 * 
 * @author Philip Helger
 */
public class SchematronReadException extends SchematronException
{
  private final IReadableResource m_aRes;

  /**
   * Constructor
   * 
   * @param aRes
   *        The resource in which the error occurred. May not be
   *        <code>null</code>.
   * @param sMsg
   *        error message
   */
  public SchematronReadException (@Nonnull final IReadableResource aRes, @Nonnull final String sMsg)
  {
    this (aRes, sMsg, (Throwable) null);
  }

  /**
   * Constructor
   * 
   * @param aRes
   *        The resource in which the error occurred. May not be
   *        <code>null</code>.
   * @param sMsg
   *        error message
   * @param t
   *        Nested exception
   */
  public SchematronReadException (@Nonnull final IReadableResource aRes,
                                  @Nonnull final String sMsg,
                                  @Nullable final Throwable t)
  {
    super (aRes.getPath () + ": " + sMsg, t);
    m_aRes = aRes;
  }

  /**
   * @return The resource, in which the error occurred. Never <code>null</code>.
   */
  @Nonnull
  public IReadableResource getResource ()
  {
    return m_aRes;
  }
}
