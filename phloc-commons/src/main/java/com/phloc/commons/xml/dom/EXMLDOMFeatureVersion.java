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
package com.phloc.commons.xml.dom;

import javax.annotation.Nonnull;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.id.IHasID;

/**
 * Represents different DOM versions for XML feature tests.<br>
 * See http://www.w3.org/TR/DOM-Level-3-Core/core.html#DOMFeatures
 * 
 * @author Philip Helger
 */
public enum EXMLDOMFeatureVersion implements IHasID <String>
{
  DOM_FEATURE_VERSION1 ("1.0"),
  DOM_FEATURE_VERSION2 ("2.0"),
  DOM_FEATURE_VERSION3 ("3.0");

  private final String m_sID;

  private EXMLDOMFeatureVersion (@Nonnull @Nonempty final String sID)
  {
    m_sID = sID;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }
}
