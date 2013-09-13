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
import com.phloc.commons.id.IHasID;
import com.phloc.commons.lang.EnumHelper;
import com.phloc.commons.log.InMemoryLogger;
import com.phloc.commons.microdom.IMicroElement;
import com.phloc.commons.microdom.impl.MicroElement;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.string.ToStringGenerator;
import com.phloc.schematron.CSchematron;
import com.phloc.schematron.CSchematronXML;

/**
 * A single Schematron dir-element.<br>
 * A section of natural-language text with a direction specified by the value
 * attribute. The value ltr indicates left-to-right text; the value rtl
 * indicates right-to-left text.<br>
 * An implementation is not required to make use of this element.
 * 
 * @author Philip Helger
 */
@NotThreadSafe
public class PSDir implements IPSClonableElement <PSDir>, IPSOptionalElement, IPSHasForeignElements, IPSHasTexts
{
  public static enum EDirValue implements IHasID <String>
  {
    LTR ("ltr"),
    RTL ("rtl");

    private final String m_sID;

    private EDirValue (@Nonnull @Nonempty final String sID)
    {
      m_sID = sID;
    }

    @Nonnull
    @Nonempty
    public String getID ()
    {
      return m_sID;
    }

    @Nullable
    public static EDirValue getFromIDOrNull (@Nullable final String sID)
    {
      return EnumHelper.getFromIDOrNull (EDirValue.class, sID);
    }
  }

  private EDirValue m_eValue;
  private final List <String> m_aContent = new ArrayList <String> ();
  private Map <String, String> m_aForeignAttrs;
  private List <IMicroElement> m_aForeignElements;

  public PSDir ()
  {}

  public boolean isValid (@Nonnull final InMemoryLogger aLogger)
  {
    if (m_aContent.isEmpty ())
    {
      aLogger.error ("<dir> has no content");
      return false;
    }
    return true;
  }

  public boolean isMinimal ()
  {
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

  public void setValue (@Nullable final EDirValue eValue)
  {
    m_eValue = eValue;
  }

  @Nullable
  public EDirValue getValue ()
  {
    return m_eValue;
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
    final IMicroElement ret = new MicroElement (CSchematron.NAMESPACE_SCHEMATRON, CSchematronXML.ELEMENT_DIR);
    if (m_eValue != null)
      ret.setAttribute (CSchematronXML.ATTR_VALUE, m_eValue.getID ());
    if (m_aForeignElements != null)
      for (final IMicroElement aForeignElement : m_aForeignElements)
        ret.appendChild (aForeignElement.getClone ());
    for (final String sContent : m_aContent)
      ret.appendText (sContent);
    if (m_aForeignAttrs != null)
      for (final Map.Entry <String, String> aEntry : m_aForeignAttrs.entrySet ())
        ret.setAttribute (aEntry.getKey (), aEntry.getValue ());
    return ret;
  }

  @Nonnull
  public PSDir getClone ()
  {
    final PSDir ret = new PSDir ();
    ret.setValue (m_eValue);
    for (final String sContent : m_aContent)
      ret.addText (sContent);
    if (hasForeignElements ())
      ret.addForeignElements (m_aForeignElements);
    if (hasForeignAttributes ())
      ret.addForeignAttributes (m_aForeignAttrs);
    return ret;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).appendIfNotNull ("value", m_eValue)
                                       .append ("content", m_aContent)
                                       .appendIfNotNull ("foreignAttrs", m_aForeignAttrs)
                                       .appendIfNotNull ("foreignElements", m_aForeignElements)
                                       .toString ();
  }
}
