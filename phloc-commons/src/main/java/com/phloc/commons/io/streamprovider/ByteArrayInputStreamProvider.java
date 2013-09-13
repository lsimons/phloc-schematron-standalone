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
import java.io.Reader;
import java.nio.charset.Charset;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.phloc.commons.io.IInputStreamAndReaderProvider;
import com.phloc.commons.io.streams.NonBlockingByteArrayInputStream;
import com.phloc.commons.io.streams.StreamUtils;
import com.phloc.commons.string.ToStringGenerator;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * An input stream provider based on a byte array.
 * 
 * @author Philip Helger
 */
public class ByteArrayInputStreamProvider implements IInputStreamAndReaderProvider
{
  private final byte [] m_aData;
  private final int m_nOfs;
  private final int m_nLen;

  public ByteArrayInputStreamProvider (@Nonnull final byte [] aData)
  {
    this (aData, 0, aData.length);
  }

  @SuppressFBWarnings ({ "EI_EXPOSE_REP2" })
  public ByteArrayInputStreamProvider (@Nonnull final byte [] aInput,
                                       @Nonnegative final int nOfs,
                                       @Nonnegative final int nLen)
  {
    if (aInput == null)
      throw new NullPointerException ("input");
    if (nOfs < 0 || nLen < 0 || (nOfs + nLen) > aInput.length)
      throw new IllegalArgumentException ("ofs:" + nOfs + ";len=" + nLen + ";bufLen=" + aInput.length);
    m_aData = aInput;
    m_nOfs = nOfs;
    m_nLen = nLen;
  }

  @Nonnull
  public final InputStream getInputStream ()
  {
    return new NonBlockingByteArrayInputStream (m_aData);
  }

  @Nonnull
  @Deprecated
  public final Reader getReader (@Nonnull final String sCharset)
  {
    return StreamUtils.createReader (getInputStream (), sCharset);
  }

  @Nonnull
  public final Reader getReader (@Nonnull final Charset aCharset)
  {
    return StreamUtils.createReader (getInputStream (), aCharset);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("byteArray[]", m_aData.length + " bytes")
                                       .append ("ofs", m_nOfs)
                                       .append ("len", m_nLen)
                                       .toString ();
  }
}
