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
package com.phloc.schematron.pure.bound.xpath;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.oclc.purl.dsdl.svrl.SchematronOutputType;
import org.xml.sax.SAXException;

import com.phloc.commons.error.EErrorLevel;
import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.io.resource.ClassPathResource;
import com.phloc.commons.log.InMemoryLogger;
import com.phloc.commons.microdom.IMicroDocument;
import com.phloc.commons.mock.DebugModeTestRule;
import com.phloc.commons.mock.PhlocTestUtils;
import com.phloc.commons.xml.serialize.XMLReader;
import com.phloc.schematron.SchematronException;
import com.phloc.schematron.SchematronHelper;
import com.phloc.schematron.mock.SchematronTestHelper;
import com.phloc.schematron.pure.binding.xpath.PSXPathQueryBinding;
import com.phloc.schematron.pure.bound.IPSBoundSchema;
import com.phloc.schematron.pure.errorhandler.CollectingPSErrorHandler;
import com.phloc.schematron.pure.errorhandler.IPSErrorHandler;
import com.phloc.schematron.pure.exchange.PSReader;
import com.phloc.schematron.pure.model.PSSchema;
import com.phloc.schematron.pure.preprocess.PSPreprocessor;
import com.phloc.schematron.svrl.SVRLWriter;

/**
 * Test class for class {@link PSPreprocessor}.
 * 
 * @author Philip Helger
 */
public final class PSXPathBoundSchemaTest
{
  @Rule
  public final TestRule m_aRule = new DebugModeTestRule ();

  private static final String [] SCH = new String [] { "valid01.sch",
                                                      "valid02.sch",
                                                      "biicore/BIICORE-UBL-T01.sch",
                                                      "biirules/BIIRULES-UBL-T01.sch",
                                                      "CellarBook.sch",
                                                      "VariableTests.sch" };
  private static final String [] XML = new String [] { "valid01.xml",
                                                      "valid01.xml",
                                                      "goodOrder01.xml",
                                                      "goodOrder01.xml",
                                                      "CellarBook.xml",
                                                      "valid01.xml" };

  @Test
  public void testSchematronValidation () throws SAXException, SchematronException
  {
    for (int i = 0; i < SCH.length; ++i)
    {
      final IReadableResource aSchRes = new ClassPathResource ("test-sch/" + SCH[i]);
      final IReadableResource aXmlRes = new ClassPathResource ("test-xml/" + XML[i]);

      // Resolve all includes
      final IMicroDocument aDoc = SchematronHelper.getWithResolvedSchematronIncludes (aSchRes);
      assertNotNull (aDoc);

      // Read to domain object
      final PSReader aReader = new PSReader (aSchRes);
      final PSSchema aSchema = aReader.readSchemaFromXML (aDoc.getDocumentElement ());
      assertNotNull (aSchema);

      // Create a compiled schema
      final String sPhaseID = null;
      final IPSErrorHandler aErrorHandler = null;
      final IPSBoundSchema aBoundSchema = PSXPathQueryBinding.getInstance ().bind (aSchema, sPhaseID, aErrorHandler);

      // Validate completely
      final SchematronOutputType aSO = aBoundSchema.validateComplete (XMLReader.readXMLDOM (aXmlRes));
      assertNotNull (aSO);

      if (true)
        System.out.println (SVRLWriter.createXMLString (aSO));
    }
  }

  @Test
  public void testBindAllValidSchematrons () throws SchematronException
  {
    for (final IReadableResource aRes : SchematronTestHelper.getAllValidSchematronFiles ())
    {
      // Parse the schema
      final PSSchema aSchema = new PSReader (aRes).readSchema ();
      assertNotNull (aSchema);
      PhlocTestUtils.testToStringImplementation (aSchema);

      final InMemoryLogger aLogger = new InMemoryLogger ();
      assertTrue (aRes.getPath (), aSchema.isValid (aLogger));
      assertTrue (aLogger.isEmpty ());

      // Create a compiled schema
      final String sPhaseID = null;
      final IPSErrorHandler aErrorHandler = null;
      final IPSBoundSchema aBoundSchema = PSXPathQueryBinding.getInstance ().bind (aSchema, sPhaseID, aErrorHandler);
      assertNotNull (aBoundSchema);
    }
  }

  @Test
  public void testBindAllInvalidSchematrons ()
  {
    for (final IReadableResource aRes : SchematronTestHelper.getAllInvalidSchematronFiles ())
    {
      System.out.println (aRes);
      try
      {
        // Parse the schema
        final PSSchema aSchema = new PSReader (aRes).readSchema ();
        final CollectingPSErrorHandler aCEH = new CollectingPSErrorHandler ();
        PSXPathQueryBinding.getInstance ().bind (aSchema, null, aCEH);
        // Either an ERROR was collected or an exception was thrown
        if (aCEH.getResourceErrors ().getMostSevereErrorLevel ().isLessSevereThan (EErrorLevel.ERROR))
          fail ();
      }
      catch (final SchematronException ex)
      {
        System.out.println ("  " + ex.getMessage ());
      }
    }
  }
}
