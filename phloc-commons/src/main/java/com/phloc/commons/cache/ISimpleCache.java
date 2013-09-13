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
package com.phloc.commons.cache;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.name.IHasName;
import com.phloc.commons.state.EChange;

/**
 * Interface for a very simple Map-like cache.
 * 
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Cache key type.
 * @param <VALUETYPE>
 *        Cache value type.
 */
public interface ISimpleCache <KEYTYPE, VALUETYPE> extends SimpleCacheMBean, IHasName
{
  /**
   * Get the cached value associated with the passed key.
   * 
   * @param aKey
   *        The key to be looked up.
   * @return <code>null</code> if no such value is in the cache.
   */
  @Nullable
  VALUETYPE getFromCache (@Nullable KEYTYPE aKey);

  /**
   * Remove the given key from the cache.
   * 
   * @param aKey
   *        The key to be removed.
   * @return {@link EChange#CHANGED} upon success, {@link EChange#UNCHANGED} if
   *         the key was not within the cache,
   */
  @Nonnull
  EChange removeFromCache (@Nullable KEYTYPE aKey);
}
