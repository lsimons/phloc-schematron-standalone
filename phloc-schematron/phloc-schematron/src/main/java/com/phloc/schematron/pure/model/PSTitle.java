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
 * A single Schematron title-element.<br>
 * A summary of the purpose or role of the schema or pattern, for the purpose of
 * documentation or a rich user interface.<br>
 * An implementation is not required to make use of this element.
 * 
 * @author Philip Helger
 */
@NotThreadSafe
public class PSTitle implements IPSClonableElement <PSTitle>, IPSOptionalElement, IPSHasMixedContent
{
  private final List <Object> m_aContent = new ArrayList <Object> ();

  public PSTitle ()
  {}

  public boolean isValid (@Nonnull final InMemoryLogger aLogger)
  {
    for (final Object aContent : m_aContent)
      if (aContent instanceof IPSElement)
        if (!((IPSElement) aContent).isValid (aLogger))
          return false;
    return true;
  }

  public boolean isMinimal ()
  {
    return false;
  }

  public void addText (@Nonnull @Nonempty final String sText)
  {
    if (StringHelper.hasNoText (sText))
      throw new IllegalArgumentException ("text");
    m_aContent.add (sText);
  }

  public boolean hasAnyText ()
  {
    for (final Object aElement : m_aContent)
      if (aElement instanceof String)
        return true;
    return false;
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <String> getAllTexts ()
  {
    final List <String> ret = new ArrayList <String> ();
    for (final Object aElement : m_aContent)
      if (aElement instanceof String)
        ret.add ((String) aElement);
    return ret;
  }

  public void addDir (@Nonnull final PSDir aDir)
  {
    if (aDir == null)
      throw new NullPointerException ("Dir");
    m_aContent.add (aDir);
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <PSDir> getAllDirs ()
  {
    final List <PSDir> ret = new ArrayList <PSDir> ();
    for (final Object aElement : m_aContent)
      if (aElement instanceof PSDir)
        ret.add ((PSDir) aElement);
    return ret;
  }

  /**
   * @return A list of {@link String} and {@link PSDir} elements.
   */
  @Nonnull
  @ReturnsMutableCopy
  public List <Object> getAllContentElements ()
  {
    return ContainerHelper.newList (m_aContent);
  }

  @Nonnull
  public IMicroElement getAsMicroElement ()
  {
    final IMicroElement ret = new MicroElement (CSchematron.NAMESPACE_SCHEMATRON, CSchematronXML.ELEMENT_TITLE);
    for (final Object aContent : m_aContent)
      if (aContent instanceof String)
        ret.appendText ((String) aContent);
      else
        ret.appendChild (((IPSElement) aContent).getAsMicroElement ());
    return ret;
  }

  @Nonnull
  public PSTitle getClone ()
  {
    final PSTitle ret = new PSTitle ();
    for (final Object aContent : m_aContent)
      if (aContent instanceof String)
        ret.addText ((String) aContent);
      else
        if (aContent instanceof PSDir)
          ret.addDir (((PSDir) aContent).getClone ());
    return ret;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("content", m_aContent).toString ();
  }
}
