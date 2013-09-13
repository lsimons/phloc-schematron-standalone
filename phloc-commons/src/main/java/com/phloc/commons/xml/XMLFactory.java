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
package com.phloc.commons.xml;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

import com.phloc.commons.SystemProperties;
import com.phloc.commons.annotations.PresentForCodeCoverage;
import com.phloc.commons.exceptions.InitializationException;
import com.phloc.commons.xml.sax.LoggingSAXErrorHandler;

/**
 * Utility class for creating XML DOM documents.
 *
 * @author Philip Helger
 */
public final class XMLFactory
{
  /** The DOM DocumentBuilderFactory. */
  private static final DocumentBuilderFactory s_aDefaultDocBuilderFactory;

  /** The DOM DocumentBuilder. */
  private static final DocumentBuilder s_aDefaultDocBuilder;

  /** The SAX parser factory. */
  private static final SAXParserFactory s_aSaxFactoryNonValidating;
  private static final SAXParserFactory s_aSaxFactoryValidating;

  static
  {
    // Explicitly use Apache Xerces for XSD reading? No in case Xerces is not in
    // the classpath.
    if (false)
      SystemProperties.setPropertyValue ("javax.xml.validation.SchemaFactory:http://www.w3.org/2001/XMLSchema",
                                         "org.apache.xerces.jaxp.validation.XMLSchemaFactory");

    // create DOM document builder
    s_aDefaultDocBuilderFactory = createDefaultDocumentBuilderFactory ();
    s_aDefaultDocBuilder = createDocumentBuilder (s_aDefaultDocBuilderFactory);

    // init SAX factory
    try
    {
      // Not validating
      s_aSaxFactoryNonValidating = SAXParserFactory.newInstance ();
      s_aSaxFactoryNonValidating.setNamespaceAware (true);

      // Validating
      s_aSaxFactoryValidating = SAXParserFactory.newInstance ();
      s_aSaxFactoryValidating.setNamespaceAware (true);
      s_aSaxFactoryValidating.setValidating (true);
    }
    catch (final FactoryConfigurationError ex)
    {
      throw new InitializationException ("Failed to create SAX parser factory", ex);
    }
  }

  @PresentForCodeCoverage
  @SuppressWarnings ("unused")
  private static final XMLFactory s_aInstance = new XMLFactory ();

  private XMLFactory ()
  {}

  /**
   * Create a new {@link DocumentBuilderFactory} with the following settings:
   * coalescing, comment ignoring and namespace aware.
   *
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static DocumentBuilderFactory createDefaultDocumentBuilderFactory ()
  {
    final DocumentBuilderFactory aDocumentBuilderFactory = DocumentBuilderFactory.newInstance ();
    // convert CDATA to text node?
    aDocumentBuilderFactory.setCoalescing (true);
    aDocumentBuilderFactory.setIgnoringComments (true);
    aDocumentBuilderFactory.setNamespaceAware (true);
    return aDocumentBuilderFactory;
  }

  /**
   * Create a new {@link DocumentBuilderFactory} for the specified schema, with
   * the following settings: coalescing, comment ignoring and namespace aware.
   *
   * @param aSchema
   *        The schema to use. May not be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static DocumentBuilderFactory createDocumentBuilderFactory (@Nonnull final Schema aSchema)
  {
    if (aSchema == null)
      throw new NullPointerException ("schema");

    final DocumentBuilderFactory aDocumentBuilderFactory = createDefaultDocumentBuilderFactory ();
    aDocumentBuilderFactory.setSchema (aSchema);
    return aDocumentBuilderFactory;
  }

  /**
   * @return The default document builder factory that is not schema specific.
   *         Never <code>null</code>.
   */
  @Nonnull
  public static DocumentBuilderFactory getDocumentBuilderFactory ()
  {
    return s_aDefaultDocBuilderFactory;
  }

  /**
   * @return The default document builder that is not schema specific. Never
   *         <code>null</code>.
   */
  @Nonnull
  public static DocumentBuilder getDocumentBuilder ()
  {
    return s_aDefaultDocBuilder;
  }

  /**
   * @return The DOM implementation of the default document builder. Never
   *         <code>null</code>.
   */
  @Nonnull
  public static DOMImplementation getDOMImplementation ()
  {
    return s_aDefaultDocBuilder.getDOMImplementation ();
  }

  /**
   * Create a document builder without a certain schema, using the default
   * {@link DocumentBuilderFactory}.
   *
   * @return The created document builder. Never <code>null</code>.
   */
  @Nonnull
  public static DocumentBuilder createDocumentBuilder ()
  {
    return createDocumentBuilder (s_aDefaultDocBuilderFactory);
  }

  /**
   * Create a document builder for a certain schema.
   *
   * @param aSchema
   *        The schema to use. May not be <code>null</code>.
   * @return The created document builder. Never <code>null</code>.
   */
  @Nonnull
  public static DocumentBuilder createDocumentBuilder (@Nonnull final Schema aSchema)
  {
    return createDocumentBuilder (createDocumentBuilderFactory (aSchema));
  }

  /**
   * Create a document builder without a certain schema, using the passed
   * {@link DocumentBuilderFactory}.
   *
   * @param aDocBuilderFactory
   *        The document builder factory to be used. May not be
   *        <code>null</code>.
   * @return The created document builder. Never <code>null</code>.
   * @throws InitializationException
   *         In case some DOM initialization goes wrong
   */
  @Nonnull
  public static DocumentBuilder createDocumentBuilder (@Nonnull final DocumentBuilderFactory aDocBuilderFactory)
  {
    if (aDocBuilderFactory == null)
      throw new NullPointerException ("docBuilderFactory");

    try
    {
      final DocumentBuilder aDocBuilder = aDocBuilderFactory.newDocumentBuilder ();
      aDocBuilder.setErrorHandler (LoggingSAXErrorHandler.getInstance ());
      return aDocBuilder;
    }
    catch (final ParserConfigurationException ex)
    {
      throw new InitializationException ("Failed to create document builder", ex);
    }
  }

  /**
   * Get the default SAX parser factory.
   *
   * @param bValidating
   *        if <code>true</code> the validating factory is returned, else the
   *        non-validating factory is returned.
   * @return The matching SAX parser factory. Never <code>null</code>.
   */
  @Nonnull
  public static SAXParserFactory getSaxParserFactory (final boolean bValidating)
  {
    return bValidating ? s_aSaxFactoryValidating : s_aSaxFactoryNonValidating;
  }

  /**
   * Create a new SAX parser.
   *
   * @param bValidating
   *        if <code>true</code> a validating parser is returned, else the
   *        non-validating parser is returned.
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static SAXParser createSaxParser (final boolean bValidating)
  {
    try
    {
      return getSaxParserFactory (bValidating).newSAXParser ();
    }
    catch (final Throwable t)
    {
      throw new IllegalStateException ("Failed to create new SAX parser", t);
    }
  }

  /**
   * Create a new XML document without document type using version
   * {@link EXMLVersion#DEFAULT}. The default document builder is used.
   *
   * @return The created document. Never <code>null</code>.
   */
  @Nonnull
  public static Document newDocument ()
  {
    return newDocument (s_aDefaultDocBuilder, null);
  }

  /**
   * Create a new XML document without document type using version
   * {@link EXMLVersion#DEFAULT}. A custom document builder is used.
   *
   * @param aDocBuilder
   *        The document builder to use. May not be <code>null</code>.
   * @return The created document. Never <code>null</code>.
   */
  @Nonnull
  public static Document newDocument (@Nonnull final DocumentBuilder aDocBuilder)
  {
    return newDocument (aDocBuilder, null);
  }

  /**
   * Create a new XML document without document type using the default document
   * builder.
   *
   * @param eVersion
   *        The XML version to use. If <code>null</code> is passed,
   *        {@link EXMLVersion#DEFAULT} will be used.
   * @return The created document. Never <code>null</code>.
   */
  @Nonnull
  public static Document newDocument (@Nullable final EXMLVersion eVersion)
  {
    return newDocument (s_aDefaultDocBuilder, eVersion);
  }

  /**
   * Create a new XML document without document type using a custom document
   * builder.
   *
   * @param aDocBuilder
   *        The document builder to use. May not be <code>null</code>.
   * @param eVersion
   *        The XML version to use. If <code>null</code> is passed,
   *        {@link EXMLVersion#DEFAULT} will be used.
   * @return The created document. Never <code>null</code>.
   */
  @Nonnull
  public static Document newDocument (@Nonnull final DocumentBuilder aDocBuilder, @Nullable final EXMLVersion eVersion)
  {
    if (aDocBuilder == null)
      throw new NullPointerException ("docBuilder");

    final Document doc = aDocBuilder.newDocument ();
    doc.setXmlVersion ((eVersion != null ? eVersion : EXMLVersion.DEFAULT).getVersion ());
    return doc;
  }

  /**
   * Create a new document with a document type using version
   * {@link EXMLVersion#DEFAULT}.
   *
   * @param sQualifiedName
   *        The qualified name to use.
   * @param sPublicId
   *        The public ID of the document type.
   * @param sSystemId
   *        The system ID of the document type.
   * @return The created document. Never <code>null</code>.
   */
  @Nonnull
  public static Document newDocument (@Nonnull final String sQualifiedName,
                                      @Nullable final String sPublicId,
                                      @Nullable final String sSystemId)
  {
    return newDocument (null, sQualifiedName, sPublicId, sSystemId);
  }

  /**
   * Create a new document with a document type using the default document
   * builder.
   *
   * @param eVersion
   *        The XML version to use. If <code>null</code> is passed,
   *        {@link EXMLVersion#DEFAULT} will be used.
   * @param sQualifiedName
   *        The qualified name to use.
   * @param sPublicId
   *        The public ID of the document type.
   * @param sSystemId
   *        The system ID of the document type.
   * @return The created document. Never <code>null</code>.
   */
  @Nonnull
  public static Document newDocument (@Nullable final EXMLVersion eVersion,
                                      @Nonnull final String sQualifiedName,
                                      @Nullable final String sPublicId,
                                      @Nullable final String sSystemId)
  {
    return newDocument (s_aDefaultDocBuilder, eVersion, sQualifiedName, sPublicId, sSystemId);
  }

  /**
   * Create a new document with a document type using a custom document builder.
   *
   * @param aDocBuilder
   *        the document builder to be used. May not be <code>null</code>.
   * @param eVersion
   *        The XML version to use. If <code>null</code> is passed,
   *        {@link EXMLVersion#DEFAULT} will be used.
   * @param sQualifiedName
   *        The qualified name to use.
   * @param sPublicId
   *        The public ID of the document type.
   * @param sSystemId
   *        The system ID of the document type.
   * @return The created document. Never <code>null</code>.
   */
  @Nonnull
  public static Document newDocument (@Nonnull final DocumentBuilder aDocBuilder,
                                      @Nullable final EXMLVersion eVersion,
                                      @Nonnull final String sQualifiedName,
                                      @Nullable final String sPublicId,
                                      @Nullable final String sSystemId)
  {
    if (aDocBuilder == null)
      throw new NullPointerException ("docBuilder");

    final DOMImplementation aDomImpl = aDocBuilder.getDOMImplementation ();
    final DocumentType aDocType = aDomImpl.createDocumentType (sQualifiedName, sPublicId, sSystemId);

    final Document doc = aDomImpl.createDocument (sSystemId, sQualifiedName, aDocType);
    doc.setXmlVersion ((eVersion != null ? eVersion : EXMLVersion.DEFAULT).getVersion ());
    return doc;
  }
}
