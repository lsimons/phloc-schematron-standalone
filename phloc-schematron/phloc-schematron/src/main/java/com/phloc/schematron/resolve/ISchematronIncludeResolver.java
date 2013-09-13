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
package com.phloc.schematron.resolve;

import java.io.IOException;

import javax.annotation.Nonnull;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.io.IReadableResource;

/**
 * Generic include resolver
 * 
 * @author Philip Helger
 */
public interface ISchematronIncludeResolver
{
  /**
   * Resolve the content of the passed href to a resource
   * 
   * @param sHref
   *        The source href that needs to be resolved relative to some base href
   * @return The location of the included href
   * @throws IOException
   *         In case of a resolution error
   */
  @Nonnull
  IReadableResource getResolvedSchematronResource (@Nonnull @Nonempty String sHref) throws IOException;
}
