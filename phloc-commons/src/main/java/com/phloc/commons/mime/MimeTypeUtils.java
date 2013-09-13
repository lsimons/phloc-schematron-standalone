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
package com.phloc.commons.mime;

import java.nio.charset.Charset;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.phloc.commons.charset.CharsetManager;
import com.phloc.commons.string.StringHelper;

/**
 * Contains some utility methods for handling MIME types.
 * 
 * @author Philip Helger
 */
@Immutable
public final class MimeTypeUtils
{
  private MimeTypeUtils ()
  {}

  /**
   * Determine the charset name from the provided MIME type.
   * 
   * @param aMimeType
   *        The MIME type to query. May be <code>null</code>.
   * @return <code>null</code> if no MIME type was provided or if the MIME type
   *         does not contain a "charset" parameter
   */
  @Nullable
  public static String getCharsetNameFromMimeType (@Nullable final IMimeType aMimeType)
  {
    return aMimeType == null ? null : aMimeType.getParameterValueWithName (CMimeType.PARAMETER_NAME_CHARSET);
  }

  /**
   * Determine the charset from the provided MIME type.
   * 
   * @param aMimeType
   *        The MIME type to query. May be <code>null</code>.
   * @return <code>null</code> if no MIME type was provided or if the MIME type
   *         does not contain a "charset" parameter or if the provided charset
   *         name is invalid.
   */
  @Nullable
  public static Charset getCharsetFromMimeType (@Nullable final IMimeType aMimeType)
  {
    final String sCharsetName = getCharsetNameFromMimeType (aMimeType);

    Charset ret = null;
    if (StringHelper.hasText (sCharsetName))
    {
      try
      {
        ret = CharsetManager.getCharsetFromName (sCharsetName);
      }
      catch (final IllegalArgumentException ex)
      {
        // Illegal charset name
      }
    }
    return ret;
  }
}
