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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.phloc.commons.log.InMemoryLogger;
import com.phloc.commons.microdom.IMicroElement;
import com.phloc.commons.microdom.impl.MicroElement;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.string.ToStringGenerator;
import com.phloc.schematron.CSchematron;
import com.phloc.schematron.CSchematronXML;

/**
 * A single Schematron include-element.<br>
 * The required href attribute references an external well-formed XML document
 * whose document element is a Schematron element of a type which is allowed by
 * the grammar for Schematron at the current position in the schema. The
 * external document is inserted in place of the include element.
 * 
 * @author Philip Helger
 */
@NotThreadSafe
public class PSInclude implements IPSElement
{
  private String m_sHref;

  public PSInclude ()
  {}

  public boolean isValid (@Nonnull final InMemoryLogger aLogger)
  {
    if (StringHelper.hasNoText (m_sHref))
    {
      aLogger.error ("<include> has no 'href'");
      return false;
    }
    return true;
  }

  public boolean isMinimal ()
  {
    return false;
  }

  /**
   * @param sHref
   *        The path to the object to include. May be <code>null</code>.
   */
  public void setHref (@Nullable final String sHref)
  {
    m_sHref = sHref;
  }

  /**
   * @return The path to the object to include. May be <code>null</code>.
   */
  @Nullable
  public String getHref ()
  {
    return m_sHref;
  }

  @Nonnull
  public IMicroElement getAsMicroElement ()
  {
    final IMicroElement ret = new MicroElement (CSchematron.NAMESPACE_SCHEMATRON, CSchematronXML.ELEMENT_INCLUDE);
    ret.setAttribute (CSchematronXML.ATTR_HREF, m_sHref);
    return ret;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("href", m_sHref).toString ();
  }
}
