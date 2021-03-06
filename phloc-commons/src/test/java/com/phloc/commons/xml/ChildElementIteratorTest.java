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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.mock.AbstractPhlocTestCase;

/**
 * Test class for class {@link ChildElementIterator}.
 * 
 * @author Philip Helger
 */
public final class ChildElementIteratorTest extends AbstractPhlocTestCase
{
  @Test
  public void testGetRecursiveChildIter ()
  {
    final Document doc = XMLFactory.newDocument ();

    // No children present
    assertFalse (new ChildElementIterator (doc).hasNext ());

    // 1 child
    final Element eRoot = (Element) doc.appendChild (doc.createElement ("root"));
    assertEquals (1, ContainerHelper.newList (new ChildElementIterator (doc)).size ());

    // 2 children
    eRoot.appendChild (doc.createElement ("Hallo"));
    eRoot.appendChild (doc.createTextNode (" - "));
    eRoot.appendChild (doc.createElement ("Welt"));
    assertEquals (2, ContainerHelper.newList (new ChildElementIterator (eRoot)).size ());
    assertEquals (1, ContainerHelper.newList (new ChildElementIterator (eRoot, new FilterElementWithTagName ("Hallo")))
                                    .size ());

    try
    {
      new ChildElementIterator (doc).remove ();
      fail ();
    }
    catch (final UnsupportedOperationException ex)
    {}
    try
    {
      new ChildElementIterator (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}
