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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.log.InMemoryLogger;
import com.phloc.commons.microdom.IMicroElement;
import com.phloc.commons.microdom.impl.MicroElement;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.string.ToStringGenerator;
import com.phloc.schematron.CSchematron;
import com.phloc.schematron.CSchematronXML;

/**
 * A single Schematron emph-element.<br>
 * A portion of text that should be rendered with some emphasis.<br>
 * An implementation is not required to make use of this element.
 * 
 * @author Philip Helger
 */
@NotThreadSafe
public class PSEmph implements IPSClonableElement <PSEmph>, IPSOptionalElement, IPSHasTexts
{
  private final List <String> m_aContent = new ArrayList <String> ();

  public PSEmph ()
  {}

  public boolean isValid (@Nonnull final InMemoryLogger aLogger)
  {
    if (m_aContent.isEmpty ())
    {
      aLogger.error ("<emph> has no content");
      return false;
    }
    return true;
  }

  public boolean isMinimal ()
  {
    return true;
  }

  public void addText (@Nonnull @Nonempty final String sText)
  {
    if (StringHelper.hasNoText (sText))
      throw new IllegalArgumentException ("text");
    m_aContent.add (sText);
  }

  public boolean hasAnyText ()
  {
    return !m_aContent.isEmpty ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <String> getAllTexts ()
  {
    return ContainerHelper.newList (m_aContent);
  }

  @Nullable
  public String getAsText ()
  {
    return StringHelper.getImploded (m_aContent);
  }

  @Nonnull
  public IMicroElement getAsMicroElement ()
  {
    final IMicroElement ret = new MicroElement (CSchematron.NAMESPACE_SCHEMATRON, CSchematronXML.ELEMENT_EMPH);
    for (final String sContent : m_aContent)
      ret.appendText (sContent);
    return ret;
  }

  @Nonnull
  public PSEmph getClone ()
  {
    final PSEmph ret = new PSEmph ();
    for (final String sContent : m_aContent)
      ret.addText (sContent);
    return ret;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("content", m_aContent).toString ();
  }
}
