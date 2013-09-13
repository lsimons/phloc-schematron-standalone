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

import java.util.List;

import javax.annotation.Nonnull;

import com.phloc.commons.annotations.ReturnsMutableCopy;

/**
 * Base interface for Pure Schematron elements that support includes.
 * 
 * @author Philip Helger
 */
public interface IPSHasIncludes
{
  /**
   * Add an include to this object.
   * 
   * @param aInclude
   *        The include to be added. May not be <code>null</code>.
   */
  void addInclude (@Nonnull PSInclude aInclude);

  /**
   * @return <code>true</code> if at least one include is present in this
   *         object.
   */
  boolean hasAnyInclude ();

  /**
   * @return A list of all contained includes. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  List <PSInclude> getAllIncludes ();
}
