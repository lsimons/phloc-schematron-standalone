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
package com.phloc.commons.io;

import javax.annotation.Nonnull;

import com.phloc.commons.IHasBooleanRepresentation;

/**
 * Small enum for determining the append or truncate state of output streams.
 * 
 * @author Philip Helger
 */
public enum EAppend implements IHasBooleanRepresentation
{
  /** Append to an existing object */
  APPEND,
  /** Truncate an eventually existing object and start over */
  TRUNCATE;

  /** The default is {@link #TRUNCATE} */
  @Nonnull
  public static final EAppend DEFAULT = TRUNCATE;

  public boolean getAsBoolean ()
  {
    return isAppend ();
  }

  public boolean isAppend ()
  {
    return this == APPEND;
  }

  public boolean isTruncate ()
  {
    return this == TRUNCATE;
  }
}
