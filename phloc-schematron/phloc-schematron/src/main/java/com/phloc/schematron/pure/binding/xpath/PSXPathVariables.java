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
package com.phloc.schematron.pure.binding.xpath;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.compare.ComparatorStringLongestFirst;
import com.phloc.commons.state.EChange;
import com.phloc.commons.string.StringHelper;

public class PSXPathVariables
{
  private final Map <String, String> m_aMap = new TreeMap <String, String> (new ComparatorStringLongestFirst ());

  @Nonnull
  public EChange add (@Nonnull final Map.Entry <String, String> aEntry)
  {
    return add (aEntry.getKey (), aEntry.getValue ());
  }

  @Nonnull
  public EChange add (@Nonnull @Nonempty final String sName, @Nonnull @Nonempty final String sValue)
  {
    if (StringHelper.hasNoText (sName))
      throw new IllegalArgumentException ("Name");
    if (StringHelper.hasNoText (sValue))
      throw new IllegalArgumentException ("Value");

    final String sRealName = PSXPathQueryBinding.PARAM_VARIABLE_PREFIX + sName;
    if (m_aMap.containsKey (sRealName))
      return EChange.UNCHANGED;

    // Apply all existing variables to this variable value!
    final String sRealValue = getAppliedReplacement (sValue);
    m_aMap.put (sRealName, sRealValue);
    return EChange.CHANGED;
  }

  @Nullable
  public String getAppliedReplacement (@Nullable final String sText)
  {
    return PSXPathQueryBinding.getWithParamTextsReplacedStatic (sText, m_aMap);
  }

  public void removeAll (@Nullable final List <String> aVars)
  {
    if (aVars != null)
      for (final String sName : aVars)
        m_aMap.remove (PSXPathQueryBinding.PARAM_VARIABLE_PREFIX + sName);
  }
}
