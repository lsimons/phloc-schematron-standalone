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
package com.phloc.commons.io.streams;

import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.phloc.commons.string.ToStringGenerator;

/**
 * A wrapper around an {@link OutputStream} that counts the number of read
 * bytes.
 * 
 * @author Philip Helger
 */
public class CountingOutputStream extends WrappedOutputStream
{
  private long m_nBytesWritten = 0;

  public CountingOutputStream (@Nonnull final OutputStream aSourceOS)
  {
    super (aSourceOS);
  }

  @Override
  public void write (final int b) throws IOException
  {
    super.write (b);
    m_nBytesWritten++;
  }

  @Override
  public void write (final byte [] b, final int nOffset, final int nLength) throws IOException
  {
    super.write (b, nOffset, nLength);
    m_nBytesWritten += nLength;
  }

  /**
   * @return The number of written bytes.
   */
  @Nonnegative
  public long getBytesWritten ()
  {
    return m_nBytesWritten;
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("bytesWritten", m_nBytesWritten).toString ();
  }
}
