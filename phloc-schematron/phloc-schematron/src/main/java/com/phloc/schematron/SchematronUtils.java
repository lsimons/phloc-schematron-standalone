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

import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.phloc.commons.annotations.PresentForCodeCoverage;
import com.phloc.commons.io.resource.URLResource;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.xml.serialize.XMLReader;

/**
 * This is a common utility class.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class SchematronUtils
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (SchematronUtils.class);

  @PresentForCodeCoverage
  @SuppressWarnings ("unused")
  private static final SchematronUtils s_aInstance = new SchematronUtils ();

  private SchematronUtils ()
  {}

  @Nullable
  public static Node getNodeOfSource (@Nonnull final Source aSource) throws SAXException
  {
    if (aSource == null)
      throw new NullPointerException ("Source");

    if (aSource instanceof DOMSource)
      return ((DOMSource) aSource).getNode ();

    if (aSource instanceof StreamSource)
    {
      final StreamSource aStreamSource = (StreamSource) aSource;

      final InputStream aIS = aStreamSource.getInputStream ();
      if (aIS != null)
      {
        final Document aDoc = XMLReader.readXMLDOM (aIS);
        if (aDoc == null)
          throw new IllegalArgumentException ("Failed to read source " + aSource + " as XML from InputStream " + aIS);
        return aDoc;
      }

      final Reader aReader = aStreamSource.getReader ();
      if (aReader != null)
      {
        final Document aDoc = XMLReader.readXMLDOM (aReader);
        if (aDoc == null)
          throw new IllegalArgumentException ("Failed to read source " + aSource + " as XML from Reader " + aReader);
        return aDoc;
      }

      final String sSystemID = aStreamSource.getSystemId ();
      if (StringHelper.hasText (sSystemID))
      {
        try
        {
          final URLResource aURL = new URLResource (sSystemID);
          final Document aDoc = XMLReader.readXMLDOM (aURL);
          if (aDoc == null)
            throw new IllegalArgumentException ("Failed to read source " +
                                                aSource +
                                                " as XML from SystemID '" +
                                                sSystemID +
                                                "'");
          return aDoc;
        }
        catch (final MalformedURLException ex)
        {
          throw new IllegalArgumentException ("Failed to read source " +
                                              aSource +
                                              " as XML from SystemID '" +
                                              sSystemID +
                                              "': " +
                                              ex.getMessage ());
        }
      }

      // Neither InputStream nor Reader present
      s_aLogger.error ("StreamSource contains neither InputStream nor Reader nor SystemID - cannot handle!");
      return null;
    }

    final String sMsg = "Can only handle DOMSource and StreamSource - having " +
                        aSource +
                        " with system ID '" +
                        aSource.getSystemId () +
                        "'";
    s_aLogger.error (sMsg);
    throw new IllegalArgumentException (sMsg);
  }
}
