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

import com.phloc.commons.annotations.MustImplementEqualsAndHashcode;

/**
 * Base interface for an abstract writable resource.
 * 
 * @author Philip Helger
 */
@MustImplementEqualsAndHashcode
public interface IWritableResource extends IOutputStreamAndWriterProvider, IResourceBase
{
  // Change return type
  @Nonnull
  IWritableResource getWritableCloneForPath (@Nonnull String sPath);
}
