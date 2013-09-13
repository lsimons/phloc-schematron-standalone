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
package com.phloc.commons.io.streamprovider;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import org.junit.Test;

import com.phloc.commons.charset.CCharset;
import com.phloc.commons.io.streams.StreamUtils;

/**
 * Test class for class {@link StringInputStreamProvider}.
 * 
 * @author Philip Helger
 */
public final class StringInputStreamProviderTest
{
  @SuppressWarnings ("deprecation")
  @Test
  public void testSimple ()
  {
    final String s = "Hallo Weltäöü";

    // String constructor
    InputStream aIS = new StringInputStreamProvider (s, CCharset.CHARSET_UTF_8).getInputStream ();
    assertEquals (s, StreamUtils.getAllBytesAsString (aIS, CCharset.CHARSET_UTF_8));

    // char[] constructor
    aIS = new StringInputStreamProvider (s.toCharArray (), CCharset.CHARSET_UTF_8).getInputStream ();
    assertEquals (s, StreamUtils.getAllBytesAsString (aIS, CCharset.CHARSET_UTF_8));

    // CharSequence constructor
    aIS = new StringInputStreamProvider (new StringBuilder (s), CCharset.CHARSET_UTF_8).getInputStream ();
    assertEquals (s, StreamUtils.getAllBytesAsString (aIS, CCharset.CHARSET_UTF_8));
  }

  @Test
  public void testSimpleCharset ()
  {
    final String s = "Hallo Weltäöü";

    // String constructor
    InputStream aIS = new StringInputStreamProvider (s, CCharset.CHARSET_UTF_8_OBJ).getInputStream ();
    assertEquals (s, StreamUtils.getAllBytesAsString (aIS, CCharset.CHARSET_UTF_8_OBJ));

    // char[] constructor
    aIS = new StringInputStreamProvider (s.toCharArray (), CCharset.CHARSET_UTF_8_OBJ).getInputStream ();
    assertEquals (s, StreamUtils.getAllBytesAsString (aIS, CCharset.CHARSET_UTF_8_OBJ));

    // CharSequence constructor
    aIS = new StringInputStreamProvider (new StringBuilder (s), CCharset.CHARSET_UTF_8_OBJ).getInputStream ();
    assertEquals (s, StreamUtils.getAllBytesAsString (aIS, CCharset.CHARSET_UTF_8_OBJ));
  }
}
