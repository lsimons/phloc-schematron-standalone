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
package com.phloc.schematron.pure.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.log.InMemoryLogger;
import com.phloc.commons.microdom.IMicroElement;
import com.phloc.commons.microdom.serialize.MicroWriter;
import com.phloc.schematron.mock.SchematronTestHelper;
import com.phloc.schematron.pure.exchange.PSReader;

/**
 * Test class for class {@link PSSchema} and {@link PSReader}.
 * 
 * @author Philip Helger
 */
public final class PSSchemaTest
{
  @Test
  public void testReadAll () throws Exception
  {
    for (final IReadableResource aRes : SchematronTestHelper.getAllValidSchematronFiles ())
    {
      final PSReader aReader = new PSReader (aRes);

      // Parse the schema
      final PSSchema aSchema1 = aReader.readSchema ();
      assertNotNull (aSchema1);
      final InMemoryLogger aLogger = new InMemoryLogger ();
      assertTrue (aRes.getPath (), aSchema1.isValid (aLogger));
      assertTrue (aLogger.isEmpty ());

      // Convert back to XML
      final IMicroElement e1 = aSchema1.getAsMicroElement ();
      final String sXML1 = MicroWriter.getXMLString (e1);

      // Re-read the created XML and re-create it
      final PSSchema aSchema2 = aReader.readSchemaFromXML (e1);
      final IMicroElement e2 = aSchema2.getAsMicroElement ();
      final String sXML2 = MicroWriter.getXMLString (e2);

      // Originally created XML and re-created-written XML must match
      assertEquals (sXML1, sXML2);
    }
  }
}
