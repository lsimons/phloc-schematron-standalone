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
 * A single Schematron value-of-element.<br>
 * Finds or calculates values from the instance document to allow clearer
 * assertions and diagnostics. The required select attribute is an expression
 * evaluated in the current context that returns a string.<br>
 * Variable references in the select attribute are resolved in the scope of the
 * current schema, phase, pattern and rule.<br>
 * An implementation which does not report natural-language assertions is not
 * required to make use of this element.
 * 
 * @author Philip Helger
 */
@NotThreadSafe
public class PSValueOf implements IPSClonableElement <PSValueOf>, IPSHasForeignAttributes
{
  private String m_sSelect;
  private Map <String, String> m_aForeignAttrs;

  public PSValueOf ()
  {}

  public boolean isValid (@Nonnull final InMemoryLogger aLogger)
  {
    if (StringHelper.hasNoText (m_sSelect))
    {
      aLogger.error ("<value-of> has no 'select'");
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
   * Set the expression to retrieve the value of
   * 
   * @param sSelect
   *        The select expression string. May be <code>null</code>.
   */
  public void setSelect (@Nullable final String sSelect)
  {
    m_sSelect = sSelect;
  }

  /**
   * @return The select expression string. May be <code>null</code>.
   */
  @Nullable
  public String getSelect ()
  {
    return m_sSelect;
  }

  @Nonnull
  public IMicroElement getAsMicroElement ()
  {
    final IMicroElement ret = new MicroElement (CSchematron.NAMESPACE_SCHEMATRON, CSchematronXML.ELEMENT_VALUE_OF);
    ret.setAttribute (CSchematronXML.ATTR_SELECT, m_sSelect);
    if (m_aForeignAttrs != null)
      for (final Map.Entry <String, String> aEntry : m_aForeignAttrs.entrySet ())
        ret.setAttribute (aEntry.getKey (), aEntry.getValue ());
    return ret;
  }

  @Nonnull
  public PSValueOf getClone ()
  {
    final PSValueOf ret = new PSValueOf ();
    ret.setSelect (m_sSelect);
    if (hasForeignAttributes ())
      ret.addForeignAttributes (m_aForeignAttrs);
    return ret;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).appendIfNotNull ("select", m_sSelect)
                                       .appendIfNotNull ("foreignAttrs", m_aForeignAttrs)
                                       .toString ();
  }
}
