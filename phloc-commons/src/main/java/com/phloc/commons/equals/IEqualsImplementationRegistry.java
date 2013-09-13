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
package com.phloc.commons.equals;

import javax.annotation.Nonnull;

/**
 * Base interface for a registry that contains equals implementations.
 * 
 * @author Philip Helger
 */
public interface IEqualsImplementationRegistry
{
  /**
   * Register a new equals implementation
   * 
   * @param aClass
   *        The class for which the equals implementation is valid
   * @param aImpl
   *        The main implementation
   */
  void registerEqualsImplementation (@Nonnull Class <?> aClass, @Nonnull IEqualsImplementation aImpl);
}
