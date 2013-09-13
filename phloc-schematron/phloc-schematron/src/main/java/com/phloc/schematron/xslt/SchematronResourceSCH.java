/**
 * Copyright (C) 2013 phloc systems
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
package com.phloc.schematron.xslt;

import java.io.File;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.URIResolver;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.io.resource.ClassPathResource;
import com.phloc.commons.io.resource.FileSystemResource;

/**
 * A Schematron resource that is based on the original SCH file.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@NotThreadSafe
public class SchematronResourceSCH extends AbstractSchematronXSLTResource
{
  public SchematronResourceSCH (@Nonnull final IReadableResource aSCHResource)
  {
    this (aSCHResource, null, null, null, null);
  }

  public SchematronResourceSCH (@Nonnull final IReadableResource aSCHResource,
                                @Nullable final ErrorListener aCustomErrorListener,
                                @Nullable final URIResolver aCustomURIResolver)
  {
    this (aSCHResource, aCustomErrorListener, aCustomURIResolver, null, null);
  }

  public SchematronResourceSCH (@Nonnull final IReadableResource aSCHResource,
                                @Nullable final String sPhase,
                                @Nullable final String sLanguageCode)
  {
    this (aSCHResource, null, null, sPhase, sLanguageCode);
  }

  public SchematronResourceSCH (@Nonnull final IReadableResource aSCHResource,
                                @Nullable final ErrorListener aCustomErrorListener,
                                @Nullable final URIResolver aCustomURIResolver,
                                @Nullable final String sPhase,
                                @Nullable final String sLanguageCode)
  {
    super (aSCHResource,
           aCustomErrorListener,
           aCustomURIResolver,
           SchematronResourceSCHCache.getSchematronXSLTProvider (aSCHResource,
                                                                 aCustomErrorListener,
                                                                 aCustomURIResolver,
                                                                 sPhase,
                                                                 sLanguageCode));
  }

  @Nonnull
  public static SchematronResourceSCH fromClassPath (@Nonnull @Nonempty final String sSCHPath)
  {
    return new SchematronResourceSCH (new ClassPathResource (sSCHPath));
  }

  @Nonnull
  public static SchematronResourceSCH fromClassPath (@Nonnull @Nonempty final String sSCHPath,
                                                     @Nullable final ErrorListener aCustomErrorListener,
                                                     @Nullable final URIResolver aCustomURIResolver)
  {
    return new SchematronResourceSCH (new ClassPathResource (sSCHPath), aCustomErrorListener, aCustomURIResolver);
  }

  @Nonnull
  public static SchematronResourceSCH fromClassPath (@Nonnull @Nonempty final String sSCHPath,
                                                     @Nullable final String sPhase,
                                                     @Nullable final String sLanguageCode)
  {
    return new SchematronResourceSCH (new ClassPathResource (sSCHPath), sPhase, sLanguageCode);
  }

  @Nonnull
  public static SchematronResourceSCH fromClassPath (@Nonnull @Nonempty final String sSCHPath,
                                                     @Nullable final ErrorListener aCustomErrorListener,
                                                     @Nullable final URIResolver aCustomURIResolver,
                                                     @Nullable final String sPhase,
                                                     @Nullable final String sLanguageCode)
  {
    return new SchematronResourceSCH (new ClassPathResource (sSCHPath),
                                      aCustomErrorListener,
                                      aCustomURIResolver,
                                      sPhase,
                                      sLanguageCode);
  }

  @Nonnull
  public static SchematronResourceSCH fromFile (@Nonnull @Nonempty final String sSCHPath)
  {
    return new SchematronResourceSCH (new FileSystemResource (sSCHPath));
  }

  @Nonnull
  public static SchematronResourceSCH fromFile (@Nonnull @Nonempty final String sSCHPath,
                                                @Nullable final ErrorListener aCustomErrorListener,
                                                @Nullable final URIResolver aCustomURIResolver)
  {
    return new SchematronResourceSCH (new FileSystemResource (sSCHPath), aCustomErrorListener, aCustomURIResolver);
  }

  @Nonnull
  public static SchematronResourceSCH fromFile (@Nonnull @Nonempty final String sSCHPath,
                                                @Nullable final String sPhase,
                                                @Nullable final String sLanguageCode)
  {
    return new SchematronResourceSCH (new FileSystemResource (sSCHPath), sPhase, sLanguageCode);
  }

  @Nonnull
  public static SchematronResourceSCH fromFile (@Nonnull @Nonempty final String sSCHPath,
                                                @Nullable final ErrorListener aCustomErrorListener,
                                                @Nullable final URIResolver aCustomURIResolver,
                                                @Nullable final String sPhase,
                                                @Nullable final String sLanguageCode)
  {
    return new SchematronResourceSCH (new FileSystemResource (sSCHPath),
                                      aCustomErrorListener,
                                      aCustomURIResolver,
                                      sPhase,
                                      sLanguageCode);
  }

  @Nonnull
  public static SchematronResourceSCH fromFile (@Nonnull final File aSCHFile)
  {
    return new SchematronResourceSCH (new FileSystemResource (aSCHFile));
  }

  @Nonnull
  public static SchematronResourceSCH fromFile (@Nonnull final File aSCHFile,
                                                @Nullable final ErrorListener aCustomErrorListener,
                                                @Nullable final URIResolver aCustomURIResolver)
  {
    return new SchematronResourceSCH (new FileSystemResource (aSCHFile), aCustomErrorListener, aCustomURIResolver);
  }

  @Nonnull
  public static SchematronResourceSCH fromFile (@Nonnull final File aSCHFile,
                                                @Nullable final String sPhase,
                                                @Nullable final String sLanguageCode)
  {
    return new SchematronResourceSCH (new FileSystemResource (aSCHFile), sPhase, sLanguageCode);
  }

  @Nonnull
  public static SchematronResourceSCH fromFile (@Nonnull final File aSCHFile,
                                                @Nullable final ErrorListener aCustomErrorListener,
                                                @Nullable final URIResolver aCustomURIResolver,
                                                @Nullable final String sPhase,
                                                @Nullable final String sLanguageCode)
  {
    return new SchematronResourceSCH (new FileSystemResource (aSCHFile),
                                      aCustomErrorListener,
                                      aCustomURIResolver,
                                      sPhase,
                                      sLanguageCode);
  }
}
