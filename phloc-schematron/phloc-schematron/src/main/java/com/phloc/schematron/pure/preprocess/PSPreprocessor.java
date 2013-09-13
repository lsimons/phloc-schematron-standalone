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
package com.phloc.schematron.pure.preprocess;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.string.ToStringGenerator;
import com.phloc.schematron.pure.binding.IPSQueryBinding;
import com.phloc.schematron.pure.model.IPSElement;
import com.phloc.schematron.pure.model.PSActive;
import com.phloc.schematron.pure.model.PSAssertReport;
import com.phloc.schematron.pure.model.PSDiagnostic;
import com.phloc.schematron.pure.model.PSDiagnostics;
import com.phloc.schematron.pure.model.PSDir;
import com.phloc.schematron.pure.model.PSEmph;
import com.phloc.schematron.pure.model.PSExtends;
import com.phloc.schematron.pure.model.PSLet;
import com.phloc.schematron.pure.model.PSNS;
import com.phloc.schematron.pure.model.PSName;
import com.phloc.schematron.pure.model.PSPattern;
import com.phloc.schematron.pure.model.PSPhase;
import com.phloc.schematron.pure.model.PSRule;
import com.phloc.schematron.pure.model.PSSchema;
import com.phloc.schematron.pure.model.PSSpan;
import com.phloc.schematron.pure.model.PSValueOf;

/**
 * This is the pre-processor class for pure Schematron. It converts an existing
 * schema to the minimal syntax (by default) but allows for a certain degree of
 * customization by keeping certain elements in the resulting schema. The actual
 * query binding is used, so that report test expressions can be converted to
 * assertions, and to replace the content of &lt;param&gt; elements into actual
 * values.
 * 
 * @author Philip Helger
 */
@NotThreadSafe
public class PSPreprocessor
{
  public static final boolean DEFAULT_KEEP_TITLES = false;
  public static final boolean DEFAULT_KEEP_DIAGNOSTICS = false;
  public static final boolean DEFAULT_KEEP_REPORTS = false;
  public static final boolean DEFAULT_KEEP_EMPTY_PATTERNS = true;
  public static final boolean DEFAULT_KEEP_EMPTY_SCHEMA = true;

  private final IPSQueryBinding m_aQueryBinding;
  private boolean m_bKeepTitles = DEFAULT_KEEP_TITLES;
  private boolean m_bKeepDiagnostics = DEFAULT_KEEP_DIAGNOSTICS;
  private boolean m_bKeepReports = DEFAULT_KEEP_REPORTS;
  private boolean m_bKeepEmptyPatterns = DEFAULT_KEEP_EMPTY_PATTERNS;
  private boolean m_bKeepEmptySchema = DEFAULT_KEEP_EMPTY_SCHEMA;

  public PSPreprocessor (@Nonnull final IPSQueryBinding aQueryBinding)
  {
    if (aQueryBinding == null)
      throw new NullPointerException ("QueryBinding");
    m_aQueryBinding = aQueryBinding;
  }

  /**
   * @return The query binding to be used. Never <code>null</code>!
   */
  @Nonnull
  public IPSQueryBinding getQueryBinding ()
  {
    return m_aQueryBinding;
  }

  /**
   * @return <code>true</code> if &lt;title&gt;-elements should be kept. Default
   *         is {@value #DEFAULT_KEEP_TITLES}.
   */
  public boolean isKeepTitles ()
  {
    return m_bKeepTitles;
  }

  @Nonnull
  public PSPreprocessor setKeepTitles (final boolean bKeepTitles)
  {
    m_bKeepTitles = bKeepTitles;
    return this;
  }

  /**
   * @return <code>true</code> if &lt;diagnostics&gt;-elements should be kept.
   *         Default is {@value #DEFAULT_KEEP_DIAGNOSTICS}.
   */
  public boolean isKeepDiagnostics ()
  {
    return m_bKeepDiagnostics;
  }

  @Nonnull
  public PSPreprocessor setKeepDiagnostics (final boolean bKeepDiagnostics)
  {
    m_bKeepDiagnostics = bKeepDiagnostics;
    return this;
  }

  /**
   * @return <code>true</code> if &lt;report&gt;-elements should be kept.
   *         Default is {@value #DEFAULT_KEEP_REPORTS}.
   */
  public boolean isKeepReports ()
  {
    return m_bKeepReports;
  }

  @Nonnull
  public PSPreprocessor setKeepReports (final boolean bKeepReports)
  {
    m_bKeepReports = bKeepReports;
    return this;
  }

  /**
   * @return <code>true</code> if &lt;pattern&gt;-elements without a rule should
   *         be kept. Default is {@value #DEFAULT_KEEP_EMPTY_PATTERNS}.
   */
  public boolean isKeepEmptyPatterns ()
  {
    return m_bKeepEmptyPatterns;
  }

  @Nonnull
  public PSPreprocessor setKeepEmptyPatterns (final boolean bKeepEmptyPatterns)
  {
    m_bKeepEmptyPatterns = bKeepEmptyPatterns;
    return this;
  }

  /**
   * @return <code>true</code> if &lt;schema&gt;-elements without a pattern
   *         should be kept. Default is {@value #DEFAULT_KEEP_EMPTY_SCHEMA}.
   */
  public boolean isKeepEmptySchema ()
  {
    return m_bKeepEmptySchema;
  }

  /**
   * Should schema objects without a pattern be kept? It makes only sense to set
   * it to <code>false</code> if {@link #setKeepEmptyPatterns(boolean)} is also
   * set to false, because otherwise patterns without rules are kept.
   * 
   * @param bKeepEmptySchema
   *        <code>true</code> to keep them, <code>false</code> to discard them.
   * @return this
   */
  @Nonnull
  public PSPreprocessor setKeepEmptySchema (final boolean bKeepEmptySchema)
  {
    m_bKeepEmptySchema = bKeepEmptySchema;
    return this;
  }

  @Nonnull
  private static PSPhase _getPreprocessedPhase (@Nonnull final PSPhase aPhase, @Nonnull final PreprocessorIDPool aIDPool) throws SchematronPreprocessException
  {
    final PSPhase ret = new PSPhase ();
    ret.setID (aIDPool.getUniqueID (aPhase.getID ()));
    ret.setRich (aPhase.getRichClone ());
    if (aPhase.hasAnyInclude ())
      throw new SchematronPreprocessException ("Cannot preprocess <phase> with an <include>");
    for (final IPSElement aElement : aPhase.getAllContentElements ())
    {
      if (aElement instanceof PSActive)
        ret.addActive (((PSActive) aElement).getClone ());
      else
        if (aElement instanceof PSLet)
          ret.addLet (((PSLet) aElement).getClone ());
      // ps are ignored
    }
    ret.addForeignElements (aPhase.getAllForeignElements ());
    ret.addForeignAttributes (aPhase.getAllForeignAttributes ());
    return ret;
  }

  /**
   * Resolve all &lt;extends&gt; elements. This method calls itself recursively
   * until all extends elements are resolved.
   * 
   * @param aRuleContent
   *        A list consisting of {@link PSAssertReport} and {@link PSExtends}
   *        objects. Never <code>null</code>.
   * @param aLookup
   *        The rule lookup object
   * @return List of assert/report elements. Never <code>null</code>.
   * @throws SchematronPreprocessException
   *         If the base rule of an extends object could not be resolved.
   */
  @Nonnull
  @ReturnsMutableCopy
  private static List <PSAssertReport> _getResolvedExtends (@Nonnull final List <IPSElement> aRuleContent,
                                                            @Nonnull final PreprocessorLookup aLookup) throws SchematronPreprocessException
  {
    final List <PSAssertReport> ret = new ArrayList <PSAssertReport> ();
    for (final IPSElement aElement : aRuleContent)
    {
      if (aElement instanceof PSAssertReport)
        ret.add ((PSAssertReport) aElement);
      else
      {
        final PSExtends aExtends = (PSExtends) aElement;
        final String sRuleID = aExtends.getRule ();
        final PSRule aBaseRule = aLookup.getAbstractRuleOfID (sRuleID);
        if (aBaseRule == null)
          throw new SchematronPreprocessException ("Failed to resolve rule ID '" + sRuleID + "'");
        // Recursively resolve the extends of the base rule
        ret.addAll (_getResolvedExtends (aBaseRule.getAllContentElements (), aLookup));
      }
    }
    return ret;
  }

  @Nonnull
  private PSAssertReport _getPreprocessedAssert (@Nonnull final PSAssertReport aAssertReport,
                                                 @Nonnull final PreprocessorIDPool aIDPool,
                                                 @Nullable final Map <String, String> aParamValueMap)
  {
    String sTest = aAssertReport.getTest ();
    if (aAssertReport.isReport () && !m_bKeepReports)
    {
      // Negate the expression!
      sTest = m_aQueryBinding.getNegatedTestExpression (sTest);
    }

    // Keep report or make it always an assert
    final PSAssertReport ret = new PSAssertReport (m_bKeepReports ? aAssertReport.isAssert () : true);
    ret.setTest (m_aQueryBinding.getWithParamTextsReplaced (sTest, aParamValueMap));
    ret.setFlag (aAssertReport.getFlag ());
    ret.setID (aIDPool.getUniqueID (aAssertReport.getID ()));
    if (m_bKeepDiagnostics)
      ret.setDiagnostics (aAssertReport.getAllDiagnostics ());
    ret.setRich (aAssertReport.getRichClone ());
    ret.setLinkable (aAssertReport.getLinkableClone ());
    for (final Object aContent : aAssertReport.getAllContentElements ())
    {
      if (aContent instanceof String)
        ret.addText ((String) aContent);
      else
        if (aContent instanceof PSName)
          ret.addName (((PSName) aContent).getClone ());
        else
          if (aContent instanceof PSValueOf)
          {
            final PSValueOf aValueOf = ((PSValueOf) aContent).getClone ();
            aValueOf.setSelect (m_aQueryBinding.getWithParamTextsReplaced (aValueOf.getSelect (), aParamValueMap));
            ret.addValueOf (aValueOf);
          }
          else
            if (aContent instanceof PSEmph)
              ret.addEmph (((PSEmph) aContent).getClone ());
            else
              if (aContent instanceof PSDir)
                ret.addDir (((PSDir) aContent).getClone ());
              else
                if (aContent instanceof PSSpan)
                  ret.addSpan (((PSSpan) aContent).getClone ());
    }
    ret.addForeignElements (aAssertReport.getAllForeignElements ());
    ret.addForeignAttributes (aAssertReport.getAllForeignAttributes ());
    return ret;
  }

  @Nullable
  private PSRule _getPreprocessedRule (@Nonnull final PSRule aRule,
                                       @Nonnull final PreprocessorLookup aLookup,
                                       @Nonnull final PreprocessorIDPool aIDPool,
                                       @Nullable final Map <String, String> aParamValueMap) throws SchematronPreprocessException
  {
    if (aRule.isAbstract ())
    {
      // Will be inlined
      return null;
    }

    final PSRule ret = new PSRule ();
    ret.setFlag (aRule.getFlag ());
    ret.setRich (aRule.getRichClone ());
    ret.setLinkable (aRule.getLinkableClone ());
    // abstract is always false
    ret.setContext (m_aQueryBinding.getWithParamTextsReplaced (aRule.getContext (), aParamValueMap));
    ret.setID (aIDPool.getUniqueID (aRule.getID ()));
    if (aRule.hasAnyInclude ())
      throw new SchematronPreprocessException ("Cannot preprocess <rule> with an <include>");
    for (final PSLet aLet : aRule.getAllLets ())
      ret.addLet (aLet.getClone ());
    for (final PSAssertReport aAssertReport : _getResolvedExtends (aRule.getAllContentElements (), aLookup))
      ret.addAssertReport (_getPreprocessedAssert (aAssertReport, aIDPool, aParamValueMap));
    ret.addForeignElements (aRule.getAllForeignElements ());
    ret.addForeignAttributes (aRule.getAllForeignAttributes ());
    return ret;
  }

  @Nullable
  private PSPattern _getPreprocessedPattern (@Nonnull final PSPattern aPattern,
                                             @Nonnull final PreprocessorLookup aLookup,
                                             @Nonnull final PreprocessorIDPool aIDPool) throws SchematronPreprocessException
  {
    if (aPattern.isAbstract ())
    {
      // Will be inlined
      return null;
    }

    final PSPattern ret = new PSPattern ();
    // abstract always false
    // is-a must be resolved
    ret.setID (aIDPool.getUniqueID (aPattern.getID ()));
    ret.setRich (aPattern.getRichClone ());
    if (aPattern.hasAnyInclude ())
      throw new SchematronPreprocessException ("Cannot preprocess <pattern> with an <include>");
    if (m_bKeepTitles && aPattern.hasTitle ())
      ret.setTitle (aPattern.getTitle ().getClone ());

    final String sIsA = aPattern.getIsA ();
    if (sIsA != null)
    {
      final PSPattern aBasePattern = aLookup.getAbstractPatternOfID (sIsA);
      if (aBasePattern == null)
        throw new SchematronPreprocessException ("Failed to resolve the pattern denoted by is-a='" + sIsA + "'");

      if (!ret.hasID ())
        ret.setID (aIDPool.getUniqueID (aBasePattern.getID ()));
      if (!ret.hasRich ())
        ret.setRich (aBasePattern.getRichClone ());

      // get the string replacements
      final Map <String, String> aParamValueMap = m_aQueryBinding.getStringReplacementMap (aPattern.getAllParams ());

      for (final IPSElement aElement : aBasePattern.getAllContentElements ())
      {
        if (aElement instanceof PSLet)
          ret.addLet (((PSLet) aElement).getClone ());
        else
          if (aElement instanceof PSRule)
          {
            final PSRule aMinifiedRule = _getPreprocessedRule ((PSRule) aElement, aLookup, aIDPool, aParamValueMap);
            if (aMinifiedRule != null)
              ret.addRule (aMinifiedRule);
          }
        // params must have be resolved
        // ps are ignored
      }
    }
    else
    {
      for (final IPSElement aElement : aPattern.getAllContentElements ())
      {
        if (aElement instanceof PSLet)
          ret.addLet (((PSLet) aElement).getClone ());
        else
          if (aElement instanceof PSRule)
          {
            final PSRule aMinifiedRule = _getPreprocessedRule ((PSRule) aElement, aLookup, aIDPool, null);
            if (aMinifiedRule != null)
              ret.addRule (aMinifiedRule);
          }
        // params must be resolved
        // ps are ignored
      }
    }
    ret.addForeignElements (aPattern.getAllForeignElements ());
    ret.addForeignAttributes (aPattern.getAllForeignAttributes ());
    return ret;
  }

  @Nonnull
  private PSDiagnostics _getPreprocessedDiagnostics (@Nonnull final PSDiagnostics aDiagnostics) throws SchematronPreprocessException
  {
    final PSDiagnostics ret = new PSDiagnostics ();
    if (aDiagnostics.hasAnyInclude ())
      throw new SchematronPreprocessException ("Cannot preprocess <diagnostics> with an <include>");
    for (final PSDiagnostic aDiagnostic : aDiagnostics.getAllDiagnostics ())
      ret.addDiagnostic (aDiagnostic.getClone ());
    ret.addForeignElements (aDiagnostics.getAllForeignElements ());
    ret.addForeignAttributes (aDiagnostics.getAllForeignAttributes ());
    return ret;
  }

  /**
   * Convert the passed schema to a minimal schema.
   * 
   * @param aSchema
   *        The schema to be made minimal. May not be <code>null</code>
   * @return The original schema object, if it is already minimal - a minimal
   *         copy otherwise! May be <code>null</code> if the original schema is
   *         not yet minimal and {@link #isKeepEmptySchema()} is set to
   *         <code>false</code>.
   * @throws SchematronPreprocessException
   *         In case a preprocessing error occurs
   */
  @Nullable
  public PSSchema getAsMinimalSchema (@Nonnull final PSSchema aSchema) throws SchematronPreprocessException
  {
    if (aSchema == null)
      throw new NullPointerException ("Schema");

    // Anything to do?
    if (aSchema.isMinimal ())
      return aSchema;

    return getForcedPreprocessedSchema (aSchema);
  }

  /**
   * Convert the passed schema to a preprocessed schema.
   * 
   * @param aSchema
   *        The schema to pre-process. May not be <code>null</code>
   * @return The original schema object, if it is already preprocessed - a
   *         preprocessed copy otherwise! May be <code>null</code> if the
   *         original schema is not yet preprocessed and
   *         {@link #isKeepEmptySchema()} is set to <code>false</code>.
   * @throws SchematronPreprocessException
   *         In case a preprocessing error occurs
   */
  @Nullable
  public PSSchema getAsPreprocessedSchema (@Nonnull final PSSchema aSchema) throws SchematronPreprocessException
  {
    if (aSchema == null)
      throw new NullPointerException ("Schema");

    // Anything to do?
    if (aSchema.isPreprocessed ())
      return aSchema;

    return getForcedPreprocessedSchema (aSchema);
  }

  /**
   * Convert the passed schema to a preprocessed schema independent if it is
   * already minimal or not.
   * 
   * @param aSchema
   *        The schema to be made minimal. May not be <code>null</code>
   * @return A minimal copy of the schema. May be <code>null</code> if the
   *         original schema is not yet minimal and {@link #isKeepEmptySchema()}
   *         is set to <code>false</code>.
   * @throws SchematronPreprocessException
   *         In case a preprocessing error occurs
   */
  @Nullable
  public PSSchema getForcedPreprocessedSchema (@Nonnull final PSSchema aSchema) throws SchematronPreprocessException
  {
    if (aSchema == null)
      throw new NullPointerException ("Schema");
    final PreprocessorLookup aLookup = new PreprocessorLookup (aSchema);
    final PreprocessorIDPool aIDPool = new PreprocessorIDPool ();

    final PSSchema ret = new PSSchema (aSchema.getResource ());
    ret.setID (aIDPool.getUniqueID (aSchema.getID ()));
    ret.setRich (aSchema.getRichClone ());
    ret.setSchemaVersion (aSchema.getSchemaVersion ());
    ret.setDefaultPhase (aSchema.getDefaultPhase ());
    ret.setQueryBinding (aSchema.getQueryBinding ());
    if (m_bKeepTitles && aSchema.hasTitle ())
      ret.setTitle (aSchema.getTitle ().getClone ());
    if (aSchema.hasAnyInclude ())
      throw new SchematronPreprocessException ("Cannot preprocess <schema> with an <include>");
    for (final PSNS aNS : aSchema.getAllNSs ())
      ret.addNS (aNS.getClone ());
    // start ps are skipped
    for (final PSLet aLet : aSchema.getAllLets ())
      ret.addLet (aLet.getClone ());
    for (final PSPhase aPhase : aSchema.getAllPhases ())
      ret.addPhase (_getPreprocessedPhase (aPhase, aIDPool));
    for (final PSPattern aPattern : aSchema.getAllPatterns ())
    {
      final PSPattern aMinifiedPattern = _getPreprocessedPattern (aPattern, aLookup, aIDPool);
      if (aMinifiedPattern != null)
      {
        // Pattern without rules?
        if (aMinifiedPattern.getRuleCount () > 0 || m_bKeepEmptyPatterns)
          ret.addPattern (aMinifiedPattern);
      }
    }

    // Schema without patterns?
    if (aSchema.getPatternCount () == 0 && !m_bKeepEmptySchema)
      return null;

    // end ps are skipped
    if (m_bKeepDiagnostics && aSchema.hasDiagnostics ())
      ret.setDiagnostics (_getPreprocessedDiagnostics (aSchema.getDiagnostics ()));
    ret.addForeignElements (aSchema.getAllForeignElements ());
    ret.addForeignAttributes (aSchema.getAllForeignAttributes ());
    return ret;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("queryBinding", m_aQueryBinding)
                                       .append ("keepTitles", m_bKeepTitles)
                                       .append ("keepDiagnostics", m_bKeepDiagnostics)
                                       .append ("keepReports", m_bKeepReports)
                                       .append ("keepEmptyPatterns", m_bKeepEmptyPatterns)
                                       .append ("keepEmptySchema", m_bKeepEmptySchema)
                                       .toString ();
  }
}
