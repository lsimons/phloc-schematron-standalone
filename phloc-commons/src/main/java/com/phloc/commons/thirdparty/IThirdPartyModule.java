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
package com.phloc.commons.thirdparty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.name.IHasDisplayName;
import com.phloc.commons.version.IHasVersion;
import com.phloc.commons.version.Version;

/**
 * Represents meta information for a single third-party module (aka JAR file).
 * 
 * @author Philip Helger
 */
public interface IThirdPartyModule extends IHasDisplayName, IHasVersion
{
  /**
   * @return The copyright owner of this module. May not be <code>null</code>.
   */
  @Nonnull
  String getCopyrightOwner ();

  /**
   * @return The license used by this module. May not be <code>null</code>.
   */
  @Nonnull
  ILicense getLicense ();

  /**
   * @return The optional version of this product. May be <code>null</code>
   *         because this means another place where the version number needs to
   *         be maintained.
   */
  @Nullable
  Version getVersion ();

  /**
   * @return The optional web site of the module. May be <code>null</code>.
   */
  @Nullable
  String getWebSiteURL ();

  /**
   * @return <code>true</code> if this is an optional thirdparty module,
   *         <code>false</code> if it is required.
   */
  boolean isOptional ();
}
