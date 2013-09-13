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
package com.phloc.schematron.svrl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.oclc.purl.dsdl.svrl.SchematronOutputType;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.phloc.commons.GlobalDebug;
import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.io.resource.ClassPathResource;
import com.phloc.commons.xml.XMLFactory;
import com.phloc.commons.xml.serialize.XMLReader;
import com.phloc.commons.xml.serialize.XMLWriter;
import com.phloc.schematron.ISchematronResource;
import com.phloc.schematron.mock.SchematronTestHelper;
import com.phloc.schematron.xslt.SchematronResourceSCH;

/**
 * Test class for class {@link SVRLReader}.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class SVRLReaderTest
{
  private static final String VALID_SCHEMATRON = "test-sch/valid01.sch";
  private static final String VALID_XMLINSTANCE = "test-xml/valid01.xml";

  @Test
  public void testCreate () throws Exception
  {
    final ISchematronResource aSV = SchematronResourceSCH.fromClassPath (VALID_SCHEMATRON);
    assertNotNull ("Failed to parse Schematron", aSV);
    final Document aDoc = aSV.applySchematronValidation (new ClassPathResource (VALID_XMLINSTANCE));
    assertNotNull ("Failed to parse demo XML", aDoc);

    if (false)
    {
      GlobalDebug.setDebugModeDirect (true);
      System.out.println (XMLWriter.getXMLString (aDoc));
    }
    final SchematronOutputType aSO = SVRLReader.readXML (aDoc);
    assertNotNull ("Failed to parse Schematron output", aSO);
  }

  @Test
  public void testRead () throws SAXException
  {
    for (final IReadableResource aRes : SchematronTestHelper.getAllValidSVRLFiles ())
      assertNotNull (aRes.getPath (), SVRLReader.readXML (XMLReader.readXMLDOM (aRes)));
  }

  @Test
  public void testReadInvalidSchematron ()
  {
    try
    {
      // Read null
      SVRLReader.readXML ((Node) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      // Read empty XML
      SVRLReader.readXML (XMLFactory.newDocument ());
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    // Read XML that is not SVRL
    final SchematronOutputType aSVRL = SVRLReader.readXML (new ClassPathResource ("test-xml/goodOrder01.xml"));
    assertNull (aSVRL);
  }
}
