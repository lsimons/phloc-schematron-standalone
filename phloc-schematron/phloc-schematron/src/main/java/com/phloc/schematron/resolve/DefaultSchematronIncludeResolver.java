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
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.string.ToStringGenerator;
import com.phloc.commons.xml.ls.SimpleLSResourceResolver;

/**
 * The default implementation of {@link ISchematronIncludeResolver} using the
 * {@link SimpleLSResourceResolver#doStandardResourceResolving(String, String)}
 * method internally.
 * 
 * @author Philip Helger
 */
public class DefaultSchematronIncludeResolver implements ISchematronIncludeResolver
{
  private final String m_sBaseHref;

  public DefaultSchematronIncludeResolver (@Nonnull final IReadableResource aResource)
  {
    this (aResource.getAsURL ().toExternalForm ());
  }

  public DefaultSchematronIncludeResolver (@Nonnull @Nonempty final String sBaseHref)
  {
    if (StringHelper.hasNoText (sBaseHref))
      throw new NullPointerException ("baseHref");
    m_sBaseHref = sBaseHref;
  }

  @Nonnull
  @Nonempty
  public String getBaseHref ()
  {
    return m_sBaseHref;
  }

  @Nonnull
  public IReadableResource getResolvedSchematronResource (@Nonnull @Nonempty final String sHref) throws IOException
  {
    return SimpleLSResourceResolver.doStandardResourceResolving (sHref, getBaseHref ());
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("baseHref", m_sBaseHref).toString ();
  }
}
