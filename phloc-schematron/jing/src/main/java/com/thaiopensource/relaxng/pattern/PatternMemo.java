package com.thaiopensource.relaxng.pattern;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.relaxng.datatype.ValidationContext;

import com.thaiopensource.xml.util.Name;

final class PatternMemo
{
  private final Pattern pattern;
  private final ValidatorPatternBuilder builder;
  private final boolean notAllowed;
  private PatternMemo memoEndAttributes;
  private PatternMemo memoTextOnly;
  private PatternMemo memoEndTagDeriv;
  private PatternMemo memoMixedTextDeriv;
  private PatternMemo memoIgnoreMissingAttributes;
  private Map <Name, PatternMemo> startTagOpenDerivMap;
  private Map <Name, PatternMemo> startTagOpenRecoverDerivMap;
  private Map <Name, PatternMemo> startAttributeDerivMap;
  private DataDerivType memoDataDerivType;
  private PatternMemo memoRecoverAfter;
  private PatternMemo memoEmptyAfter;
  private NormalizedNameClass memoPossibleAttributeNames;
  private NormalizedNameClass memoPossibleStartTagNames;

  PatternMemo (final Pattern pattern, final ValidatorPatternBuilder builder)
  {
    this.pattern = pattern;
    this.builder = builder;
    this.notAllowed = pattern.isNotAllowed ();
  }

  Pattern getPattern ()
  {
    return pattern;
  }

  ValidatorPatternBuilder getPatternBuilder ()
  {
    return builder;
  }

  boolean isNotAllowed ()
  {
    return notAllowed;
  }

  PatternMemo endAttributes ()
  {
    if (memoEndAttributes == null)
      memoEndAttributes = applyForPatternMemo (builder.getEndAttributesFunction ());
    return memoEndAttributes;
  }

  PatternMemo endAttributes (final PatternFunction <Pattern> f)
  {
    if (memoEndAttributes == null)
      memoEndAttributes = applyForPatternMemo (f);
    return memoEndAttributes;
  }

  PatternMemo ignoreMissingAttributes ()
  {
    if (memoIgnoreMissingAttributes == null)
      memoIgnoreMissingAttributes = applyForPatternMemo (builder.getIgnoreMissingAttributesFunction ());
    return memoIgnoreMissingAttributes;
  }

  PatternMemo ignoreMissingAttributes (final PatternFunction <Pattern> f)
  {
    if (memoIgnoreMissingAttributes == null)
      memoIgnoreMissingAttributes = applyForPatternMemo (f);
    return memoIgnoreMissingAttributes;
  }

  PatternMemo textOnly ()
  {
    if (memoTextOnly == null)
      memoTextOnly = applyForPatternMemo (builder.getTextOnlyFunction ());
    return memoTextOnly;
  }

  PatternMemo textOnly (final PatternFunction <Pattern> f)
  {
    if (memoTextOnly == null)
      memoTextOnly = applyForPatternMemo (f);
    return memoTextOnly;
  }

  PatternMemo endTagDeriv ()
  {
    if (memoEndTagDeriv == null)
      memoEndTagDeriv = applyForPatternMemo (builder.getEndTagDerivFunction ());
    return memoEndTagDeriv;
  }

  PatternMemo endTagDeriv (final PatternFunction <Pattern> f)
  {
    if (memoEndTagDeriv == null)
      memoEndTagDeriv = applyForPatternMemo (f);
    return memoEndTagDeriv;
  }

  PatternMemo mixedTextDeriv ()
  {
    if (memoMixedTextDeriv == null)
      memoMixedTextDeriv = applyForPatternMemo (builder.getMixedTextDerivFunction ());
    return memoMixedTextDeriv;
  }

  PatternMemo mixedTextDeriv (final PatternFunction <Pattern> f)
  {
    if (memoMixedTextDeriv == null)
      memoMixedTextDeriv = applyForPatternMemo (f);
    return memoMixedTextDeriv;
  }

  PatternMemo startTagOpenDeriv (final Name name)
  {
    return startTagOpenDeriv (name, null);
  }

  PatternMemo startTagOpenDeriv (final StartTagOpenDerivFunction f)
  {
    return startTagOpenDeriv (f.getName (), f);
  }

  private PatternMemo startTagOpenDeriv (final Name name, StartTagOpenDerivFunction f)
  {
    PatternMemo tem;
    if (startTagOpenDerivMap == null)
      startTagOpenDerivMap = new HashMap <Name, PatternMemo> ();
    else
    {
      tem = startTagOpenDerivMap.get (name);
      if (tem != null)
        return tem;
    }
    if (f == null)
      f = new StartTagOpenDerivFunction (name, builder);
    tem = applyForPatternMemo (f);
    startTagOpenDerivMap.put (name, tem);
    return tem;
  }

  PatternMemo startTagOpenRecoverDeriv (final Name name)
  {
    return startTagOpenRecoverDeriv (name, null);
  }

  PatternMemo startTagOpenRecoverDeriv (final StartTagOpenRecoverDerivFunction f)
  {
    return startTagOpenRecoverDeriv (f.getName (), f);
  }

  private PatternMemo startTagOpenRecoverDeriv (final Name name, StartTagOpenRecoverDerivFunction f)
  {
    PatternMemo tem;
    if (startTagOpenRecoverDerivMap == null)
      startTagOpenRecoverDerivMap = new HashMap <Name, PatternMemo> ();
    else
    {
      tem = startTagOpenRecoverDerivMap.get (name);
      if (tem != null)
        return tem;
    }
    if (f == null)
      f = new StartTagOpenRecoverDerivFunction (name, builder);
    tem = applyForPatternMemo (f);
    startTagOpenRecoverDerivMap.put (name, tem);
    return tem;
  }

  PatternMemo startAttributeDeriv (final Name name)
  {
    return startAttributeDeriv (name, null);
  }

  PatternMemo startAttributeDeriv (final StartAttributeDerivFunction f)
  {
    return startAttributeDeriv (f.getName (), f);
  }

  private PatternMemo startAttributeDeriv (final Name name, StartAttributeDerivFunction f)
  {
    PatternMemo tem;
    if (startAttributeDerivMap == null)
      startAttributeDerivMap = new HashMap <Name, PatternMemo> ();
    else
    {
      tem = startAttributeDerivMap.get (name);
      if (tem != null)
        return tem;
    }
    if (f == null)
      f = new StartAttributeDerivFunction (name, builder);
    tem = applyForPatternMemo (f);
    startAttributeDerivMap.put (name, tem);
    return tem;
  }

  DataDerivType dataDerivType ()
  {
    if (memoDataDerivType == null)
      memoDataDerivType = DataDerivTypeFunction.dataDerivType (builder, pattern).copy ();
    return memoDataDerivType;
  }

  PatternMemo dataDeriv (final String str, final ValidationContext vc)
  {
    return dataDerivType ().dataDeriv (builder, pattern, str, vc, null);
  }

  PatternMemo dataDeriv (final String str, final ValidationContext vc, final List <DataDerivFailure> fail)
  {
    return dataDerivType ().dataDeriv (builder, pattern, str, vc, fail);
  }

  PatternMemo recoverAfter ()
  {
    if (memoRecoverAfter == null)
      memoRecoverAfter = applyForPatternMemo (builder.getRecoverAfterFunction ());
    return memoRecoverAfter;
  }

  PatternMemo emptyAfter ()
  {
    if (memoEmptyAfter == null)
      memoEmptyAfter = applyForPatternMemo (new ApplyAfterFunction (builder)
      {
        @Override
        Pattern apply (final Pattern p)
        {
          return builder.makeEmpty ();
        }

        // allow emptyAfter to be applied to anything
        @Override
        public Pattern caseOther (final Pattern p)
        {
          return p;
        }
      });
    return memoEmptyAfter;
  }

  NormalizedNameClass possibleStartTagNames ()
  {
    if (memoPossibleStartTagNames == null)
      memoPossibleStartTagNames = builder.getPossibleStartTagNamesFunction ().applyTo (pattern);
    return memoPossibleStartTagNames;
  }

  NormalizedNameClass possibleAttributeNames ()
  {
    if (memoPossibleAttributeNames == null)
      memoPossibleAttributeNames = builder.getPossibleAttributeNamesFunction ().applyTo (pattern);
    return memoPossibleAttributeNames;
  }

  private PatternMemo applyForPatternMemo (final PatternFunction <Pattern> f)
  {
    return builder.getPatternMemo (pattern.apply (f));
  }
}
