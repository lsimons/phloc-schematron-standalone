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
import com.phloc.commons.microdom.IMicroElement;

/**
 * Base interface for Pure Schematron elements that support foreign elements and
 * attributes.
 * 
 * @author Philip Helger
 */
public interface IPSHasForeignElements extends IPSHasForeignAttributes
{
  void addForeignElement (@Nonnull IMicroElement aForeignElement);

  void addForeignElements (@Nonnull List <IMicroElement> aForeignElements);

  boolean hasForeignElements ();

  @Nonnull
  @ReturnsMutableCopy
  List <IMicroElement> getAllForeignElements ();
}
