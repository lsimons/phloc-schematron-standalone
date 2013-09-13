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
package com.phloc.commons.error;

import java.io.Serializable;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.name.IHasDisplayText;
import com.phloc.commons.state.IErrorIndicator;
import com.phloc.commons.state.ISuccessIndicator;

/**
 * Interface for an error that can be drilled down to a certain resource (e.g. a
 * document). The name is a bit misleading, as an "IResourceError" can also
 * contain an INFO or a WARNING message!
 * 
 * @author Philip Helger
 */
public interface IResourceError extends IHasErrorLevel, IHasDisplayText, ISuccessIndicator, IErrorIndicator, Serializable
{
  /**
   * @return The non-<code>null</code> location of the error.
   */
  @Nonnull
  IResourceLocation getLocation ();

  /**
   * @return The error level. Never <code>null</code>.
   */
  @Nonnull
  EErrorLevel getErrorLevel ();

  /**
   * @return The linked exception or <code>null</code> if no such exception is
   *         available.
   */
  @Nullable
  Throwable getLinkedException ();

  /**
   * Get the error as a string representation
   * 
   * @param aDisplayLocale
   *        Locale to resolve the error text
   * @return The default string representation
   */
  @Nonnull
  @Nonempty
  String getAsString (@Nonnull Locale aDisplayLocale);
}
