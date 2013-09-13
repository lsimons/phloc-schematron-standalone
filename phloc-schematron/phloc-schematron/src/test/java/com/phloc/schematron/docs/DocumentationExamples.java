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
package com.phloc.schematron.docs;

import java.io.File;

import javax.annotation.Nonnull;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

import com.phloc.commons.io.resource.FileSystemResource;
import com.phloc.commons.microdom.serialize.MicroWriter;
import com.phloc.commons.xml.serialize.XMLReader;
import com.phloc.schematron.ISchematronResource;
import com.phloc.schematron.pure.SchematronResourcePure;
import com.phloc.schematron.pure.binding.IPSQueryBinding;
import com.phloc.schematron.pure.binding.PSQueryBindingRegistry;
import com.phloc.schematron.pure.bound.IPSBoundSchema;
import com.phloc.schematron.pure.exchange.PSReader;
import com.phloc.schematron.pure.model.PSSchema;
import com.phloc.schematron.pure.model.PSTitle;
import com.phloc.schematron.pure.preprocess.PSPreprocessor;
import com.phloc.schematron.xslt.SchematronResourceSCH;

/**
 * This class contains code examples that are used in the documentation.
 * 
 * @author Philip Helger
 */
public class DocumentationExamples
{
  public static boolean validateXMLViaXSLTSchematron (@Nonnull final File aSchematronFile, @Nonnull final File aXMLFile) throws Exception
  {
    final ISchematronResource aResSCH = SchematronResourceSCH.fromFile (aSchematronFile);
    if (!aResSCH.isValidSchematron ())
      throw new IllegalArgumentException ("Invalid Schematron!");
    return aResSCH.getSchematronValidity (new StreamSource (aXMLFile)).isValid ();
  }

  public static boolean validateXMLViaPureSchematron (@Nonnull final File aSchematronFile, @Nonnull final File aXMLFile) throws Exception
  {
    final ISchematronResource aResPure = SchematronResourcePure.fromFile (aSchematronFile);
    if (!aResPure.isValidSchematron ())
      throw new IllegalArgumentException ("Invalid Schematron!");
    return aResPure.getSchematronValidity (new StreamSource (aXMLFile)).isValid ();
  }

  public static boolean validateXMLViaPureSchematron2 (@Nonnull final File aSchematronFile, @Nonnull final File aXMLFile) throws Exception
  {
    // Read the schematron from file
    final PSSchema aSchema = new PSReader (new FileSystemResource (aSchematronFile)).readSchema ();
    if (!aSchema.isValid ())
      throw new IllegalArgumentException ("Invalid Schematron!");
    // Resolve the query binding to use
    final IPSQueryBinding aQueryBinding = PSQueryBindingRegistry.getQueryBindingOfNameOrThrow (aSchema.getQueryBinding ());
    // Pre-process schema
    final PSPreprocessor aPreprocessor = new PSPreprocessor (aQueryBinding);
    aPreprocessor.setKeepTitles (true);
    final PSSchema aPreprocessedSchema = aPreprocessor.getAsPreprocessedSchema (aSchema);
    // Bind the pre-processed schema
    final IPSBoundSchema aBoundSchema = aQueryBinding.bind (aPreprocessedSchema, null, null);
    // Read the XML file
    final Document aXMLNode = XMLReader.readXMLDOM (aXMLFile);
    if (aXMLNode == null)
      return false;
    // Perform the validation
    return aBoundSchema.validatePartially (aXMLNode).isValid ();
  }

  public static boolean readModifyAndWrite (@Nonnull final File aSchematronFile) throws Exception
  {
    final PSSchema aSchema = new PSReader (new FileSystemResource (aSchematronFile)).readSchema ();
    final PSTitle aTitle = new PSTitle ();
    aTitle.addText ("Created by phloc-schematron");
    aSchema.setTitle (aTitle);
    return MicroWriter.writeToFile (aSchema.getAsMicroElement (), aSchematronFile).isSuccess ();
  }
}
