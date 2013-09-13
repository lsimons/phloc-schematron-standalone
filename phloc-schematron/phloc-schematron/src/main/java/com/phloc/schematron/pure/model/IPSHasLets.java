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
import java.util.Map;

import javax.annotation.Nonnull;

import com.phloc.commons.annotations.ReturnsMutableCopy;

/**
 * Base interface for all objects having {@link PSLet} elements contained
 * 
 * @author Philip Helger
 */
public interface IPSHasLets
{
  /**
   * Add a {@link PSLet} element.
   * 
   * @param aLet
   *        The let element to be added. May not be <code>null</code>.
   */
  void addLet (@Nonnull PSLet aLet);

  /**
   * @return <code>true</code> if this object has at least on contained
   *         {@link PSLet} object.
   */
  boolean hasAnyLet ();

  /**
   * @return A list of all contained {@link PSLet} elements. Never
   *         <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  List <PSLet> getAllLets ();

  /**
   * @return The content of all {@link PSLet} elements as an ordered Map from
   *         name to value. The order must match the declaration order! Never
   *         <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  Map <String, String> getAllLetsAsMap ();
}
