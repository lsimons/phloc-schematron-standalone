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
package com.phloc.commons.combine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.phloc.commons.mock.PhlocTestUtils;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Test class for class {@link CombinatorStringWithSeparatorIgnoreNull}.
 *
 * @author Philip Helger
 */
public final class CombinatorStringWithSeparatorIgnoreNullTest
{

  @Test
  @SuppressFBWarnings (value = "NP_NONNULL_PARAM_VIOLATION")
  public void testGetStringCombinatorWithSeparatorIgnoreNull ()
  {
    final ICombinator <String> c = new CombinatorStringWithSeparatorIgnoreNull (";");
    assertEquals ("a;b", c.combine ("a", "b"));
    assertEquals ("a", c.combine ("a", null));
    assertEquals ("b", c.combine (null, "b"));
    assertEquals (null, c.combine (null, null));

    try
    {
      // Empty separator not allowed
      new CombinatorStringWithSeparatorIgnoreNull ("");
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    try
    {
      // Empty separator not allowed
      new CombinatorStringWithSeparatorIgnoreNull (null);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    PhlocTestUtils.testDefaultImplementationWithEqualContentObject (new CombinatorStringWithSeparatorIgnoreNull (";"),
                                                                    new CombinatorStringWithSeparatorIgnoreNull (";"));
    PhlocTestUtils.testDefaultImplementationWithDifferentContentObject (new CombinatorStringWithSeparatorIgnoreNull (";"),
                                                                        new CombinatorStringWithSeparatorIgnoreNull (","));
  }
}
