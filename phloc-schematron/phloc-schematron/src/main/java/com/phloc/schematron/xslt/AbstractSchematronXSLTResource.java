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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;

import org.oclc.purl.dsdl.svrl.FailedAssert;
import org.oclc.purl.dsdl.svrl.SchematronOutputType;
import org.oclc.purl.dsdl.svrl.SuccessfulReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.state.EValidity;
import com.phloc.commons.string.ToStringGenerator;
import com.phloc.commons.xml.XMLFactory;
import com.phloc.commons.xml.transform.TransformSourceFactory;
import com.phloc.schematron.AbstractSchematronResource;
import com.phloc.schematron.svrl.SVRLReader;

/**
 * Abstract implementation of a Schematron resource that is based on XSLT
 * transformations.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@NotThreadSafe
public abstract class AbstractSchematronXSLTResource extends AbstractSchematronResource
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (AbstractSchematronXSLTResource.class);

  private final ErrorListener m_aCustomErrorListener;
  private final URIResolver m_aCustomURIResolver;
  private final ISchematronXSLTProvider m_aXSLTProvider;

  public AbstractSchematronXSLTResource (@Nonnull final IReadableResource aSCHResource,
                                         @Nullable final ErrorListener aCustomErrorListener,
                                         @Nullable final URIResolver aCustomURIResolver,
                                         @Nullable final ISchematronXSLTProvider aXSLTProvider)
  {
    super (aSCHResource);
    m_aCustomErrorListener = aCustomErrorListener;
    m_aCustomURIResolver = aCustomURIResolver;
    m_aXSLTProvider = aXSLTProvider;
  }

  public final boolean isValidSchematron ()
  {
    return m_aXSLTProvider != null && m_aXSLTProvider.isValidSchematron ();
  }

  @Nonnull
  public EValidity getSchematronValidity (@Nonnull final IReadableResource aXMLResource) throws Exception
  {
    if (aXMLResource == null)
      throw new NullPointerException ("XMLResource");

    if (!aXMLResource.exists ())
    {
      // Resource not found
      s_aLogger.warn ("XML resource " + aXMLResource + " does not exist!");
      return EValidity.INVALID;
    }

    return getSchematronValidity (TransformSourceFactory.create (aXMLResource));
  }

  @Nonnull
  public EValidity getSchematronValidity (@Nonnull final Source aXMLSource) throws Exception
  {
    // We don't have a short circuit here - apply the full validation
    final SchematronOutputType aSO = applySchematronValidationToSVRL (aXMLSource);

    // And now filter all elements that make the passed source invalid
    for (final Object aObj : aSO.getActivePatternAndFiredRuleAndFailedAssert ())
      if (aObj instanceof FailedAssert || aObj instanceof SuccessfulReport)
        return EValidity.INVALID;
    return EValidity.VALID;
  }

  @Nullable
  public Document applySchematronValidation (@Nonnull final IReadableResource aXMLResource) throws Exception
  {
    if (aXMLResource == null)
      throw new NullPointerException ("XMLResource");

    if (!aXMLResource.exists ())
    {
      // Resource not found
      s_aLogger.warn ("XML resource " + aXMLResource + " does not exist!");
      return null;
    }
    return applySchematronValidation (TransformSourceFactory.create (aXMLResource));
  }

  @Nullable
  public final Document applySchematronValidation (@Nonnull final Source aXMLSource) throws Exception
  {
    if (aXMLSource == null)
      throw new NullPointerException ("XMLSource");

    if (!isValidSchematron ())
      return null;

    // Create result document
    final Document ret = XMLFactory.newDocument ();

    // Create the transformer object from the templates specified in the
    // constructor
    final Transformer aTransformer = m_aXSLTProvider.getXSLTTemplates ().newTransformer ();
    if (m_aCustomErrorListener != null)
      aTransformer.setErrorListener (m_aCustomErrorListener);
    if (m_aCustomURIResolver != null)
      aTransformer.setURIResolver (m_aCustomURIResolver);

    // Do the main transformation
    aTransformer.transform (aXMLSource, new DOMResult (ret));
    return ret;
  }

  @Nullable
  public SchematronOutputType applySchematronValidationToSVRL (@Nonnull final IReadableResource aXMLResource) throws Exception
  {
    final Document aDoc = applySchematronValidation (aXMLResource);
    return aDoc == null ? null : SVRLReader.readXML (aDoc);
  }

  @Nullable
  public SchematronOutputType applySchematronValidationToSVRL (@Nonnull final Source aXMLSource) throws Exception
  {
    final Document aDoc = applySchematronValidation (aXMLSource);
    return aDoc == null ? null : SVRLReader.readXML (aDoc);
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .appendIfNotNull ("customErrListener", m_aCustomErrorListener)
                            .appendIfNotNull ("customURIResolver", m_aCustomURIResolver)
                            .append ("XSLTProvider", m_aXSLTProvider)
                            .toString ();
  }
}
