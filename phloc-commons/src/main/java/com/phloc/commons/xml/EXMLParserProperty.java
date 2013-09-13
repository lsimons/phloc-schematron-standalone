/**
 * Copyright (C) 2006-2013 phloc systems
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
package com.phloc.commons.xml;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.lang.EnumHelper;
import com.phloc.commons.name.IHasName;

/**
 * Contains constants for parser properties.
 * 
 * @author Philip Helger
 */
public enum EXMLParserProperty implements IHasName
{
  /**
   * SAX lexical handler. Directly pass in the object!
   */
  SAX_FEATURE_LEXICAL_HANDLER ("http://xml.org/sax/properties/lexical-handler");

  private static final Logger s_aLogger = LoggerFactory.getLogger (EXMLParserProperty.class);

  private final String m_sName;

  private EXMLParserProperty (@Nonnull @Nonempty final String sName)
  {
    m_sName = sName;
  }

  @Nonnull
  @Nonempty
  public String getName ()
  {
    return m_sName;
  }

  public void applyTo (@Nonnull final org.xml.sax.XMLReader aParser, final Object aValue)
  {
    try
    {
      aParser.setProperty (m_sName, aValue);
    }
    catch (final SAXNotRecognizedException ex)
    {
      s_aLogger.warn ("XML Parser does not recognize property '" + name () + "'");
    }
    catch (final SAXNotSupportedException ex)
    {
      s_aLogger.warn ("XML Parser does not support property '" + name () + "'");
    }
  }

  @Nullable
  public static EXMLParserProperty getFromNameOrNull (@Nullable final String sName)
  {
    return EnumHelper.getFromNameOrNull (EXMLParserProperty.class, sName);
  }
}
