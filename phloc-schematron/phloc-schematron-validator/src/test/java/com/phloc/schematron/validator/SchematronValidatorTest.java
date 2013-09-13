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
package com.phloc.schematron.validator;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.microdom.IMicroDocument;
import com.phloc.schematron.SchematronHelper;
import com.phloc.schematron.mock.SchematronTestHelper;

/**
 * Test class for class {@link SchematronValidator}.
 * 
 * @author Philip Helger
 */
public final class SchematronValidatorTest
{
  @Test
  public void testSchematron ()
  {
    // Check all documents
    for (final IReadableResource aRes : SchematronTestHelper.getAllValidSchematronFiles ())
    {
      final IMicroDocument aDoc = SchematronHelper.getWithResolvedSchematronIncludes (aRes);
      final boolean bIsValid = SchematronValidator.isValidSchematron (aDoc);
      assertTrue (aRes.getPath (), bIsValid);
    }
  }
}
