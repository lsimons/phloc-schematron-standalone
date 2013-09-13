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
package com.phloc.commons.text.resource;

import java.util.Enumeration;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.annotation.Nonnull;

import com.phloc.commons.charset.CCharset;
import com.phloc.commons.charset.CharsetManager;

/**
 * Special property resource bundle that wraps all strings into UTF-8 strings.
 * 
 * @author Philip Helger
 */
final class Utf8PropertyResourceBundle extends ResourceBundle
{
  private final PropertyResourceBundle m_aBundle;

  Utf8PropertyResourceBundle (@Nonnull final PropertyResourceBundle aBundle)
  {
    if (aBundle == null)
      throw new NullPointerException ("bundle");
    m_aBundle = aBundle;
  }

  @Override
  public Enumeration <String> getKeys ()
  {
    return m_aBundle.getKeys ();
  }

  @Override
  protected Object handleGetObject (final String sKey)
  {
    final String sValue = m_aBundle.getString (sKey);
    // This does the main trick of converting the String to UTF-8
    return CharsetManager.getAsStringInOtherCharset (sValue,
                                                     CCharset.CHARSET_ISO_8859_1_OBJ,
                                                     CCharset.CHARSET_UTF_8_OBJ);
  }
}
