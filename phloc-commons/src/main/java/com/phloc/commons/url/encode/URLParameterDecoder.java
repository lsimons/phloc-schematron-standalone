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

import java.nio.charset.Charset;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.encode.IDecoder;
import com.phloc.commons.url.URLUtils;

/**
 * Decoder for URL parameters
 * 
 * @author Philip Helger
 */
public class URLParameterDecoder implements IDecoder <String>
{
  private final Charset m_aCharset;

  public URLParameterDecoder (@Nonnull final Charset aCharset)
  {
    if (aCharset == null)
      throw new NullPointerException ("charset");
    m_aCharset = aCharset;
  }

  @Nullable
  public String decode (@Nullable final String sInput)
  {
    return sInput == null ? null : URLUtils.urlDecode (sInput, m_aCharset);
  }
}
