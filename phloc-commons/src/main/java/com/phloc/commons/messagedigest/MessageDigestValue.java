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
package com.phloc.commons.messagedigest;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import javax.annotation.Nonnull;
import javax.annotation.WillNotClose;
import javax.annotation.concurrent.Immutable;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.collections.ArrayHelper;
import com.phloc.commons.equals.EqualsUtils;
import com.phloc.commons.hash.HashCodeGenerator;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.string.ToStringGenerator;

/**
 * Represents a single message digest value as the combination of the algorithm
 * and the digest bytes.
 * 
 * @author Philip Helger
 */
@Immutable
public final class MessageDigestValue
{
  private final EMessageDigestAlgorithm m_eAlgorithm;
  private final byte [] m_aDigestBytes;

  public MessageDigestValue (@Nonnull final EMessageDigestAlgorithm eAlgorithm, final byte [] aDigestBytes)
  {
    if (eAlgorithm == null)
      throw new NullPointerException ("algorithm");
    if (ArrayHelper.isEmpty (aDigestBytes))
      throw new IllegalArgumentException ("hashBytes");
    m_eAlgorithm = eAlgorithm;
    m_aDigestBytes = ArrayHelper.getCopy (aDigestBytes);
  }

  /**
   * @return The message digest algorithm used. Never <code>null</code>.
   */
  @Nonnull
  public EMessageDigestAlgorithm getAlgorithm ()
  {
    return m_eAlgorithm;
  }

  /**
   * @return A copy of the message digest bytes. The length depends on the used
   *         algorithm. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public byte [] getDigestBytes ()
  {
    return ArrayHelper.getCopy (m_aDigestBytes);
  }

  /**
   * Write the digest bytes to the specified output stream.
   * 
   * @param aOS
   *        The output stream to write to. May not be <code>null</code>.
   * @throws IOException
   *         In case of a write error
   */
  public void writeDigestBytes (@Nonnull @WillNotClose final OutputStream aOS) throws IOException
  {
    aOS.write (m_aDigestBytes, 0, m_aDigestBytes.length);
  }

  /**
   * @return The hex-encoded String of the message digest bytes. Never
   *         <code>null</code>.
   */
  @Nonnull
  @Nonempty
  public String getDigestString ()
  {
    return StringHelper.getHexEncoded (m_aDigestBytes);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!(o instanceof MessageDigestValue))
      return false;
    final MessageDigestValue rhs = (MessageDigestValue) o;
    return m_eAlgorithm.equals (rhs.m_eAlgorithm) && EqualsUtils.equals (m_aDigestBytes, rhs.m_aDigestBytes);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_eAlgorithm).append (m_aDigestBytes).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("algorithm", m_eAlgorithm)
                                       .append ("bytes", Arrays.toString (m_aDigestBytes))
                                       .toString ();
  }

  /**
   * Create a new {@link MessageDigestValue} object based on the passed source
   * byte array
   * 
   * @param aBytes
   *        The byte array to create the hash value from. May not be
   *        <code>null</code>.
   * @param eAlgorithm
   *        The algorithm to be used. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static MessageDigestValue create (@Nonnull final byte [] aBytes,
                                           @Nonnull final EMessageDigestAlgorithm eAlgorithm)
  {
    final byte [] aDigestBytes = MessageDigestGeneratorHelper.getDigest (aBytes, eAlgorithm);
    return new MessageDigestValue (eAlgorithm, aDigestBytes);
  }
}
