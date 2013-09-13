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
 * A single Schematron diagnostics-element.<br>
 * A section containing individual diagnostic elements.<br>
 * An implementation is not required to make use of this element.
 * 
 * @author Philip Helger
 */
@NotThreadSafe
public class PSDiagnostics implements IPSElement, IPSOptionalElement, IPSHasForeignElements, IPSHasIncludes
{
  private final List <PSInclude> m_aIncludes = new ArrayList <PSInclude> ();
  private final List <PSDiagnostic> m_aDiagnostics = new ArrayList <PSDiagnostic> ();
  private Map <String, String> m_aForeignAttrs;
  private List <IMicroElement> m_aForeignElements;

  public PSDiagnostics ()
  {}

  public boolean isValid (@Nonnull final InMemoryLogger aLogger)
  {
    for (final PSInclude aInclude : m_aIncludes)
      if (!aInclude.isValid (aLogger))
        return false;
    for (final PSDiagnostic aDiagnostic : m_aDiagnostics)
      if (!aDiagnostic.isValid (aLogger))
        return false;
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

  public void addDiagnostic (@Nonnull final PSDiagnostic aDiagnostic)
  {
    if (aDiagnostic == null)
      throw new NullPointerException ("diagnostic");
    m_aDiagnostics.add (aDiagnostic);
  }

  @Nullable
  public PSDiagnostic getDiagnosticOfID (@Nullable final String sID)
  {
    if (StringHelper.hasText (sID))
      for (final PSDiagnostic aDiagnostic : m_aDiagnostics)
        if (sID.equals (aDiagnostic.getID ()))
          return aDiagnostic;
    return null;
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <PSDiagnostic> getAllDiagnostics ()
  {
    return ContainerHelper.newList (m_aDiagnostics);
  }

  @Nonnull
  public IMicroElement getAsMicroElement ()
  {
    final IMicroElement ret = new MicroElement (CSchematron.NAMESPACE_SCHEMATRON, CSchematronXML.ELEMENT_DIAGNOSTICS);
    if (m_aForeignElements != null)
      for (final IMicroElement aForeignElement : m_aForeignElements)
        ret.appendChild (aForeignElement.getClone ());
    for (final PSInclude aInclude : m_aIncludes)
      ret.appendChild (aInclude.getAsMicroElement ());
    for (final PSDiagnostic aDiagnostic : m_aDiagnostics)
      ret.appendChild (aDiagnostic.getAsMicroElement ());
    if (m_aForeignAttrs != null)
      for (final Map.Entry <String, String> aEntry : m_aForeignAttrs.entrySet ())
        ret.setAttribute (aEntry.getKey (), aEntry.getValue ());
    return ret;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("includes", m_aIncludes)
                                       .append ("diagnostics", m_aDiagnostics)
                                       .appendIfNotNull ("foreignAttrs", m_aForeignAttrs)
                                       .appendIfNotNull ("foreignElements", m_aForeignElements)
                                       .toString ();
  }
}
