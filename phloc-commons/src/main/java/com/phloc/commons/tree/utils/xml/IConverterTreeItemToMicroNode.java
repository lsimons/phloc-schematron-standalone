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
package com.phloc.commons.tree.utils.xml;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.microdom.IMicroElement;

/**
 * Interface used to convert a single tree item data value to a micro node.
 * 
 * @author Philip Helger
 * @param <DATATYPE>
 *        The type of the tree item data
 */
public interface IConverterTreeItemToMicroNode <DATATYPE>
{
  void appendDataValue (@Nonnull IMicroElement eDataElement, @Nullable DATATYPE aObject);
}
