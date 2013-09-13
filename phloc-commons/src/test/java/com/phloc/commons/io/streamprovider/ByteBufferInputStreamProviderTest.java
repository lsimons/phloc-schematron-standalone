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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.nio.ByteBuffer;

import org.junit.Test;

import com.phloc.commons.charset.CCharset;
import com.phloc.commons.charset.CharsetManager;
import com.phloc.commons.io.streams.StreamUtils;

/**
 * Test class for class {@link ByteBufferInputStreamProvider}.
 * 
 * @author Philip Helger
 */
public final class ByteBufferInputStreamProviderTest
{
  @Test
  public void testSimple ()
  {
    final byte [] aBytes = CharsetManager.getAsBytes ("Hallo Weltäöü", CCharset.CHARSET_ISO_8859_1_OBJ);
    final ByteBuffer bb = ByteBuffer.wrap (aBytes);
    final ByteBufferInputStreamProvider aISP = new ByteBufferInputStreamProvider (bb);
    final InputStream aIS = aISP.getInputStream ();
    assertArrayEquals (aBytes, StreamUtils.getAllBytes (aIS));
    StreamUtils.close (aISP.getReader (CCharset.CHARSET_UTF_8_OBJ));
    assertNotNull (aISP.toString ());
  }
}
