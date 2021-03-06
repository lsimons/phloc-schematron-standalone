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
package com.phloc.commons.compare;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.text.Collator;
import java.util.List;

import org.junit.Test;

import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.mock.AbstractPhlocTestCase;

/**
 * Test class for class {@link ComparatorAsString}.
 * 
 * @author Philip Helger
 */
public final class ComparatorAsStringTest extends AbstractPhlocTestCase
{
  @Test
  public void testAll ()
  {
    final List <String> l = ContainerHelper.newList ("a", "b", "c");
    assertEquals (3, ContainerHelper.getSorted (l, new ComparatorAsString ()).size ());
    assertEquals (3, ContainerHelper.getSorted (l, new ComparatorAsString (ESortOrder.DESCENDING)).size ());
    assertEquals (3, ContainerHelper.getSorted (l, new ComparatorAsString (L_EN)).size ());
    assertEquals (3, ContainerHelper.getSorted (l, new ComparatorAsString (Collator.getInstance (L_FR))).size ());

    try
    {
      new ComparatorAsString ((ESortOrder) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      new ComparatorAsString ().setSortOrder ((ESortOrder) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}
