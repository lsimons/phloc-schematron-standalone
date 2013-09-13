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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
 * A single Schematron diagnostic-element.<br>
 * A natural-language message giving more specific details concerning a failed
 * assertion, such as found versus expected values and repair hints.<br>
 * NOTE: Diagnostics in multiple languages may be supported by using a different
 * diagnostic element for each language, with the appropriate xml:lang language
 * attribute, and referencing all the unique identifiers of the diagnostic
 * elements in the diagnostics attribute of the assertion. Annex G gives a
 * simple example of a multi-lingual schema.<br>
 * An implementation is not required to make use of this element.
 * 
 * @author Philip Helger
 */
@NotThreadSafe
public class PSDiagnostic implements IPSClonableElement <PSDiagnostic>, IPSOptionalElement, IPSHasID, IPSHasForeignElements, IPSHasMixedContent, IPSHasRichGroup
{
  private String m_sID;
  private PSRichGroup m_aRich;
  private final List <Object> m_aContent = new ArrayList <Object> ();
  private Map <String, String> m_aForeignAttrs;
  private List <IMicroElement> m_aForeignElements;

  public PSDiagnostic ()
  {}

  public boolean isValid (@Nonnull final InMemoryLogger aLogger)
  {
    for (final Object aContent : m_aContent)
      if (aContent instanceof IPSElement)
        if (!((IPSElement) aContent).isValid (aLogger))
          return false;
    if (StringHelper.hasNoText (m_sID))
    {
      aLogger.error ("<diagnostic> has no 'id'");
      return false;
    }
    return true;
  }

  public boolean isMinimal ()
  {
    return false;
  }

  public void addForeignElement (@Nonnull final IMicroElement aForeignElement)
  {
    if (aForeignElement == null)
      throw new NullPointerException ("ForeignElement");
    if (aForeignElement.hasParent ())
      throw new IllegalArgumentException ("ForeignElement already has a parent!");
    if (m_aForeignElements == null)
      m_aForeignElements = new ArrayList <IMicroElement> ();
    m_aForeignElements.add (aForeignElement);
  }

  public void addForeignElements (@Nonnull final List <IMicroElement> aForeignElements)
  {
    if (aForeignElements == null)
      throw new NullPointerException ("ForeignElements");
    for (final IMicroElement aForeignElement : aForeignElements)
      addForeignElement (aForeignElement);
  }

  public boolean hasForeignElements ()
  {
    return m_aForeignElements != null && !m_aForeignElements.isEmpty ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <IMicroElement> getAllForeignElements ()
  {
    return ContainerHelper.newList (m_aForeignElements);
  }

  public void addForeignAttribute (@Nonnull final String sAttrName, @Nonnull final String sAttrValue)
  {
    if (sAttrName == null)
      throw new NullPointerException ("AttrName");
    if (sAttrValue == null)
      throw new NullPointerException ("AttrValue");
    if (m_aForeignAttrs == null)
      m_aForeignAttrs = new LinkedHashMap <String, String> ();
    m_aForeignAttrs.put (sAttrName, sAttrValue);
  }

  public void addForeignAttributes (@Nonnull final Map <String, String> aForeignAttrs)
  {
    if (aForeignAttrs == null)
      throw new NullPointerException ("foreignAttrs");
    for (final Map.Entry <String, String> aEntry : aForeignAttrs.entrySet ())
      addForeignAttribute (aEntry.getKey (), aEntry.getValue ());
  }

  public boolean hasForeignAttributes ()
  {
    return m_aForeignAttrs != null && !m_aForeignAttrs.isEmpty ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public Map <String, String> getAllForeignAttributes ()
  {
    return ContainerHelper.newOrderedMap (m_aForeignAttrs);
  }

  public void setID (@Nullable final String sID)
  {
    m_sID = sID;
  }

  public boolean hasID ()
  {
    return m_sID != null;
  }

  @Nullable
  public String getID ()
  {
    return m_sID;
  }

  public void setRich (@Nullable final PSRichGroup aRich)
  {
    m_aRich = aRich;
  }

  public boolean hasRich ()
  {
    return m_aRich != null;
  }

  @Nullable
  public PSRichGroup getRich ()
  {
    return m_aRich;
  }

  @Nullable
  public PSRichGroup getRichClone ()
  {
    return m_aRich == null ? null : m_aRich.getClone ();
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

  public void addValueOf (@Nonnull final PSValueOf aValueOf)
  {
    if (aValueOf == null)
      throw new NullPointerException ("ValueOf");
    m_aContent.add (aValueOf);
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <PSValueOf> getAllValueOfs ()
  {
    final List <PSValueOf> ret = new ArrayList <PSValueOf> ();
    for (final Object aElement : m_aContent)
      if (aElement instanceof PSValueOf)
        ret.add ((PSValueOf) aElement);
    return ret;
  }

  public void addEmph (@Nonnull final PSEmph aEmph)
  {
    if (aEmph == null)
      throw new NullPointerException ("Emph");
    m_aContent.add (aEmph);
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <PSEmph> getAllEmphs ()
  {
    final List <PSEmph> ret = new ArrayList <PSEmph> ();
    for (final Object aElement : m_aContent)
      if (aElement instanceof PSEmph)
        ret.add ((PSEmph) aElement);
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

  public void addSpan (@Nonnull final PSSpan aSpan)
  {
    if (aSpan == null)
      throw new NullPointerException ("Span");
    m_aContent.add (aSpan);
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <PSSpan> getAllSpans ()
  {
    final List <PSSpan> ret = new ArrayList <PSSpan> ();
    for (final Object aElement : m_aContent)
      if (aElement instanceof PSSpan)
        ret.add ((PSSpan) aElement);
    return ret;
  }

  /**
   * @return A list of {@link String}, {@link PSValueOf}, {@link PSEmph},
   *         {@link PSDir} and {@link PSSpan} elements.
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
    final IMicroElement ret = new MicroElement (CSchematron.NAMESPACE_SCHEMATRON, CSchematronXML.ELEMENT_DIAGNOSTIC);
    ret.setAttribute (CSchematronXML.ATTR_ID, m_sID);
    if (m_aRich != null)
      m_aRich.fillMicroElement (ret);
    if (m_aForeignElements != null)
      for (final IMicroElement aForeignElement : m_aForeignElements)
        ret.appendChild (aForeignElement.getClone ());
    for (final Object aContent : m_aContent)
      if (aContent instanceof String)
        ret.appendText ((String) aContent);
      else
        ret.appendChild (((IPSElement) aContent).getAsMicroElement ());
    if (m_aForeignAttrs != null)
      for (final Map.Entry <String, String> aEntry : m_aForeignAttrs.entrySet ())
        ret.setAttribute (aEntry.getKey (), aEntry.getValue ());
    return ret;
  }

  @Nonnull
  public PSDiagnostic getClone ()
  {
    final PSDiagnostic ret = new PSDiagnostic ();
    ret.setID (m_sID);
    ret.setRich (getRichClone ());
    for (final Object aContent : m_aContent)
    {
      if (aContent instanceof String)
        ret.addText ((String) aContent);
      else
        if (aContent instanceof PSValueOf)
          ret.addValueOf (((PSValueOf) aContent).getClone ());
        else
          if (aContent instanceof PSEmph)
            ret.addEmph (((PSEmph) aContent).getClone ());
          else
            if (aContent instanceof PSDir)
              ret.addDir (((PSDir) aContent).getClone ());
            else
              if (aContent instanceof PSSpan)
                ret.addSpan (((PSSpan) aContent).getClone ());
    }
    if (hasForeignElements ())
      ret.addForeignElements (m_aForeignElements);
    if (hasForeignAttributes ())
      ret.addForeignAttributes (m_aForeignAttrs);
    return ret;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).appendIfNotNull ("id", m_sID)
                                       .appendIfNotNull ("rich", m_aRich)
                                       .append ("content", m_aContent)
                                       .appendIfNotNull ("foreignAttrs", m_aForeignAttrs)
                                       .appendIfNotNull ("foreignElements", m_aForeignElements)
                                       .toString ();
  }
}
