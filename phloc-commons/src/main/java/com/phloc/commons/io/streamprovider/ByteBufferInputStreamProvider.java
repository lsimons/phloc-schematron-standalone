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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import javax.annotation.Nonnull;

import com.phloc.commons.io.IInputStreamAndReaderProvider;
import com.phloc.commons.io.streams.ByteBufferInputStream;
import com.phloc.commons.io.streams.StreamUtils;
import com.phloc.commons.string.ToStringGenerator;

/**
 * An {@link InputStream} provider based on a {@link ByteBuffer}.
 * 
 * @author Philip Helger
 */
public class ByteBufferInputStreamProvider implements IInputStreamAndReaderProvider
{
  private final ByteBuffer m_aBuffer;

  public ByteBufferInputStreamProvider (@Nonnull final ByteBuffer aBuffer)
  {
    if (aBuffer == null)
      throw new NullPointerException ("buffer");
    m_aBuffer = aBuffer;
  }

  @Nonnull
  public ByteBuffer getByteBuffer ()
  {
    return m_aBuffer;
  }

  @Nonnull
  public final ByteBufferInputStream getInputStream ()
  {
    return new ByteBufferInputStream (m_aBuffer);
  }

  @Nonnull
  @Deprecated
  public final InputStreamReader getReader (@Nonnull final String sCharset)
  {
    return StreamUtils.createReader (getInputStream (), sCharset);
  }

  @Nonnull
  public final InputStreamReader getReader (@Nonnull final Charset aCharset)
  {
    return StreamUtils.createReader (getInputStream (), aCharset);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("buffer", m_aBuffer).toString ();
  }
}
