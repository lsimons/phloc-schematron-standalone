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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.URIResolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.GlobalDebug;
import com.phloc.commons.annotations.DevelopersNote;
import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.xml.serialize.XMLWriter;

/**
 * Factory for creating {@link ISchematronXSLTProvider} objects.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@ThreadSafe
public final class SchematronResourceSCHCache
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (SchematronResourceSCHCache.class);
  private static final Lock s_aLock = new ReentrantLock ();
  private static final Map <String, ISchematronXSLTProvider> s_aCache = new HashMap <String, ISchematronXSLTProvider> ();

  private SchematronResourceSCHCache ()
  {}

  /**
   * Create a new Schematron validator for the passed resource.
   * 
   * @param aSchematronResource
   *        The resource of the Schematron rules. May not be <code>null</code>.
   * @param aCustomErrorListener
   *        An optional custom XSLT error listener that is used when converting
   *        the Schematron resource to an XSLT document. May be
   *        <code>null</code>.
   * @param aCustomURIResolver
   *        An optional custom XSLT URI resolver that is used when converting
   *        the Schematron resource to an XSLT document. May be
   *        <code>null</code>.
   * @return <code>null</code> if the passed Schematron resource does not exist
   *         or is invalid.
   */
  @Nullable
  @Deprecated
  @DevelopersNote ("For binary compatibility to schematron2xslt-maven-plugin 2.5.0")
  public static ISchematronXSLTProvider createSchematronXSLTProvider (@Nonnull final IReadableResource aSchematronResource,
                                                                      @Nullable final ErrorListener aCustomErrorListener,
                                                                      @Nullable final URIResolver aCustomURIResolver)
  {
    return createSchematronXSLTProvider (aSchematronResource, aCustomErrorListener, aCustomURIResolver, null, null);
  }

  /**
   * Create a new Schematron validator for the passed resource.
   * 
   * @param aSchematronResource
   *        The resource of the Schematron rules. May not be <code>null</code>.
   * @param aCustomErrorListener
   *        An optional custom XSLT error listener that is used when converting
   *        the Schematron resource to an XSLT document. May be
   *        <code>null</code>.
   * @param aCustomURIResolver
   *        An optional custom XSLT URI resolver that is used when converting
   *        the Schematron resource to an XSLT document. May be
   *        <code>null</code>.
   * @param sPhase
   *        Optional phase to use. If not specified, the defaultPhase from the
   *        schema is used. If no default phase is specified, than all patterns
   *        are used
   * @param sLanguageCode
   *        An optional language code for the error messages. <code>null</code>
   *        means English. Supported language codes are: cs, de, en, fr, nl (see
   *        directory files schematron\20100414-xslt2\sch-messages-??.xhtml).
   * @return <code>null</code> if the passed Schematron resource does not exist
   *         or is invalid.
   */
  @Nullable
  public static ISchematronXSLTProvider createSchematronXSLTProvider (@Nonnull final IReadableResource aSchematronResource,
                                                                      @Nullable final ErrorListener aCustomErrorListener,
                                                                      @Nullable final URIResolver aCustomURIResolver,
                                                                      @Nullable final String sPhase,
                                                                      @Nullable final String sLanguageCode)
  {
    if (GlobalDebug.isDebugMode () && s_aLogger.isInfoEnabled ())
      s_aLogger.info ("Compiling Schematron instance " + aSchematronResource.toString ());

    final SchematronProviderXSLTFromSCH aXSLTPreprocessor = new SchematronProviderXSLTFromSCH (aSchematronResource,
                                                                                               aCustomErrorListener,
                                                                                               aCustomURIResolver,
                                                                                               sPhase,
                                                                                               sLanguageCode);
    if (!aXSLTPreprocessor.isValidSchematron ())
    {
      // Schematron is invalid -> parsing failed
      s_aLogger.warn ("The Schematron resource '" + aSchematronResource.getResourceID () + "' is invalid!");
      if (GlobalDebug.isDebugMode () && aXSLTPreprocessor.getXSLTDocument () != null)
      {
        // Log the created XSLT document for better error tracking
        s_aLogger.warn ("  Created XSLT document:\n" + XMLWriter.getXMLString (aXSLTPreprocessor.getXSLTDocument ()));
      }
      return null;
    }

    // If it is a valid schematron, there must be a result XSLT present!
    if (aXSLTPreprocessor.getXSLTDocument () == null)
      throw new IllegalStateException ("No XSLT document retrieved from Schematron resource '" +
                                       aSchematronResource.getResourceID () +
                                       "'!");

    // Create the main validator for the schematron
    return aXSLTPreprocessor;
  }

  /**
   * Get the Schematron validator for the passed resource using a cache.
   * 
   * @param aSchematronResource
   *        The resource of the Schematron rules. May not be <code>null</code>.
   * @param aCustomErrorListener
   *        An optional custom XSLT error listener that is used when converting
   *        the Schematron resource to an XSLT document. May be
   *        <code>null</code>.
   * @param aCustomURIResolver
   *        An optional custom XSLT URI resolver that is used when converting
   *        the Schematron resource to an XSLT document. May be
   *        <code>null</code>.
   * @param sPhase
   *        Optional phase to use. If not specified, the defaultPhase from the
   *        schema is used. If no default phase is specified, than all patterns
   *        are used
   * @param sLanguageCode
   *        An optional language code for the error messages. <code>null</code>
   *        means English. Supported language codes are: cs, de, en, fr, nl (see
   *        directory files schematron\20100414-xslt2\sch-messages-??.xhtml).
   * @return <code>null</code> if the passed Schematron resource does not exist
   *         or is invalid.
   */
  @Nullable
  public static ISchematronXSLTProvider getSchematronXSLTProvider (@Nonnull final IReadableResource aSchematronResource,
                                                                   @Nullable final ErrorListener aCustomErrorListener,
                                                                   @Nullable final URIResolver aCustomURIResolver,
                                                                   @Nullable final String sPhase,
                                                                   @Nullable final String sLanguageCode)
  {
    if (aSchematronResource == null)
      throw new NullPointerException ("resource");

    if (!aSchematronResource.exists ())
    {
      s_aLogger.warn ("Schematron resource " + aSchematronResource + " does not exist!");
      return null;
    }

    s_aLock.lock ();
    try
    {
      // Determine the unique resource ID for caching
      final String sResourceID = aSchematronResource.getResourceID ();

      // Validator already in the cache?
      ISchematronXSLTProvider aProvider = s_aCache.get (sResourceID);
      if (aProvider == null)
      {
        // Create new object and put in cache
        aProvider = createSchematronXSLTProvider (aSchematronResource,
                                                  aCustomErrorListener,
                                                  aCustomURIResolver,
                                                  sPhase,
                                                  sLanguageCode);
        if (aProvider != null)
          s_aCache.put (sResourceID, aProvider);
      }
      return aProvider;
    }
    finally
    {
      s_aLock.unlock ();
    }
  }
}
