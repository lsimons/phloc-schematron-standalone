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
package com.phloc.commons.io.resolver;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.phloc.commons.hash.HashCodeGenerator;
import com.phloc.commons.io.EAppend;
import com.phloc.commons.io.IInputStreamResolver;
import com.phloc.commons.io.IOutputStreamResolver;
import com.phloc.commons.io.file.FileUtils;
import com.phloc.commons.string.ToStringGenerator;

/**
 * Implementation of the {@link IInputStreamResolver} and
 * {@link IOutputStreamResolver} interfaces for {@link File} objects.
 * 
 * @author Philip Helger
 */
@Immutable
public final class FileSystemStreamResolver implements IInputStreamResolver, IOutputStreamResolver
{
  private final File m_aBasePath;

  public FileSystemStreamResolver (@Nonnull final String sBasePath)
  {
    this (new File (sBasePath));
  }

  public FileSystemStreamResolver (@Nonnull final File aBasePath)
  {
    if (aBasePath == null)
      throw new NullPointerException ("basePath");
    if (!aBasePath.exists ())
      throw new IllegalArgumentException ("Base path does not exist: " + aBasePath);
    if (!aBasePath.isDirectory ())
      throw new IllegalArgumentException ("Only directories are allowed as base path: " + aBasePath);
    m_aBasePath = aBasePath;
  }

  @Nullable
  public InputStream getInputStream (@Nonnull final String sName)
  {
    return FileUtils.getInputStream (new File (m_aBasePath, sName));
  }

  @Nullable
  public OutputStream getOutputStream (@Nonnull final String sName, @Nonnull final EAppend eAppend)
  {
    return FileUtils.getOutputStream (new File (m_aBasePath, sName), eAppend);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!(o instanceof FileSystemStreamResolver))
      return false;
    final FileSystemStreamResolver rhs = (FileSystemStreamResolver) o;
    return m_aBasePath.equals (rhs.m_aBasePath);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aBasePath).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).appendIfNotNull ("basePath", m_aBasePath).toString ();
  }
}
