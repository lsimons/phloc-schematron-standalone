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
 * A single Schematron phase-element.<br>
 * A grouping of patterns, to name and declare variations in schemas, for
 * example, to support progressive validation. The required id attribute is the
 * name of the phase. The implementation determines which phase to use for
 * validating documents, for example by user command.<br>
 * Two names, #ALL and #DEFAULT, have special meanings. The name #ALL is
 * reserved and available for use by implementations to denote that all patterns
 * are active. The name #DEFAULT is reserved and available for use by
 * implementations to denote that the name given in the defaultPhase attribute
 * on the schema element should be used. If no defaultPhase is specified, then
 * all patterns are active.<br>
 * NOTE: The names #ALL and #DEFAULT shall not be used in a Schematron schema.
 * They are for use when invoking or configuring schema validation, for example
 * as a command-line parameter.<br>
 * The icon, see and fpi attributes allow rich interfaces and documentation.<br>
 * {@link PSPhase} elements are only referenced from {@link PSSchema} elements.
 * 
 * @author Philip Helger
 */
@NotThreadSafe
public class PSPhase implements IPSElement, IPSHasForeignElements, IPSHasIncludes, IPSHasLets, IPSHasID, IPSHasRichGroup
{
  private String m_sID;
  private PSRichGroup m_aRich;
  private final List <PSInclude> m_aIncludes = new ArrayList <PSInclude> ();
  private final List <IPSElement> m_aContent = new ArrayList <IPSElement> ();
  private Map <String, String> m_aForeignAttrs;
  private List <IMicroElement> m_aForeignElements;

  public PSPhase ()
  {}

  public boolean isValid (@Nonnull final InMemoryLogger aLogger)
  {
    for (final PSInclude aInclude : m_aIncludes)
      if (!aInclude.isValid (aLogger))
        return false;
    for (final IPSElement aContent : m_aContent)
      if (!aContent.isValid (aLogger))
        return false;
    if (StringHelper.hasNoText (m_sID))
    {
      aLogger.error ("<phase> has no 'id'");
      return false;
    }
    return true;
  }

  public boolean isMinimal ()
  {
    for (final PSInclude aInclude : m_aIncludes)
      if (!aInclude.isMinimal ())
        return false;
    for (final IPSElement aContent : m_aContent)
      if (!aContent.isMinimal ())
        return false;
    return true;
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

  public void addInclude (@Nonnull final PSInclude aInclude)
  {
    if (aInclude == null)
      throw new NullPointerException ("Include");
    m_aIncludes.add (aInclude);
  }

  public boolean hasAnyInclude ()
  {
    return !m_aIncludes.isEmpty ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <PSInclude> getAllIncludes ()
  {
    return ContainerHelper.newList (m_aIncludes);
  }

  public void addP (@Nonnull final PSP aP)
  {
    if (aP == null)
      throw new NullPointerException ("P");
    m_aContent.add (aP);
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <PSP> getAllPs ()
  {
    final List <PSP> ret = new ArrayList <PSP> ();
    for (final IPSElement aElement : m_aContent)
      if (aElement instanceof PSP)
        ret.add ((PSP) aElement);
    return ret;
  }

  public void addLet (@Nonnull final PSLet aLet)
  {
    if (aLet == null)
      throw new NullPointerException ("Let");
    m_aContent.add (aLet);
  }

  public boolean hasAnyLet ()
  {
    for (final IPSElement aElement : m_aContent)
      if (aElement instanceof PSLet)
        return true;
    return false;
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <PSLet> getAllLets ()
  {
    final List <PSLet> ret = new ArrayList <PSLet> ();
    for (final IPSElement aElement : m_aContent)
      if (aElement instanceof PSLet)
        ret.add ((PSLet) aElement);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public Map <String, String> getAllLetsAsMap ()
  {
    final Map <String, String> ret = new LinkedHashMap <String, String> ();
    for (final IPSElement aElement : m_aContent)
      if (aElement instanceof PSLet)
      {
        final PSLet aLet = (PSLet) aElement;
        ret.put (aLet.getName (), aLet.getValue ());
      }
    return ret;
  }

  public void addActive (@Nonnull final PSActive aActive)
  {
    if (aActive == null)
      throw new NullPointerException ("Active");
    m_aContent.add (aActive);
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <PSActive> getAllActives ()
  {
    final List <PSActive> ret = new ArrayList <PSActive> ();
    for (final IPSElement aElement : m_aContent)
      if (aElement instanceof PSActive)
        ret.add ((PSActive) aElement);
    return ret;
  }

  /**
   * @return A list of {@link PSActive}, {@link PSLet} and {@link PSP} elements.
   */
  @Nonnull
  @ReturnsMutableCopy
  public List <IPSElement> getAllContentElements ()
  {
    return ContainerHelper.newList (m_aContent);
  }

  @Nonnull
  public IMicroElement getAsMicroElement ()
  {
    final IMicroElement ret = new MicroElement (CSchematron.NAMESPACE_SCHEMATRON, CSchematronXML.ELEMENT_PHASE);
    ret.setAttribute (CSchematronXML.ATTR_ID, m_sID);
    if (m_aRich != null)
      m_aRich.fillMicroElement (ret);
    if (m_aForeignElements != null)
      for (final IMicroElement aForeignElement : m_aForeignElements)
        ret.appendChild (aForeignElement.getClone ());
    for (final PSInclude aInclude : m_aIncludes)
      ret.appendChild (aInclude.getAsMicroElement ());
    for (final IPSElement aContent : m_aContent)
      ret.appendChild (aContent.getAsMicroElement ());
    if (m_aForeignAttrs != null)
      for (final Map.Entry <String, String> aEntry : m_aForeignAttrs.entrySet ())
        ret.setAttribute (aEntry.getKey (), aEntry.getValue ());
    return ret;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).appendIfNotNull ("id", m_sID)
                                       .appendIfNotNull ("rich", m_aRich)
                                       .append ("includes", m_aIncludes)
                                       .append ("content", m_aContent)
                                       .appendIfNotNull ("foreignAttrs", m_aForeignAttrs)
                                       .appendIfNotNull ("foreignElements", m_aForeignElements)
                                       .toString ();
  }
}
