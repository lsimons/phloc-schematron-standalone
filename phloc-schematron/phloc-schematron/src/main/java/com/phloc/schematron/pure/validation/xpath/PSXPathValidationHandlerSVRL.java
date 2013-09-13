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
package com.phloc.schematron.pure.validation.xpath;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.oclc.purl.dsdl.svrl.ActivePattern;
import org.oclc.purl.dsdl.svrl.DiagnosticReference;
import org.oclc.purl.dsdl.svrl.FailedAssert;
import org.oclc.purl.dsdl.svrl.FiredRule;
import org.oclc.purl.dsdl.svrl.NsPrefixInAttributeValues;
import org.oclc.purl.dsdl.svrl.SchematronOutputType;
import org.oclc.purl.dsdl.svrl.SuccessfulReport;
import org.w3c.dom.Node;

import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.state.EContinue;
import com.phloc.schematron.pure.bound.xpath.PSXPathBoundAssertReport;
import com.phloc.schematron.pure.bound.xpath.PSXPathBoundDiagnostic;
import com.phloc.schematron.pure.bound.xpath.PSXPathBoundElement;
import com.phloc.schematron.pure.errorhandler.IPSErrorHandler;
import com.phloc.schematron.pure.model.IPSElement;
import com.phloc.schematron.pure.model.PSAssertReport;
import com.phloc.schematron.pure.model.PSDiagnostics;
import com.phloc.schematron.pure.model.PSDir;
import com.phloc.schematron.pure.model.PSEmph;
import com.phloc.schematron.pure.model.PSName;
import com.phloc.schematron.pure.model.PSPattern;
import com.phloc.schematron.pure.model.PSPhase;
import com.phloc.schematron.pure.model.PSRule;
import com.phloc.schematron.pure.model.PSSchema;
import com.phloc.schematron.pure.model.PSSpan;
import com.phloc.schematron.pure.model.PSTitle;
import com.phloc.schematron.pure.model.PSValueOf;
import com.phloc.schematron.pure.validation.PSValidationHandlerDefault;
import com.phloc.schematron.pure.validation.SchematronValidationException;

/**
 * A special validation handler that creates an SVRL document. This class only
 * works for the XPath binding, as the special {@link PSXPathBoundAssertReport}
 * class is referenced!
 * 
 * @author Philip Helger
 */
@NotThreadSafe
public class PSXPathValidationHandlerSVRL extends PSValidationHandlerDefault
{
  private final IPSErrorHandler m_aErrorHandler;
  private SchematronOutputType m_aSchematronOutput;
  private PSSchema m_aSchema;
  private String m_sLastRuleContext;

  /**
   * Constructor
   * 
   * @param aErrorHandler
   *        The error handler to be used. May not be <code>null</code>.
   */
  public PSXPathValidationHandlerSVRL (@Nonnull final IPSErrorHandler aErrorHandler)
  {
    if (aErrorHandler == null)
      throw new NullPointerException ("ErrorHandler");
    m_aErrorHandler = aErrorHandler;
  }

  private void _warn (@Nonnull final IPSElement aSourceElement, @Nonnull final String sMsg)
  {
    if (m_aSchema == null)
      throw new IllegalStateException ("No schema is present!");

    m_aErrorHandler.warn (m_aSchema.getResource (), aSourceElement, sMsg);
  }

  private void _error (@Nonnull final IPSElement aSourceElement, @Nonnull final String sMsg, @Nullable final Throwable t)
  {
    if (m_aSchema == null)
      throw new IllegalStateException ("No schema is present!");

    m_aErrorHandler.error (m_aSchema.getResource (), aSourceElement, sMsg, t);
  }

  @Nullable
  private static String _getTitleAsString (@Nullable final PSTitle aTitle) throws SchematronValidationException
  {
    if (aTitle == null)
      return null;

    final StringBuilder aSB = new StringBuilder ();
    for (final Object aContent : aTitle.getAllContentElements ())
    {
      if (aContent instanceof String)
        aSB.append ((String) aContent);
      else
        if (aContent instanceof PSDir)
          aSB.append (((PSDir) aContent).getAsText ());
        else
          throw new SchematronValidationException ("Unsupported title content element: " + aContent);
    }
    return aSB.toString ();
  }

  @Override
  public void onStart (@Nonnull final PSSchema aSchema, @Nullable final PSPhase aActivePhase) throws SchematronValidationException
  {
    final SchematronOutputType aSchematronOutput = new SchematronOutputType ();
    if (aActivePhase != null)
      aSchematronOutput.setPhase (aActivePhase.getID ());
    aSchematronOutput.setSchemaVersion (aSchema.getSchemaVersion ());
    aSchematronOutput.setTitle (_getTitleAsString (aSchema.getTitle ()));

    // Add namespace prefixes
    for (final Map.Entry <String, String> aEntry : aSchema.getAsNamespaceContext ()
                                                          .getPrefixToNamespaceURIMap ()
                                                          .entrySet ())
    {
      final NsPrefixInAttributeValues aNsPrefix = new NsPrefixInAttributeValues ();
      aNsPrefix.setPrefix (aEntry.getKey ());
      aNsPrefix.setUri (aEntry.getValue ());
      aSchematronOutput.getNsPrefixInAttributeValues ().add (aNsPrefix);
    }
    m_aSchematronOutput = aSchematronOutput;
    m_aSchema = aSchema;
  }

  @Override
  public void onPattern (@Nonnull final PSPattern aPattern)
  {
    final ActivePattern aRetPattern = new ActivePattern ();
    // TODO document
    aRetPattern.setId (aPattern.getID ());
    // TODO name
    // TODO role
    m_aSchematronOutput.getActivePatternAndFiredRuleAndFailedAssert ().add (aRetPattern);
  }

  @Override
  public void onRule (@Nonnull final PSRule aRule, @Nonnull final String sContext)
  {
    final FiredRule aRetRule = new FiredRule ();
    aRetRule.setContext (sContext);
    aRetRule.setFlag (aRule.getFlag ());
    aRetRule.setId (aRule.getID ());
    // TODO role
    m_aSchematronOutput.getActivePatternAndFiredRuleAndFailedAssert ().add (aRetRule);
    m_sLastRuleContext = sContext;
  }

  /**
   * Get the error text from an assert or report element.
   * 
   * @param aBoundContentElements
   *        The list of bound elements to be evaluated.
   * @param aSourceNode
   *        The XML node of the document currently validated.
   * @return A non-<code>null</code> String
   * @throws SchematronValidationException
   *         In case evaluating an XPath expression fails.
   */
  @Nonnull
  private String _getErrorText (@Nonnull final List <PSXPathBoundElement> aBoundContentElements,
                                @Nonnull final Node aSourceNode) throws SchematronValidationException
  {
    final StringBuilder aSB = new StringBuilder ();
    for (final PSXPathBoundElement aBoundElement : aBoundContentElements)
    {
      final Object aContent = aBoundElement.getElement ();
      if (aContent instanceof String)
        aSB.append ((String) aContent);
      else
        if (aContent instanceof PSName)
        {
          final PSName aName = (PSName) aContent;
          if (aName.hasPath ())
          {
            // XPath present
            try
            {
              aSB.append ((String) aBoundElement.getBoundExpression ().evaluate (aSourceNode, XPathConstants.STRING));
            }
            catch (final XPathExpressionException ex)
            {
              _error (aName, "Failed to evaluate XPath expression to a string: '" +
                             aBoundElement.getExpression () +
                             "'", ex);
              // Append the path so that something is present in the output
              aSB.append (aName.getPath ());
            }
          }
          else
          {
            // No XPath present
            aSB.append (aSourceNode.getNodeName ());
          }
        }
        else
          if (aContent instanceof PSValueOf)
          {
            final PSValueOf aValueOf = (PSValueOf) aContent;
            try
            {
              aSB.append ((String) aBoundElement.getBoundExpression ().evaluate (aSourceNode, XPathConstants.STRING));
            }
            catch (final XPathExpressionException ex)
            {
              _error (aValueOf, "Failed to evaluate XPath expression to a string: '" +
                                aBoundElement.getExpression () +
                                "'", ex);
              // Append the path so that something is present in the output
              aSB.append (aValueOf.getSelect ());
            }
          }
          else
            if (aContent instanceof PSEmph)
              aSB.append (((PSEmph) aContent).getAsText ());
            else
              if (aContent instanceof PSDir)
                aSB.append (((PSDir) aContent).getAsText ());
              else
                if (aContent instanceof PSSpan)
                  aSB.append (((PSSpan) aContent).getAsText ());
                else
                  throw new SchematronValidationException ("Unsupported assert/report content element: " + aContent);
    }
    return aSB.toString ();
  }

  /**
   * Handle the diagnostic references of a single assert/report element
   * 
   * @param aSrcDiagnostics
   *        The list of diagnostic reference IDs in the source assert/report
   *        element. May be <code>null</code> if no diagnostic references are
   *        present
   * @param aDstList
   *        The diagnostic reference list of the SchematronOutput to be filled.
   *        May not be <code>null</code>.
   * @param aBoundAssertReport
   *        The bound assert report element. Never <code>null</code>.
   * @param aRuleMatchingNode
   *        The XML node of the XML document currently validated. Never
   *        <code>null</code>.
   * @throws SchematronValidationException
   */
  private void _handleDiagnosticReferences (@Nullable final List <String> aSrcDiagnostics,
                                            @Nonnull final List <DiagnosticReference> aDstList,
                                            @Nonnull final PSXPathBoundAssertReport aBoundAssertReport,
                                            @Nonnull final Node aRuleMatchingNode) throws SchematronValidationException
  {
    if (ContainerHelper.isNotEmpty (aSrcDiagnostics))
    {
      if (m_aSchema.hasDiagnostics ())
      {
        final PSDiagnostics aDiagnostics = m_aSchema.getDiagnostics ();
        for (final String sDiagnosticID : aSrcDiagnostics)
        {
          final PSXPathBoundDiagnostic aDiagnostic = aBoundAssertReport.getBoundDiagnosticOfID (sDiagnosticID);
          if (aDiagnostic == null)
            _warn (aDiagnostics, "Failed to resolve diagnostics with ID '" + sDiagnosticID + "'");
          else
          {
            // Create the SVRL diagnostic-reference element
            final DiagnosticReference aDR = new DiagnosticReference ();
            aDR.setDiagnostic (sDiagnosticID);
            aDR.setText (_getErrorText (aDiagnostic.getAllBoundContentElements (), aRuleMatchingNode));
            aDstList.add (aDR);
          }
        }
      }
      else
        _warn (m_aSchema, "Failed to resolve diagnostic because schema has no diagnostics");
    }
  }

  @Override
  @Nonnull
  public EContinue onFailedAssert (@Nonnull final PSAssertReport aAssertReport,
                                   @Nonnull final String sTestExpression,
                                   @Nonnull final Node aRuleMatchingNode,
                                   @Nonnull final int nNodeIndex,
                                   @Nullable final Object aContext) throws SchematronValidationException
  {
    if (!(aContext instanceof PSXPathBoundAssertReport))
      throw new SchematronValidationException ("The passed context must be an XPath object but is a " + aContext);
    final PSXPathBoundAssertReport aBoundAssertReport = (PSXPathBoundAssertReport) aContext;

    final FailedAssert aFailedAssert = new FailedAssert ();
    aFailedAssert.setFlag (aAssertReport.getFlag ());
    aFailedAssert.setId (aAssertReport.getID ());
    aFailedAssert.setLocation (m_sLastRuleContext + "[" + nNodeIndex + "]");
    // TODO role
    aFailedAssert.setTest (sTestExpression);
    aFailedAssert.setText (_getErrorText (aBoundAssertReport.getAllBoundContentElements (), aRuleMatchingNode));
    _handleDiagnosticReferences (aAssertReport.getAllDiagnostics (),
                                 aFailedAssert.getDiagnosticReference (),
                                 aBoundAssertReport,
                                 aRuleMatchingNode);
    m_aSchematronOutput.getActivePatternAndFiredRuleAndFailedAssert ().add (aFailedAssert);
    return EContinue.CONTINUE;
  }

  @Override
  @Nonnull
  public EContinue onSuccessfulReport (@Nonnull final PSAssertReport aAssertReport,
                                       @Nonnull final String sTestExpression,
                                       @Nonnull final Node aRuleMatchingNode,
                                       @Nonnull final int nNodeIndex,
                                       @Nullable final Object aContext) throws SchematronValidationException
  {
    if (!(aContext instanceof PSXPathBoundAssertReport))
      throw new SchematronValidationException ("The passed context must be an XPath object but is a " + aContext);
    final PSXPathBoundAssertReport aBoundAssertReport = (PSXPathBoundAssertReport) aContext;

    final SuccessfulReport aSuccessfulReport = new SuccessfulReport ();
    aSuccessfulReport.setFlag (aAssertReport.getFlag ());
    aSuccessfulReport.setId (aAssertReport.getID ());
    aSuccessfulReport.setLocation (m_sLastRuleContext + "[" + nNodeIndex + "]");
    // TODO role
    aSuccessfulReport.setTest (sTestExpression);
    aSuccessfulReport.setText (_getErrorText (aBoundAssertReport.getAllBoundContentElements (), aRuleMatchingNode));
    _handleDiagnosticReferences (aAssertReport.getAllDiagnostics (),
                                 aSuccessfulReport.getDiagnosticReference (),
                                 aBoundAssertReport,
                                 aRuleMatchingNode);
    m_aSchematronOutput.getActivePatternAndFiredRuleAndFailedAssert ().add (aSuccessfulReport);
    return EContinue.CONTINUE;
  }

  @Nullable
  public SchematronOutputType getSVRL ()
  {
    return m_aSchematronOutput;
  }
}
