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
package com.phloc.schematron.svrl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.xml.transform.Result;

import org.oclc.purl.dsdl.svrl.SchematronOutputType;
import org.w3c.dom.Document;

import com.phloc.commons.annotations.PresentForCodeCoverage;
import com.phloc.commons.state.ESuccess;

/**
 * This is the XML writer for Schematron SVRL documents. It reads
 * {@link SchematronOutputType} elements and converts them to W3C nodes. The
 * writing itself is done with JAXB.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class SVRLWriter
{
  @PresentForCodeCoverage
  @SuppressWarnings ("unused")
  private static final SVRLWriter s_aInstance = new SVRLWriter ();

  private SVRLWriter ()
  {}

  /**
   * Convert the passed schematron output element into an W3C Document node.
   * 
   * @param aSchematronOutput
   *        The schematron output to be converted. May not be <code>null</code>.
   * @return {@link ESuccess}
   */
  @Nonnull
  public static ESuccess writeSVRL (@Nonnull final SchematronOutputType aSchematronOutput, @Nonnull final Result aResult)
  {
    return new SVRLMarshaller ().write (aSchematronOutput, aResult);
  }

  /**
   * Convert the passed schematron output element into an W3C Document node.
   * 
   * @param aSchematronOutput
   *        The schematron output to be converted. May not be <code>null</code>.
   * @return <code>null</code> if conversion failed.
   */
  @Nullable
  public static Document createXML (@Nonnull final SchematronOutputType aSchematronOutput)
  {
    return new SVRLMarshaller ().write (aSchematronOutput);
  }

  /**
   * Utility method to directly convert the passed SVRL domain object to an XML
   * string.
   * 
   * @param aSchematronOutput
   *        The SVRL domain object to be converted. May not be null.
   * @return <code>null</code> if the passed domain object could not be
   *         converted because of validation errors.
   */
  @Nullable
  public static String createXMLString (@Nonnull final SchematronOutputType aSchematronOutput)
  {
    return new SVRLMarshaller ().getAsXMLString (aSchematronOutput);
  }
}
