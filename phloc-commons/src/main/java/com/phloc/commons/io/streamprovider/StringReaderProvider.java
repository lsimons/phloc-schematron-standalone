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

import java.io.Reader;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.phloc.commons.io.IReaderProvider;
import com.phloc.commons.io.streams.NonBlockingStringReader;
import com.phloc.commons.string.ToStringGenerator;

/**
 * An {@link Reader} provider based on a {@link String}.
 * 
 * @author Philip Helger
 */
public class StringReaderProvider implements IReaderProvider
{
  private final String m_sData;

  public StringReaderProvider (@Nonnull final char [] aChars)
  {
    this (new String (aChars));
  }

  public StringReaderProvider (@Nonnull final char [] aChars,
                               @Nonnegative final int nOfs,
                               @Nonnegative final int nLength)
  {
    this (new String (aChars, nOfs, nLength));
  }

  public StringReaderProvider (@Nonnull final CharSequence aData)
  {
    this (aData.toString ());
  }

  public StringReaderProvider (@Nonnull final String sData)
  {
    if (sData == null)
      throw new NullPointerException ("data");
    m_sData = sData;
  }

  @Nonnull
  public String getData ()
  {
    return m_sData;
  }

  @Nonnull
  public final NonBlockingStringReader getReader ()
  {
    return new NonBlockingStringReader (m_sData);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("data", m_sData).toString ();
  }
}
