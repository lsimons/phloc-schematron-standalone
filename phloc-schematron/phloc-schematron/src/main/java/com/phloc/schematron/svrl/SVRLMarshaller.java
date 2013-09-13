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
import javax.annotation.concurrent.Immutable;
import javax.xml.bind.JAXBElement;

import org.oclc.purl.dsdl.svrl.ObjectFactory;
import org.oclc.purl.dsdl.svrl.SchematronOutputType;

import com.phloc.commons.GlobalDebug;
import com.phloc.commons.io.resource.ClassPathResource;
import com.phloc.commons.jaxb.utils.AbstractJAXBMarshaller;

/**
 * This is the XML reader and write for Schematron SVRL documents. It reads XML
 * DOM documents and returns {@link SchematronOutputType} elements and vice
 * versa. The reading and writing itself is done with JAXB.<br>
 * 
 * @author Philip Helger
 */
@Immutable
public class SVRLMarshaller extends AbstractJAXBMarshaller <SchematronOutputType>
{
  public SVRLMarshaller ()
  {
    super (SchematronOutputType.class, new ClassPathResource (CSVRL.SVRL_XSD_PATH));
    setWriteFormatted (GlobalDebug.isDebugMode ());
  }

  @Override
  @Nonnull
  protected JAXBElement <SchematronOutputType> wrapObject (final SchematronOutputType aObject)
  {
    return new ObjectFactory ().createSchematronOutput (aObject);
  }
}
