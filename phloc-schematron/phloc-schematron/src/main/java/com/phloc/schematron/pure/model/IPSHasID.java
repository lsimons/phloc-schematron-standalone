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
package com.phloc.schematron.pure.model;

import javax.annotation.Nullable;

/**
 * Base interface for all elements that may have an ID.
 * <code>IHasID&lt;String></code> is not used here, because it requires an ID to
 * be present!
 * 
 * @author Philip Helger
 */
public interface IPSHasID
{
  /**
   * Set an ID for this object.
   * 
   * @param sID
   *        The ID to be set. May be <code>null</code>.
   */
  void setID (@Nullable String sID);

  /**
   * @return <code>true</code> if an ID is present, <code>false</code>
   *         otherwise.
   */
  boolean hasID ();

  /**
   * @return The optional ID of this element. May be <code>null</code>.
   */
  @Nullable
  String getID ();
}
