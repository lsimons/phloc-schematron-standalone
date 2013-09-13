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
package com.phloc.commons.io.file.filter;

import java.io.File;
import java.io.FilenameFilter;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.collections.ArrayHelper;
import com.phloc.commons.io.file.FilenameHelper;
import com.phloc.commons.regex.RegExHelper;
import com.phloc.commons.string.ToStringGenerator;

/**
 * A filter that rejects certain file names, based on a regular expression. If
 * at least one regular expressions is fulfilled, the file is rejected. The
 * filter is applied on directories and files!
 * 
 * @author Philip Helger
 */
@ThreadSafe
public final class FilenameFilterMatchNoRegEx implements FilenameFilter
{
  private final String [] m_aRegExs;

  public FilenameFilterMatchNoRegEx (@Nonnull @Nonempty final String sRegEx)
  {
    this (new String [] { sRegEx });
  }

  public FilenameFilterMatchNoRegEx (@Nonnull @Nonempty final String... aRegExs)
  {
    if (ArrayHelper.isEmpty (aRegExs))
      throw new IllegalArgumentException ("empty array passed");
    m_aRegExs = aRegExs;
  }

  public boolean accept (@Nonnull final File aDir, @Nonnull final String sName)
  {
    final String sRealName = FilenameHelper.getSecureFilename (sName);
    if (sRealName == null)
      return false;
    for (final String sRegEx : m_aRegExs)
      if (RegExHelper.stringMatchesPattern (sRegEx, sRealName))
        return false;
    return true;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("regExs", m_aRegExs).toString ();
  }
}
