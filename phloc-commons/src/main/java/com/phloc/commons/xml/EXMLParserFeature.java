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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import com.phloc.commons.annotations.CodingStyleguideUnaware;
import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.lang.EnumHelper;
import com.phloc.commons.name.IHasName;

/**
 * Contains constants for parser features.
 * 
 * @author Philip Helger
 */
public enum EXMLParserFeature implements IHasName
{
  /**
   * When set: Perform namespace processing: prefixes will be stripped off
   * element and attribute names and replaced with the corresponding namespace
   * URIs. By default, the two will simply be concatenated, but the
   * namespace-sep core property allows the application to specify a delimiter
   * string for separating the URI part and the local part
   * (http://xml.org/sax/features/namespaces).
   */
  NAMESPACES (EXMLParserFeatureType.GENERAL, "http://xml.org/sax/features/namespaces"),

  /**
   * When set: The methods of the org.xml.sax.ext.EntityResolver2 interface will
   * be used when an object implementing this interface is registered with the
   * parser using setEntityResolver. (http://xml.org/sax/features/namespaces).
   */
  USE_ENTITY_RESOLVER2 (EXMLParserFeatureType.GENERAL, "http://xml.org/sax/features/use-entity-resolver2"),

  /**
   * When set: Validate the document and report validity errors.
   * (http://xml.org/sax/features/validation).
   */
  VALIDATION (EXMLParserFeatureType.GENERAL, "http://xml.org/sax/features/validation"),

  /**
   * When set: The parser will validate the document only if a grammar is
   * specified. (http://apache.org/xml/features/validation/dynamic).
   */
  DYNAMIC (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/validation/dynamic"),

  /**
   * When set: Turn on XML Schema validation by inserting an XML Schema
   * validator into the pipeline.
   * (http://apache.org/xml/features/validation/schema).
   */
  SCHEMA (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/validation/schema"),

  /**
   * When set: Enable full schema grammar constraint checking, including
   * checking which may be time-consuming or memory intensive. Currently, unique
   * particle attribution constraint checking and particle derivation
   * restriction checking are controlled by this option.
   * (http://apache.org/xml/features/validation/schema-full-checking).
   */
  SCHEMA_FULL_CHECKING (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/validation/schema-full-checking"),

  /**
   * When set: Expose via SAX and DOM XML Schema normalized values for
   * attributes and elements.
   * (http://apache.org/xml/features/validation/schema/normalized-value).
   */
  NORMALIZED_VALUE (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/validation/schema/normalized-value"),

  /**
   * When set: Send XML Schema element default values via characters().
   * (http://apache.org/xml/features/validation/schema/element-default).
   */
  ELEMENT_DEFAULT (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/validation/schema/element-default"),

  /**
   * When set: Augment Post-Schema-Validation-Infoset.
   * (http://apache.org/xml/features/validation/schema/augment-psvi).
   */
  AUGMENT_PSVI (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/validation/schema/augment-psvi"),

  /**
   * When set: xsi:type attributes will be ignored until a global element
   * declaration has been found, at which point xsi:type attributes will be
   * processed on the element for which the global element declaration was found
   * as well as its descendants.
   * (http://apache.org/xml/features/validation/schema
   * /ignore-xsi-type-until-elemdecl).
   */
  IGNORE_XSI_TYPE_UNTIL_ELEMDECL (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/validation/schema/ignore-xsi-type-until-elemdecl"),

  /**
   * When set: Enable generation of synthetic annotations. A synthetic
   * annotation will be generated when a schema component has non-schema
   * attributes but no child annotation.
   * (http://apache.org/xml/features/generate-synthetic-annotations).
   */
  GENERATE_SYNTHETIC_ANNOTATIONS (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/generate-synthetic-annotations"),

  /**
   * When set: Schema annotations will be laxly validated against available
   * schema components. (http://apache.org/xml/features/validate-annotations).
   */
  VALIDATE_ANNOTATIONS (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/validate-annotations"),

  /**
   * When set: All schema location hints will be used to locate the components
   * for a given target namespace.
   * (http://apache.org/xml/features/honour-all-schemaLocations).
   */
  HONOUR_ALL_SCHEMA_LOCATIONS (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/honour-all-schemaLocations"),

  /**
   * When set: Include external general entities.
   * (http://xml.org/sax/features/external-general-entities).
   */
  EXTERNAL_GENERAL_ENTITIES (EXMLParserFeatureType.GENERAL, "http://xml.org/sax/features/external-general-entities"),

  /**
   * When set: Include external parameter entities and the external DTD subset.
   * (http://xml.org/sax/features/external-parameter-entities).
   */
  EXTERNAL_PARAMETER_ENTITIES (EXMLParserFeatureType.GENERAL, "http://xml.org/sax/features/external-parameter-entities"),

  /**
   * When set: Construct an optimal representation for DTD content models to
   * significantly reduce the likelihood a StackOverflowError will occur when
   * large content models are processed.
   * (http://apache.org/xml/features/validation/balance-syntax-trees).
   */
  BALANCE_SYNTAX_TREES (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/validation/balance-syntax-trees"),

  /**
   * When set: Enable checking of ID/IDREF constraints.
   * (http://apache.org/xml/features/validation/id-idref-checking).
   */
  ID_IDREF_CHECKING (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/validation/id-idref-checking"),

  /**
   * When set: Enable identity constraint checking.
   * (http://apache.org/xml/features/validation/identity-constraint-checking).
   */
  IDENTITY_CONSTRAINT_CHECKING (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/validation/identity-constraint-checking"),

  /**
   * When set: Check that each value of type ENTITY matches the name of an
   * unparsed entity declared in the DTD.
   * (http://apache.org/xml/features/validation/unparsed-entity-checking).
   */
  UNPARSED_ENTITY_CHECKING (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/validation/unparsed-entity-checking"),

  /**
   * When set: Report a warning when a duplicate attribute is re-declared.
   * (http://apache.org/xml/features/validation/warn-on-duplicate-attdef).
   */
  WARN_ON_DUPLICATE_ATTDEF (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/validation/warn-on-duplicate-attdef"),

  /**
   * When set: Report a warning if an element referenced in a content model is
   * not declared.
   * (http://apache.org/xml/features/validation/warn-on-undeclared-elemdef).
   */
  WARN_ON_UNDECLARED_ELEMDEF (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/validation/warn-on-undeclared-elemdef"),

  /**
   * When set: Report a warning for duplicate entity declaration.
   * (http://apache.org/xml/features/warn-on-duplicate-entitydef).
   */
  WARN_ON_DUPLICATE_ENTITYDEF (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/warn-on-duplicate-entitydef"),

  /**
   * When set: Enable XInclude processing.
   * (http://apache.org/xml/features/xinclude).
   */
  XINCLUDE (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/xinclude"),

  /**
   * When set: Perform base URI fixup as specified by the XInclude
   * Recommendation. (http://apache.org/xml/features/xinclude/fixup-base-uris).
   */
  XINCLUDE_FIXUP_BASE_URIS (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/xinclude/fixup-base-uris"),

  /**
   * When set: Perform language fixup as specified by the XInclude
   * Recommendation. (http://apache.org/xml/features/xinclude/fixup-language).
   */
  XINCLUDE_FIXUP_LANGUAGE (EXMLParserFeatureType.GENERAL, "http://apache.org/xml/features/xinclude/fixup-language"),

  /**
   * Namespace prefixes feature id
   * (http://xml.org/sax/features/namespace-prefixes).
   */
  SAX_NAMESPACE_PREFIXES (EXMLParserFeatureType.SAX, "http://xml.org/sax/features/namespace-prefixes"),

  /**
   * When set: The document specified standalone="yes" in its XML declaration.<br>
   * Read-only! (http://xml.org/sax/features/is-standalone)
   */
  SAX_FEATURE_IS_STANDALONE (EXMLParserFeatureType.SAX, "http://xml.org/sax/features/is-standalone"),

  /**
   * When set: The Attributes objects passed by the parser in
   * org.xml.sax.ContentHandler.startElement() implement the
   * org.xml.sax.ext.Attributes2 interface.
   * (http://xml.org/sax/features/use-attributes2).
   */
  SAX_USE_ATTRIBUTES2 (EXMLParserFeatureType.SAX, "http://xml.org/sax/features/use-attributes2"),

  /**
   * When set: The Locator objects passed by the parser in
   * org.xml.sax.ContentHandler.setDocumentLocator() implement the
   * org.xml.sax.ext.Locator2 interface.
   * (http://xml.org/sax/features/use-locator2).
   */
  SAX_USE_LOCATOR2 (EXMLParserFeatureType.SAX, "http://xml.org/sax/features/use-locator2"),

  /**
   * When set: The parser supports both XML 1.0 and XML 1.1.<br>
   * Read-only! (http://xml.org/sax/features/xml-1.1)
   */
  SAX_IS_XML11_PARSER (EXMLParserFeatureType.SAX, "http://xml.org/sax/features/xml-1.1");

  private static final Logger s_aLogger = LoggerFactory.getLogger (EXMLParserFeature.class);

  private final EXMLParserFeatureType m_eType;
  private final String m_sName;
  @CodingStyleguideUnaware
  private boolean m_bWarnedOnce = false;

  private EXMLParserFeature (@Nonnull final EXMLParserFeatureType eType, @Nonnull @Nonempty final String sName)
  {
    m_eType = eType;
    m_sName = sName;
  }

  @Nonnull
  public EXMLParserFeatureType getFeatureType ()
  {
    return m_eType;
  }

  @Nonnull
  @Nonempty
  public String getName ()
  {
    return m_sName;
  }

  public void applyTo (@Nonnull final org.xml.sax.XMLReader aParser, final boolean bValue)
  {
    if (m_eType != EXMLParserFeatureType.GENERAL && m_eType != EXMLParserFeatureType.SAX)
      s_aLogger.warn ("Parser feature type of '" + name () + "' is not applicable for SAX parsers!");

    try
    {
      aParser.setFeature (m_sName, bValue);
    }
    catch (final SAXNotRecognizedException ex)
    {
      if (!m_bWarnedOnce)
      {
        s_aLogger.warn ("XML Parser does not recognize feature '" + name () + "'");
        m_bWarnedOnce = true;
      }
    }
    catch (final SAXNotSupportedException ex)
    {
      s_aLogger.warn ("XML Parser does not support feature '" + name () + "'");
    }
  }

  @Nullable
  public static EXMLParserFeature getFromNameOrNull (@Nullable final String sName)
  {
    return EnumHelper.getFromNameOrNull (EXMLParserFeature.class, sName);
  }
}
