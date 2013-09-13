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

import java.util.LinkedHashMap;
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
 * A single Schematron ns-element.<br>
 * Specification of a namespace prefix and URI. The required prefix attribute is
 * an XML name with no colon character. The required uri attribute is a
 * namespace URI.<br>
 * NOTE: Because the characters allowed as names may change in versions of XML
 * subsequent to W3C XML 1.0, the ISO/IEC 19757-2 (RELAX NG Compact Syntax)
 * schema for Schematron does not constrain the prefix to particular characters.<br>
 * In an ISO Schematron schema, namespace prefixes in context expressions,
 * assertion tests and other query expressions should use the namespace bindings
 * provided by this element. Namespace prefixes should not use the namespace
 * bindings in scope for element and attribute names.
 * 
 * @author Philip Helger
 */
@NotThreadSafe
public class PSNS implements IPSClonableElement <PSNS>, IPSHasForeignAttributes
{
  private String m_sUri;
  private String m_sPrefix;
  private Map <String, String> m_aForeignAttrs;

  public PSNS ()
  {}

  public boolean isValid (@Nonnull final InMemoryLogger aLogger)
  {
    if (StringHelper.hasNoText (m_sUri))
    {
      aLogger.error ("<ns> has no 'uri'");
      return false;
    }
    if (StringHelper.hasNoText (m_sPrefix))
    {
      aLogger.error ("<ns> has no 'prefix'");
      return false;
    }
    return true;
  }

  public boolean isMinimal ()
  {
    return true;
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

  /**
   * @param sUri
   *        The namespace URI.
   */
  public void setUri (@Nullable final String sUri)
  {
    m_sUri = sUri;
  }

  /**
   * @return The namespace URI. May be <code>null</code>.
   */
  @Nullable
  public String getUri ()
  {
    return m_sUri;
  }

  /**
   * @param sPrefix
   *        The namespace prefix to use.
   */
  public void setPrefix (@Nullable final String sPrefix)
  {
    m_sPrefix = sPrefix;
  }

  /**
   * @return The namespace prefix. May be <code>null</code>.
   */
  @Nullable
  public String getPrefix ()
  {
    return m_sPrefix;
  }

  @Nonnull
  public IMicroElement getAsMicroElement ()
  {
    final IMicroElement ret = new MicroElement (CSchematron.NAMESPACE_SCHEMATRON, CSchematronXML.ELEMENT_NS);
    ret.setAttribute (CSchematronXML.ATTR_PREFIX, m_sPrefix);
    ret.setAttribute (CSchematronXML.ATTR_URI, m_sUri);
    if (m_aForeignAttrs != null)
      for (final Map.Entry <String, String> aEntry : m_aForeignAttrs.entrySet ())
        ret.setAttribute (aEntry.getKey (), aEntry.getValue ());
    return ret;
  }

  @Nonnull
  public PSNS getClone ()
  {
    final PSNS ret = new PSNS ();
    ret.setUri (m_sUri);
    ret.setPrefix (m_sPrefix);
    if (hasForeignAttributes ())
      ret.addForeignAttributes (m_aForeignAttrs);
    return ret;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).appendIfNotNull ("uri", m_sUri)
                                       .appendIfNotNull ("prefix", m_sPrefix)
                                       .appendIfNotNull ("foreignAttrs", m_aForeignAttrs)
                                       .toString ();
  }
}
