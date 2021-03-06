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
package com.phloc.commons.url.encode;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.phloc.commons.charset.CCharset;

/**
 * Test class for class {@link URLParameterEncoder}.
 * 
 * @author Philip Helger
 */
public final class URLParameterEncoderTest
{
  @Test
  public void testEncode ()
  {
    final URLParameterEncoder aPE = new URLParameterEncoder (CCharset.CHARSET_ISO_8859_1_OBJ);
    assertEquals ("a+b+c", aPE.encode ("a b c"));
    assertEquals ("a+b+c%3Ad", aPE.encode ("a b c:d"));
  }
}
