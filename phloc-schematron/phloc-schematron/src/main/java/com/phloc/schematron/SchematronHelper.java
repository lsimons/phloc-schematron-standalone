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
package com.phloc.schematron;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.oclc.purl.dsdl.svrl.SchematronOutputType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import com.phloc.commons.annotations.PresentForCodeCoverage;
import com.phloc.commons.error.IResourceErrorGroup;
import com.phloc.commons.error.ResourceErrorGroup;
import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.microdom.IMicroDocument;
import com.phloc.commons.microdom.IMicroElement;
import com.phloc.commons.microdom.serialize.MicroReader;
import com.phloc.schematron.resolve.DefaultSchematronIncludeResolver;
import com.phloc.schematron.svrl.SVRLFailedAssert;
import com.phloc.schematron.svrl.SVRLResourceError;
import com.phloc.schematron.svrl.SVRLUtils;

/**
 * This is a helper class that provides a way to easily apply an Schematron
 * resource on an XML resource.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class SchematronHelper
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (SchematronHelper.class);

  @PresentForCodeCoverage
  @SuppressWarnings ("unused")
  private static final SchematronHelper s_aInstance = new SchematronHelper ();

  private SchematronHelper ()
  {}

  /**
   * Apply the passed schematron on the passed XML resource using a custom error
   * handler.
   * 
   * @param aSchematron
   *        The Schematron resource. May not be <code>null</code>.
   * @param aXML
   *        The XML resource. May not be <code>null</code>.
   * @return <code>null</code> if either the Schematron or the XML could not be
   *         read.
   * @throws IllegalStateException
   *         if the processing throws an unexpected exception.
   */
  @Nullable
  public static SchematronOutputType applySchematron (@Nonnull final ISchematronResource aSchematron,
                                                      @Nonnull final IReadableResource aXML)
  {
    if (aSchematron == null)
      throw new NullPointerException ("schematron");
    if (aXML == null)
      throw new NullPointerException ("XML document");

    try
    {
      // Apply Schematron on XML
      return aSchematron.applySchematronValidationToSVRL (aXML);
    }
    catch (final Exception ex)
    {
      throw new IllegalArgumentException ("Failed to apply Schematron " +
                                          aSchematron.getID () +
                                          " onto XML resource " +
                                          aXML.getResourceID (), ex);
    }
  }

  /**
   * Apply the passed schematron on the passed XML resource.
   * 
   * @param aSchematron
   *        The Schematron resource. May not be <code>null</code>.
   * @param aXML
   *        The XML resource. May not be <code>null</code>.
   * @return <code>null</code> if either the Schematron or the XML could not be
   *         read.
   * @throws IllegalStateException
   *         if the processing throws an unexpected exception.
   */
  @Nullable
  public static SchematronOutputType applySchematron (@Nonnull final ISchematronResource aSchematron,
                                                      @Nonnull final Source aXML)
  {
    if (aSchematron == null)
      throw new NullPointerException ("schematron");
    if (aXML == null)
      throw new NullPointerException ("XML document");

    try
    {
      // Apply Schematron on XML.
      return aSchematron.applySchematronValidationToSVRL (aXML);
    }
    catch (final Exception ex)
    {
      throw new IllegalArgumentException ("Failed to apply Schematron " +
                                          aSchematron.getID () +
                                          " onto XML source " +
                                          aXML, ex);
    }
  }

  /**
   * Apply the passed schematron on the passed XML node.
   * 
   * @param aSchematron
   *        The Schematron resource. May not be <code>null</code>.
   * @param aNode
   *        The XML node. May not be <code>null</code>.
   * @return <code>null</code> if either the Schematron or the XML could not be
   *         read.
   * @throws IllegalStateException
   *         if the processing throws an unexpected exception.
   */
  @Nullable
  public static SchematronOutputType applySchematron (@Nonnull final ISchematronResource aSchematron,
                                                      @Nonnull final Node aNode)
  {
    if (aSchematron == null)
      throw new NullPointerException ("schematron");
    if (aNode == null)
      throw new NullPointerException ("Node");

    return applySchematron (aSchematron, new DOMSource (aNode));
  }

  /**
   * Convert a {@link SchematronOutputType} to an {@link IResourceErrorGroup}.
   * 
   * @param aSchematronOutput
   *        The result of Schematron validation
   * @param sResourceName
   *        The name of the resource that was validated (may be a file path
   *        etc.)
   * @return List non-<code>null</code> error list of {@link SVRLResourceError}
   *         objects.
   */
  @Nonnull
  public static IResourceErrorGroup convertToResourceErrorGroup (@Nonnull final SchematronOutputType aSchematronOutput,
                                                                 @Nullable final String sResourceName)
  {
    if (aSchematronOutput == null)
      throw new NullPointerException ("schematronOutput");

    final ResourceErrorGroup ret = new ResourceErrorGroup ();
    for (final SVRLFailedAssert aFailedAssert : SVRLUtils.getAllFailedAssertions (aSchematronOutput))
      ret.addResourceError (aFailedAssert.getAsResourceError (sResourceName));
    return ret;
  }

  private static void _recursiveResolveAllSchematronIncludes (@Nonnull final IMicroElement eRoot,
                                                              @Nonnull final IReadableResource aResource)
  {
    if (eRoot != null)
    {
      final DefaultSchematronIncludeResolver aIncludeResolver = new DefaultSchematronIncludeResolver (aResource);

      for (final IMicroElement aElement : eRoot.getAllChildElementsRecursive ())
        if (CSchematron.NAMESPACE_SCHEMATRON.equals (aElement.getNamespaceURI ()) &&
            aElement.getLocalName ().equals (CSchematronXML.ELEMENT_INCLUDE))
        {
          final String sHref = aElement.getAttribute (CSchematronXML.ATTR_HREF);
          try
          {
            final IReadableResource aIncludeRes = aIncludeResolver.getResolvedSchematronResource (sHref);

            if (s_aLogger.isDebugEnabled ())
              s_aLogger.debug ("Resolved '" +
                               sHref +
                               "' relative to '" +
                               aIncludeResolver.getBaseHref () +
                               "' as '" +
                               aIncludeRes.getPath () +
                               "'");

            final IMicroDocument aIncludeDoc = MicroReader.readMicroXML (aIncludeRes);
            if (aIncludeDoc == null)
              throw new IllegalStateException ("Failed to parse include " + aIncludeRes);

            // Return the document element
            final IMicroElement aIncludeElement = aIncludeDoc.getDocumentElement ();
            // Important to detach from parent!
            aIncludeElement.detachFromParent ();

            // Recursive resolve includes
            _recursiveResolveAllSchematronIncludes (aIncludeElement, aIncludeRes);

            // Now replace in MicroDOM
            aElement.getParent ().replaceChild (aElement, aIncludeElement);
          }
          catch (final IOException ex)
          {
            throw new IllegalStateException ("Failed to read include " + sHref);
          }
        }
    }
  }

  /**
   * Resolve all Schematron includes of the passed resource.
   * 
   * @param aResource
   *        The Schematron resource to read. May not be <code>null</code>.
   * @return <code>null</code> if the passed resource could not be read as XML
   *         document
   */
  @Nullable
  public static IMicroDocument getWithResolvedSchematronIncludes (@Nonnull final IReadableResource aResource)
  {
    final IMicroDocument aDoc = MicroReader.readMicroXML (aResource);
    if (aDoc != null)
    {
      // Resolve all Schematron includes
      _recursiveResolveAllSchematronIncludes (aDoc.getDocumentElement (), aResource);
    }
    return aDoc;
  }
}
