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
package com.phloc.commons.xml.serialize;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.phloc.commons.charset.CCharset;

/**
 * Test class for class {@link DOMInputStreamProvider}.
 * 
 * @author Philip Helger
 */
public final class DOMInputStreamProviderTest
{
  @Test
  public void testSimple () throws SAXException
  {
    final Document doc = XMLReader.readXMLDOM ("<?xml version=\"1.0\"?><root />");
    assertNotNull (doc);

    final InputStream aIS = new DOMInputStreamProvider (doc, CCharset.CHARSET_UTF_8_OBJ).getInputStream ();
    final Document doc2 = XMLReader.readXMLDOM (aIS);
    assertEquals (XMLWriter.getXMLString (doc), XMLWriter.getXMLString (doc2));
  }
}
