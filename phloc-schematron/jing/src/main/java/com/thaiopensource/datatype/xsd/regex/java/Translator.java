package com.thaiopensource.datatype.xsd.regex.java;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.thaiopensource.datatype.xsd.regex.RegexSyntaxException;
import com.thaiopensource.util.Localizer;
import com.thaiopensource.util.Utf16;

/**
 * Translates XML Schema regexes into <code>java.util.regex</code> regexes.
 * 
 * @see java.util.regex.Pattern
 * @see <a href="http://www.w3.org/TR/xmlschema-2/#regexs">XML Schema Part 2</a>
 */
public class Translator
{
  private final String regExp;
  private int pos = 0;
  private final int length;
  private char curChar;
  private boolean eos = false;
  private final StringBuffer result = new StringBuffer ();
  static private final boolean surrogatesDirect = RegexFeatures.SURROGATES_DIRECT;

  static private final String categories = "LMNPZSC";
  static private final CharClass [] categoryCharClasses = new CharClass [categories.length ()];
  static private final String subCategories = "LuLlLtLmLoMnMcMeNdNlNoPcPdPsPePiPfPoZsZlZpSmScSkSoCcCfCoCn";
  static private final CharClass [] subCategoryCharClasses = new CharClass [subCategories.length () / 2];

  static private final int NONBMP_MIN = 0x10000;
  static private final int NONBMP_MAX = 0x10FFFF;
  static private final char SURROGATE2_MIN = '\uDC00';
  static private final char SURROGATE2_MAX = '\uDFFF';

  static final Localizer localizer = new Localizer (Translator.class);

  static private final String [] blockNames = { "BasicLatin",
                                               "Latin-1Supplement",
                                               "LatinExtended-A",
                                               "LatinExtended-B",
                                               "IPAExtensions",
                                               "SpacingModifierLetters",
                                               "CombiningDiacriticalMarks",
                                               "Greek",
                                               "Cyrillic",
                                               "Armenian",
                                               "Hebrew",
                                               "Arabic",
                                               "Syriac",
                                               "Thaana",
                                               "Devanagari",
                                               "Bengali",
                                               "Gurmukhi",
                                               "Gujarati",
                                               "Oriya",
                                               "Tamil",
                                               "Telugu",
                                               "Kannada",
                                               "Malayalam",
                                               "Sinhala",
                                               "Thai",
                                               "Lao",
                                               "Tibetan",
                                               "Myanmar",
                                               "Georgian",
                                               "HangulJamo",
                                               "Ethiopic",
                                               "Cherokee",
                                               "UnifiedCanadianAboriginalSyllabics",
                                               "Ogham",
                                               "Runic",
                                               "Khmer",
                                               "Mongolian",
                                               "LatinExtendedAdditional",
                                               "GreekExtended",
                                               "GeneralPunctuation",
                                               "SuperscriptsandSubscripts",
                                               "CurrencySymbols",
                                               "CombiningMarksforSymbols",
                                               "LetterlikeSymbols",
                                               "NumberForms",
                                               "Arrows",
                                               "MathematicalOperators",
                                               "MiscellaneousTechnical",
                                               "ControlPictures",
                                               "OpticalCharacterRecognition",
                                               "EnclosedAlphanumerics",
                                               "BoxDrawing",
                                               "BlockElements",
                                               "GeometricShapes",
                                               "MiscellaneousSymbols",
                                               "Dingbats",
                                               "BraillePatterns",
                                               "CJKRadicalsSupplement",
                                               "KangxiRadicals",
                                               "IdeographicDescriptionCharacters",
                                               "CJKSymbolsandPunctuation",
                                               "Hiragana",
                                               "Katakana",
                                               "Bopomofo",
                                               "HangulCompatibilityJamo",
                                               "Kanbun",
                                               "BopomofoExtended",
                                               "EnclosedCJKLettersandMonths",
                                               "CJKCompatibility",
                                               "CJKUnifiedIdeographsExtensionA",
                                               "CJKUnifiedIdeographs",
                                               "YiSyllables",
                                               "YiRadicals",
                                               "HangulSyllables",
                                               // surrogates excluded because
                                               // there are never any
                                               // *characters* with codes in
                                               // surrogate range
                                               // "PrivateUse", excluded because
                                               // 3.1 adds non-BMP ranges
                                               "CJKCompatibilityIdeographs",
                                               "AlphabeticPresentationForms",
                                               "ArabicPresentationForms-A",
                                               "CombiningHalfMarks",
                                               "CJKCompatibilityForms",
                                               "SmallFormVariants",
                                               "ArabicPresentationForms-B",
                                               "Specials",
                                               "HalfwidthandFullwidthForms",
                                               "Specials" };

  /**
   * Names of blocks including ranges outside the BMP.
   */
  static private final String [] specialBlockNames = { "OldItalic",
                                                      "Gothic",
                                                      "Deseret",
                                                      "ByzantineMusicalSymbols",
                                                      "MusicalSymbols",
                                                      "MathematicalAlphanumericSymbols",
                                                      "CJKUnifiedIdeographsExtensionB",
                                                      "CJKCompatibilityIdeographsSupplement",
                                                      "Tags",
                                                      "PrivateUse" };

  /**
   * CharClass for each block name in specialBlockNames.
   */
  static private final CharClass [] specialBlockCharClasses = { new CharRange (0x10300, 0x1032F),
                                                               new CharRange (0x10330, 0x1034F),
                                                               new CharRange (0x10400, 0x1044F),
                                                               new CharRange (0x1D000, 0x1D0FF),
                                                               new CharRange (0x1D100, 0x1D1FF),
                                                               new CharRange (0x1D400, 0x1D7FF),
                                                               new CharRange (0x20000, 0x2A6D6),
                                                               new CharRange (0x2F800, 0x2FA1F),
                                                               new CharRange (0xE0000, 0xE007F),
                                                               new Union (new CharClass [] { new CharRange (0xE000,
                                                                                                            0xF8FF),
                                                                                            new CharRange (0xF0000,
                                                                                                           0xFFFFD),
                                                                                            new CharRange (0x100000,
                                                                                                           0x10FFFD) }) };

  static private final CharClass DOT = new Complement (new Union (new CharClass [] { new SingleChar ('\n'),
                                                                                    new SingleChar ('\r') }));

  static private final CharClass ESC_d = new Property ("Nd");

  static private final CharClass ESC_D = new Complement (ESC_d);

  static private final CharClass ESC_W = new Union (new CharClass [] { new Property ("P"),
                                                                      new Property ("Z"),
                                                                      new Property ("C") });

  static private final CharClass ESC_w = new Complement (ESC_W);

  static private final CharClass ESC_s = new Union (new CharClass [] { new SingleChar (' '),
                                                                      new SingleChar ('\n'),
                                                                      new SingleChar ('\r'),
                                                                      new SingleChar ('\t') });

  static private final CharClass ESC_S = new Complement (ESC_s);

  static private final CharClass ESC_i = makeCharClass (NamingExceptions.NMSTRT_CATEGORIES,
                                                        NamingExceptions.NMSTRT_INCLUDES,
                                                        NamingExceptions.NMSTRT_EXCLUDE_RANGES);

  static private final CharClass ESC_I = new Complement (ESC_i);

  static private final CharClass ESC_c = makeCharClass (NamingExceptions.NMCHAR_CATEGORIES,
                                                        NamingExceptions.NMCHAR_INCLUDES,
                                                        NamingExceptions.NMCHAR_EXCLUDE_RANGES);

  static private final CharClass ESC_C = new Complement (ESC_c);

  static private final char EOS = '\0';

  private Translator (final String regExp)
  {
    this.regExp = regExp;
    this.length = regExp.length ();
    advance ();
  }

  /**
   * Translates a regular expression in the syntax of XML Schemas Part 2 into a
   * regular expression in the syntax of <code>java.util.regex.Pattern</code>.
   * The translation assumes that the string to be matched against the regex
   * uses surrogate pairs correctly. If the string comes from XML content, a
   * conforming XML parser will automatically check this; if the string comes
   * from elsewhere, it may be necessary to check surrogate usage before
   * matching.
   * 
   * @param regexp
   *        a String containing a regular expression in the syntax of XML
   *        Schemas Part 2
   * @return a String containing a regular expression in the syntax of
   *         java.util.regex.Pattern
   * @throws RegexSyntaxException
   *         if <code>regexp</code> is not a regular expression in the syntax of
   *         XML Schemas Part 2
   * @see java.util.regex.Pattern
   * @see <a href="http://www.w3.org/TR/xmlschema-2/#regexs">XML Schema Part
   *      2</a>
   */
  static public String translate (final String regexp) throws RegexSyntaxException
  {
    final Translator tr = new Translator (regexp);
    tr.translateTop ();
    return tr.result.toString ();
  }

  private void advance ()
  {
    if (pos < length)
      curChar = regExp.charAt (pos++);
    else
    {
      pos++;
      curChar = EOS;
      eos = true;
    }
  }

  private void translateTop () throws RegexSyntaxException
  {
    translateRegExp ();
    if (!eos)
      throw makeException ("expected_eos");
  }

  private void translateRegExp () throws RegexSyntaxException
  {
    translateBranch ();
    while (curChar == '|')
    {
      copyCurChar ();
      translateBranch ();
    }
  }

  private void translateBranch () throws RegexSyntaxException
  {
    while (translateAtom ())
      translateQuantifier ();
  }

  private void translateQuantifier () throws RegexSyntaxException
  {
    switch (curChar)
    {
      case '*':
      case '?':
      case '+':
        copyCurChar ();
        return;
      case '{':
        copyCurChar ();
        translateQuantity ();
        expect ('}');
        copyCurChar ();
    }
  }

  private void translateQuantity () throws RegexSyntaxException
  {
    final String lower = parseQuantExact ();
    int lowerValue = -1;
    try
    {
      lowerValue = Integer.parseInt (lower);
      result.append (lower);
    }
    catch (final NumberFormatException e)
    {
      // JDK 1.4 cannot handle ranges bigger than this
      result.append (Integer.MAX_VALUE);
    }
    if (curChar == ',')
    {
      copyCurChar ();
      if (curChar != '}')
      {
        final String upper = parseQuantExact ();
        try
        {
          final int upperValue = Integer.parseInt (upper);
          result.append (upper);
          if (lowerValue < 0 || upperValue < lowerValue)
            throw makeException ("invalid_quantity_range");
        }
        catch (final NumberFormatException e)
        {
          result.append (Integer.MAX_VALUE);
          if (lowerValue < 0 && new BigDecimal (lower).compareTo (new BigDecimal (upper)) > 0)
            throw makeException ("invalid_quantity_range");
        }
      }
    }
  }

  private String parseQuantExact () throws RegexSyntaxException
  {
    final StringBuffer buf = new StringBuffer ();
    do
    {
      if ("0123456789".indexOf (curChar) < 0)
        throw makeException ("expected_digit");
      buf.append (curChar);
      advance ();
    } while (curChar != ',' && curChar != '}');
    return buf.toString ();
  }

  private void copyCurChar ()
  {
    result.append (curChar);
    advance ();
  }

  static final int NONE = -1;
  static final int SOME = 0;
  static final int ALL = 1;

  static final String SURROGATES1_CLASS = "[\uD800-\uDBFF]";
  static final String SURROGATES2_CLASS = "[\uDC00-\uDFFF]";
  static final String NOT_ALLOWED_CLASS = "[\u0000&&[^\u0000]]";

  static final class Range implements Comparable <Range>
  {
    private final int min;
    private final int max;

    Range (final int min, final int max)
    {
      this.min = min;
      this.max = max;
    }

    int getMin ()
    {
      return min;
    }

    int getMax ()
    {
      return max;
    }

    public int compareTo (final Range o)
    {
      final Range other = o;
      if (this.min < other.min)
        return -1;
      if (this.min > other.min)
        return 1;
      if (this.max > other.max)
        return -1;
      if (this.max < other.max)
        return 1;
      return 0;
    }
  }

  static abstract class CharClass
  {

    private final int containsBmp;
    // if it contains ALL and containsBmp != NONE, then the generated class for
    // containsBmp must
    // contain all the high surrogates
    private final int containsNonBmp;

    protected CharClass (final int containsBmp, final int containsNonBmp)
    {
      this.containsBmp = containsBmp;
      this.containsNonBmp = containsNonBmp;
    }

    int getContainsBmp ()
    {
      return containsBmp;
    }

    int getContainsNonBmp ()
    {
      return containsNonBmp;
    }

    final void output (final StringBuffer buf)
    {
      if (surrogatesDirect)
        outputDirect (buf);
      else
        outputMungeSurrogates (buf);
    }

    final void outputMungeSurrogates (final StringBuffer buf)
    {
      switch (containsNonBmp)
      {
        case NONE:
          if (containsBmp == NONE)
            buf.append (NOT_ALLOWED_CLASS);
          else
            outputDirect (buf);
          break;
        case ALL:
          buf.append ('(');
          if (containsBmp == NONE)
          {
            buf.append (SURROGATES1_CLASS);
            buf.append (SURROGATES2_CLASS);
          }
          else
          {
            outputDirect (buf);
            buf.append (SURROGATES2_CLASS);
            buf.append ('?');
          }
          buf.append (')');
          break;
        case SOME:
          buf.append ('(');
          boolean needSep = false;
          if (containsBmp != NONE)
          {
            needSep = true;
            outputDirect (buf);
          }
          final List <Range> ranges = new Vector <Range> ();
          addNonBmpRanges (ranges);
          sortRangeList (ranges);
          final String hi = highSurrogateRanges (ranges);
          if (hi.length () > 0)
          {
            if (needSep)
              buf.append ('|');
            else
              needSep = true;
            buf.append ('[');
            for (int i = 0, len = hi.length (); i < len; i += 2)
            {
              final char min = hi.charAt (i);
              final char max = hi.charAt (i + 1);
              if (min == max)
                buf.append (min);
              else
              {
                buf.append (min);
                buf.append ('-');
                buf.append (max);
              }
            }
            buf.append (']');
            buf.append (SURROGATES2_CLASS);
          }
          final String lo = lowSurrogateRanges (ranges);
          for (int i = 0, len = lo.length (); i < len; i += 3)
          {
            if (needSep)
              buf.append ('|');
            else
              needSep = true;
            buf.append (lo.charAt (i));
            char min = lo.charAt (i + 1);
            char max = lo.charAt (i + 2);
            if (min == max && (i + 3 >= len || lo.charAt (i + 3) != lo.charAt (i)))
              buf.append (min);
            else
            {
              buf.append ('[');
              for (;;)
              {
                if (min == max)
                  buf.append (min);
                else
                {
                  buf.append (min);
                  buf.append ('-');
                  buf.append (max);
                }
                if (i + 3 >= len || lo.charAt (i + 3) != lo.charAt (i))
                  break;
                i += 3;
                min = lo.charAt (i + 1);
                max = lo.charAt (i + 2);
              }
              buf.append (']');
            }
          }
          if (!needSep)
            buf.append (NOT_ALLOWED_CLASS);
          buf.append (')');
          break;
      }
    }

    static String highSurrogateRanges (final List <Range> ranges)
    {
      final StringBuffer highRanges = new StringBuffer ();
      for (int i = 0, len = ranges.size (); i < len; i++)
      {
        final Range r = ranges.get (i);
        char min1 = Utf16.surrogate1 (r.getMin ());
        final char min2 = Utf16.surrogate2 (r.getMin ());
        char max1 = Utf16.surrogate1 (r.getMax ());
        final char max2 = Utf16.surrogate2 (r.getMax ());
        if (min2 != SURROGATE2_MIN)
          min1++;
        if (max2 != SURROGATE2_MAX)
          max1--;
        if (max1 >= min1)
        {
          highRanges.append (min1);
          highRanges.append (max1);
        }
      }
      return highRanges.toString ();
    }

    static String lowSurrogateRanges (final List <Range> ranges)
    {
      final StringBuffer lowRanges = new StringBuffer ();
      for (int i = 0, len = ranges.size (); i < len; i++)
      {
        final Range r = ranges.get (i);
        final char min1 = Utf16.surrogate1 (r.getMin ());
        final char min2 = Utf16.surrogate2 (r.getMin ());
        final char max1 = Utf16.surrogate1 (r.getMax ());
        final char max2 = Utf16.surrogate2 (r.getMax ());
        if (min1 == max1)
        {
          if (min2 != SURROGATE2_MIN || max2 != SURROGATE2_MAX)
          {
            lowRanges.append (min1);
            lowRanges.append (min2);
            lowRanges.append (max2);
          }
        }
        else
        {
          if (min2 != SURROGATE2_MIN)
          {
            lowRanges.append (min1);
            lowRanges.append (min2);
            lowRanges.append (SURROGATE2_MAX);
          }
          if (max2 != SURROGATE2_MAX)
          {
            lowRanges.append (max1);
            lowRanges.append (SURROGATE2_MIN);
            lowRanges.append (max2);
          }
        }
      }
      return lowRanges.toString ();
    }

    abstract void outputDirect (StringBuffer buf);

    abstract void outputComplementDirect (StringBuffer buf);

    int singleChar ()
    {
      return -1;
    }

    void addNonBmpRanges (final List <Range> ranges)
    {}

    static void sortRangeList (final List <Range> ranges)
    {
      Collections.sort (ranges);
      int toIndex = 0;
      int fromIndex = 0;
      int len = ranges.size ();
      while (fromIndex < len)
      {
        Range r = ranges.get (fromIndex);
        final int min = r.getMin ();
        int max = r.getMax ();
        while (++fromIndex < len)
        {
          final Range r2 = ranges.get (fromIndex);
          if (r2.getMin () > max + 1)
            break;
          if (r2.getMax () > max)
            max = r2.getMax ();
        }
        if (max != r.getMax ())
          r = new Range (min, max);
        ranges.set (toIndex++, r);
      }
      while (len > toIndex)
        ranges.remove (--len);
    }

  }

  static abstract class SimpleCharClass extends CharClass
  {
    SimpleCharClass (final int containsBmp, final int containsNonBmp)
    {
      super (containsBmp, containsNonBmp);
    }

    @Override
    void outputDirect (final StringBuffer buf)
    {
      buf.append ('[');
      inClassOutputDirect (buf);
      buf.append (']');
    }

    // must not call if containsBmp == ALL && !surrogatesDirect
    @Override
    void outputComplementDirect (final StringBuffer buf)
    {
      if (!surrogatesDirect && getContainsBmp () == NONE)
        buf.append ("[\u0000-\uFFFF]");
      else
      {
        buf.append ("[^");
        inClassOutputDirect (buf);
        buf.append (']');
      }
    }

    abstract void inClassOutputDirect (StringBuffer buf);

    static void outputWide (final StringBuffer buf, final int c)
    {
      buf.append (Utf16.surrogate1 (c));
      buf.append (Utf16.surrogate2 (c));
    }
  }

  static class SingleChar extends SimpleCharClass
  {
    private final char c;

    SingleChar (final char c)
    {
      super (SOME, NONE);
      this.c = c;
    }

    @Override
    int singleChar ()
    {
      return c;
    }

    @Override
    void outputDirect (final StringBuffer buf)
    {
      inClassOutputDirect (buf);
    }

    @Override
    void inClassOutputDirect (final StringBuffer buf)
    {
      if (isJavaMetaChar (c))
        buf.append ('\\');
      buf.append (c);
    }

  }

  static class WideSingleChar extends SimpleCharClass
  {
    private final int c;

    WideSingleChar (final int c)
    {
      super (NONE, SOME);
      this.c = c;
    }

    @Override
    void inClassOutputDirect (final StringBuffer buf)
    {
      if (!surrogatesDirect)
        throw new RuntimeException ("BMP output botch");
      outputWide (buf, c);
    }

    @Override
    int singleChar ()
    {
      return c;
    }

    @Override
    void addNonBmpRanges (final List <Range> ranges)
    {
      ranges.add (new Range (c, c));
    }
  }

  static class CharRange extends SimpleCharClass
  {
    private final int lower;
    private final int upper;

    CharRange (final int lower, final int upper)
    {
      super (lower < NONBMP_MIN ? SOME : NONE,
      // don't use ALL here, because that requires that the BMP class contains
      // high surrogates
             upper >= NONBMP_MIN ? SOME : NONE);
      this.lower = lower;
      this.upper = upper;
    }

    @Override
    void inClassOutputDirect (final StringBuffer buf)
    {
      if (lower < NONBMP_MIN)
      {
        if (isJavaMetaChar ((char) lower))
          buf.append ('\\');
        buf.append ((char) lower);
      }
      else
        if (surrogatesDirect)
          outputWide (buf, lower);
        else
          throw new RuntimeException ("BMP output botch");
      buf.append ('-');
      if (upper < NONBMP_MIN)
      {
        if (isJavaMetaChar ((char) upper))
          buf.append ('\\');
        buf.append ((char) upper);
      }
      else
        if (surrogatesDirect)
          outputWide (buf, upper);
        else
          buf.append ('\uFFFF');
    }

    @Override
    void addNonBmpRanges (final List <Range> ranges)
    {
      if (upper >= NONBMP_MIN)
        ranges.add (new Range (lower < NONBMP_MIN ? NONBMP_MIN : lower, upper));
    }
  }

  static class Property extends SimpleCharClass
  {
    private final String name;

    Property (final String name)
    {
      super (SOME, NONE);
      this.name = name;
    }

    @Override
    void outputDirect (final StringBuffer buf)
    {
      inClassOutputDirect (buf);
    }

    @Override
    void inClassOutputDirect (final StringBuffer buf)
    {
      buf.append ("\\p{");
      buf.append (name);
      buf.append ('}');
    }

    @Override
    void outputComplementDirect (final StringBuffer buf)
    {
      buf.append ("\\P{");
      buf.append (name);
      buf.append ('}');
    }
  }

  static class Subtraction extends CharClass
  {
    private final CharClass cc1;
    private final CharClass cc2;

    Subtraction (final CharClass cc1, final CharClass cc2)
    {
      // min corresponds to intersection
      // complement corresponds to negation
      super (Math.min (cc1.getContainsBmp (), -cc2.getContainsBmp ()), Math.min (cc1.getContainsNonBmp (),
                                                                                 -cc2.getContainsNonBmp ()));
      this.cc1 = cc1;
      this.cc2 = cc2;
    }

    @Override
    void outputDirect (final StringBuffer buf)
    {
      buf.append ('[');
      cc1.outputDirect (buf);
      buf.append ("&&");
      cc2.outputComplementDirect (buf);
      buf.append (']');
    }

    @Override
    void outputComplementDirect (final StringBuffer buf)
    {
      buf.append ('[');
      cc1.outputComplementDirect (buf);
      cc2.outputDirect (buf);
      buf.append (']');
    }

    @Override
    void addNonBmpRanges (final List <Range> ranges)
    {
      final List <Range> posList = new Vector <Range> ();
      cc1.addNonBmpRanges (posList);
      final List <Range> negList = new Vector <Range> ();
      cc2.addNonBmpRanges (negList);
      sortRangeList (posList);
      sortRangeList (negList);
      final Iterator <Range> negIter = negList.iterator ();
      Range negRange;
      if (negIter.hasNext ())
        negRange = negIter.next ();
      else
        negRange = null;
      for (int i = 0, len = posList.size (); i < len; i++)
      {
        final Range posRange = posList.get (i);
        while (negRange != null && negRange.getMax () < posRange.getMin ())
        {
          if (negIter.hasNext ())
            negRange = negIter.next ();
          else
            negRange = null;
        }
        // if negRange != null, negRange.max >= posRange.min
        int min = posRange.getMin ();
        while (negRange != null && negRange.getMin () <= posRange.getMax ())
        {
          if (min < negRange.getMin ())
          {
            ranges.add (new Range (min, negRange.getMin () - 1));
          }
          min = negRange.getMax () + 1;
          if (min > posRange.getMax ())
            break;
          if (negIter.hasNext ())
            negRange = negIter.next ();
          else
            negRange = null;
        }
        if (min <= posRange.getMax ())
          ranges.add (new Range (min, posRange.getMax ()));
      }
    }
  }

  static class Union extends CharClass
  {
    private final List <CharClass> members;

    Union (final CharClass [] v)
    {
      this (toList (v));
    }

    static private List <CharClass> toList (final CharClass [] v)
    {
      final List <CharClass> members = new Vector <CharClass> ();
      for (final CharClass element : v)
        members.add (element);
      return members;
    }

    Union (final List <CharClass> members)
    {
      super (computeContainsBmp (members), computeContainsNonBmp (members));
      this.members = members;
    }

    @Override
    void outputDirect (final StringBuffer buf)
    {
      buf.append ('[');
      for (int i = 0, len = members.size (); i < len; i++)
      {
        final CharClass cc = members.get (i);
        if (surrogatesDirect || cc.getContainsBmp () != NONE)
        {
          if (cc instanceof SimpleCharClass)
            ((SimpleCharClass) cc).inClassOutputDirect (buf);
          else
            cc.outputDirect (buf);
        }
      }
      buf.append (']');
    }

    @Override
    void outputComplementDirect (final StringBuffer buf)
    {
      boolean first = true;
      final int len = members.size ();
      for (int i = 0; i < len; i++)
      {
        final CharClass cc = members.get (i);
        if ((surrogatesDirect || cc.getContainsBmp () != NONE) && cc instanceof SimpleCharClass)
        {
          if (first)
          {
            buf.append ("[^");
            first = false;
          }
          ((SimpleCharClass) cc).inClassOutputDirect (buf);
        }
      }
      for (int i = 0; i < len; i++)
      {
        final CharClass cc = members.get (i);
        if ((surrogatesDirect || cc.getContainsBmp () != NONE) && !(cc instanceof SimpleCharClass))
        {
          if (first)
          {
            buf.append ('[');
            first = false;
          }
          else
            buf.append ("&&");
          // can't have any members that are ALL, because that would make this
          // ALL, which violates
          // the precondition for outputComplementDirect
          cc.outputComplementDirect (buf);
        }
      }
      if (first)
        // all members are NONE, so this is NONE, so complement is everything
        buf.append ("[\u0000-\uFFFF]");
      else
        buf.append (']');
    }

    @Override
    void addNonBmpRanges (final List <Range> ranges)
    {
      for (int i = 0, len = members.size (); i < len; i++)
        members.get (i).addNonBmpRanges (ranges);
    }

    private static int computeContainsBmp (final List <CharClass> members)
    {
      int ret = NONE;
      for (int i = 0, len = members.size (); i < len; i++)
        ret = Math.max (ret, members.get (i).getContainsBmp ());
      return ret;
    }

    private static int computeContainsNonBmp (final List <CharClass> members)
    {
      int ret = NONE;
      for (int i = 0, len = members.size (); i < len; i++)
        ret = Math.max (ret, members.get (i).getContainsNonBmp ());
      return ret;
    }
  }

  static class Complement extends CharClass
  {
    private final CharClass cc;

    Complement (final CharClass cc)
    {
      super (-cc.getContainsBmp (), -cc.getContainsNonBmp ());
      this.cc = cc;
    }

    @Override
    void outputDirect (final StringBuffer buf)
    {
      cc.outputComplementDirect (buf);
    }

    @Override
    void outputComplementDirect (final StringBuffer buf)
    {
      cc.outputDirect (buf);
    }

    @Override
    void addNonBmpRanges (final List <Range> ranges)
    {
      final List <Range> tem = new Vector <Range> ();
      cc.addNonBmpRanges (tem);
      sortRangeList (tem);
      int c = NONBMP_MIN;
      for (int i = 0, len = tem.size (); i < len; i++)
      {
        final Range r = tem.get (i);
        if (r.getMin () > c)
          ranges.add (new Range (c, r.getMin () - 1));
        c = r.getMax () + 1;
      }
      if (c != NONBMP_MAX + 1)
        ranges.add (new Range (c, NONBMP_MAX));
    }
  }

  private boolean translateAtom () throws RegexSyntaxException
  {
    switch (curChar)
    {
      case EOS:
        if (!eos)
          break;
        // fall through
      case '?':
      case '*':
      case '+':
      case ')':
      case '{':
      case '}':
      case '|':
      case ']':
        return false;
      case '(':
        copyCurChar ();
        translateRegExp ();
        expect (')');
        copyCurChar ();
        return true;
      case '\\':
        advance ();
        parseEsc ().output (result);
        return true;
      case '[':
        advance ();
        parseCharClassExpr ().output (result);
        return true;
      case '.':
        DOT.output (result);
        advance ();
        return true;
      case '$':
      case '^':
        result.append ('\\');
        break;
    }
    copyCurChar ();
    return true;
  }

  static private CharClass makeCharClass (final String categories, final String includes, final String excludeRanges)
  {
    final List <CharClass> includeList = new Vector <CharClass> ();
    for (int i = 0, len = categories.length (); i < len; i += 2)
      includeList.add (new Property (categories.substring (i, i + 2)));
    for (int i = 0, len = includes.length (); i < len; i++)
    {
      int j = i + 1;
      for (; j < len && includes.charAt (j) - includes.charAt (i) == j - i; j++)
      {}
      --j;
      if (i == j - 1)
        --j;
      if (i == j)
        includeList.add (new SingleChar (includes.charAt (i)));
      else
        includeList.add (new CharRange (includes.charAt (i), includes.charAt (j)));
      i = j;
    }
    final List <CharClass> excludeList = new Vector <CharClass> ();
    for (int i = 0, len = excludeRanges.length (); i < len; i += 2)
    {
      final char min = excludeRanges.charAt (i);
      final char max = excludeRanges.charAt (i + 1);
      if (min == max)
        excludeList.add (new SingleChar (min));
      else
        if (min == max - 1)
        {
          excludeList.add (new SingleChar (min));
          excludeList.add (new SingleChar (max));
        }
        else
          excludeList.add (new CharRange (min, max));
    }
    if (surrogatesDirect)
      excludeList.add (new CharRange (NONBMP_MIN, NONBMP_MAX)); // Unicode 4.0
                                                                // adds some
                                                                // non-BMP
                                                                // letters
    return new Subtraction (new Union (includeList), new Union (excludeList));
  }

  private CharClass parseEsc () throws RegexSyntaxException
  {
    switch (curChar)
    {
      case 'n':
        advance ();
        return new SingleChar ('\n');
      case 'r':
        advance ();
        return new SingleChar ('\r');
      case 't':
        advance ();
        return new SingleChar ('\t');
      case '\\':
      case '|':
      case '.':
      case '-':
      case '^':
      case '?':
      case '*':
      case '+':
      case '(':
      case ')':
      case '{':
      case '}':
      case '[':
      case ']':
        break;
      case 's':
        advance ();
        return ESC_s;
      case 'S':
        advance ();
        return ESC_S;
      case 'i':
        advance ();
        return ESC_i;
      case 'I':
        advance ();
        return ESC_I;
      case 'c':
        advance ();
        return ESC_c;
      case 'C':
        advance ();
        return ESC_C;
      case 'd':
        advance ();
        return ESC_d;
      case 'D':
        advance ();
        return ESC_D;
      case 'w':
        advance ();
        return ESC_w;
      case 'W':
        advance ();
        return ESC_W;
      case 'p':
        advance ();
        return parseProp ();
      case 'P':
        advance ();
        return new Complement (parseProp ());
      default:
        throw makeException ("bad_escape");
    }
    final CharClass tem = new SingleChar (curChar);
    advance ();
    return tem;
  }

  private CharClass parseProp () throws RegexSyntaxException
  {
    expect ('{');
    final int start = pos;
    for (;;)
    {
      advance ();
      if (curChar == '}')
        break;
      if (!isAsciiAlnum (curChar) && curChar != '-')
        expect ('}');
    }
    final String propertyName = regExp.substring (start, pos - 1);
    advance ();
    switch (propertyName.length ())
    {
      case 0:
        throw makeException ("empty_property_name");
      case 2:
        final int sci = subCategories.indexOf (propertyName);
        if (sci < 0 || sci % 2 != 0)
          throw makeException ("bad_category");
        return getSubCategoryCharClass (sci / 2);
      case 1:
        final int ci = categories.indexOf (propertyName.charAt (0));
        if (ci < 0)
          throw makeException ("bad_category", propertyName);
        return getCategoryCharClass (ci);
      default:
        if (!propertyName.startsWith ("Is"))
          break;
        final String blockName = propertyName.substring (2);
        for (int i = 0; i < specialBlockNames.length; i++)
          if (blockName.equals (specialBlockNames[i]))
            return specialBlockCharClasses[i];
        if (!isBlock (blockName))
          throw makeException ("bad_block_name", blockName);
        return new Property ("In" + blockName);
    }
    throw makeException ("bad_property_name", propertyName);
  }

  static private boolean isBlock (final String name)
  {
    for (final String blockName : blockNames)
      if (name.equals (blockName))
        return true;
    return false;
  }

  static private boolean isAsciiAlnum (final char c)
  {
    if ('a' <= c && c <= 'z')
      return true;
    if ('A' <= c && c <= 'Z')
      return true;
    if ('0' <= c && c <= '9')
      return true;
    return false;
  }

  private void expect (final char c) throws RegexSyntaxException
  {
    if (curChar != c)
      throw makeException ("expected", new String (new char [] { c }));
  }

  private CharClass parseCharClassExpr () throws RegexSyntaxException
  {
    boolean compl;
    if (curChar == '^')
    {
      advance ();
      compl = true;
    }
    else
      compl = false;
    final List <CharClass> members = new Vector <CharClass> ();
    do
    {
      final CharClass lower = parseCharClassEscOrXmlChar ();
      members.add (lower);
      if (curChar == '-')
      {
        advance ();
        if (curChar == '[')
          break;
        final CharClass upper = parseCharClassEscOrXmlChar ();
        if (lower.singleChar () < 0 || upper.singleChar () < 0)
          throw makeException ("multi_range");
        if (lower.singleChar () > upper.singleChar ())
          throw makeException ("invalid_range");
        members.set (members.size () - 1, new CharRange (lower.singleChar (), upper.singleChar ()));
        if (curChar == '-')
        {
          advance ();
          expect ('[');
          break;
        }
      }
    } while (curChar != ']');
    CharClass result;
    if (members.size () == 1)
      result = members.get (0);
    else
      result = new Union (members);
    if (compl)
      result = new Complement (result);
    if (curChar == '[')
    {
      advance ();
      result = new Subtraction (result, parseCharClassExpr ());
      expect (']');
    }
    advance ();
    return result;
  }

  private CharClass parseCharClassEscOrXmlChar () throws RegexSyntaxException
  {
    switch (curChar)
    {
      case EOS:
        if (eos)
          expect (']');
        break;
      case '\\':
        advance ();
        return parseEsc ();
      case '[':
      case ']':
      case '-':
        throw makeException ("should_quote", new String (new char [] { curChar }));
    }
    CharClass tem;
    if (Utf16.isSurrogate (curChar))
    {
      if (!Utf16.isSurrogate1 (curChar))
        throw makeException ("invalid_surrogate");
      final char c1 = curChar;
      advance ();
      if (!Utf16.isSurrogate2 (curChar))
        throw makeException ("invalid_surrogate");
      tem = new WideSingleChar (Utf16.scalarValue (c1, curChar));
    }
    else
      tem = new SingleChar (curChar);
    advance ();
    return tem;
  }

  private RegexSyntaxException makeException (final String key)
  {
    return new RegexSyntaxException (localizer.message (key), pos - 1);
  }

  private RegexSyntaxException makeException (final String key, final String arg)
  {
    return new RegexSyntaxException (localizer.message (key, arg), pos - 1);
  }

  static private boolean isJavaMetaChar (final char c)
  {
    switch (c)
    {
      case '\\':
      case '^':
      case '?':
      case '*':
      case '+':
      case '(':
      case ')':
      case '{':
      case '}':
      case '|':
      case '[':
      case ']':
      case '-':
      case '&':
      case '$':
      case '.':
        return true;
    }
    return false;
  }

  static private synchronized CharClass getCategoryCharClass (final int ci)
  {
    if (categoryCharClasses[ci] == null)
      categoryCharClasses[ci] = computeCategoryCharClass (categories.charAt (ci));
    return categoryCharClasses[ci];
  }

  static private synchronized CharClass getSubCategoryCharClass (final int sci)
  {
    if (subCategoryCharClasses[sci] == null)
      subCategoryCharClasses[sci] = computeSubCategoryCharClass (subCategories.substring (sci * 2, (sci + 1) * 2));
    return subCategoryCharClasses[sci];
  }

  static private final char UNICODE_3_1_ADD_Lu = '\u03F4'; // added in 3.1
  static private final char UNICODE_3_1_ADD_Ll = '\u03F5'; // added in 3.1
  // 3 characters changed from No to Nl between 3.0 and 3.1
  static private final char UNICODE_3_1_CHANGE_No_to_Nl_MIN = '\u16EE';
  static private final char UNICODE_3_1_CHANGE_No_to_Nl_MAX = '\u16F0';
  static private final String CATEGORY_Pi = "\u00AB\u2018\u201B\u201C\u201F\u2039"; // Java
                                                                                    // doesn't
                                                                                    // know
                                                                                    // about
                                                                                    // category
                                                                                    // Pi
  static private final String CATEGORY_Pf = "\u00BB\u2019\u201D\u203A"; // Java
                                                                        // doesn't
                                                                        // know
                                                                        // about
                                                                        // category
                                                                        // Pf

  static private CharClass computeCategoryCharClass (final char code)
  {
    final List <CharClass> classes = new Vector <CharClass> ();
    classes.add (new Property (new String (new char [] { code })));
    if (!surrogatesDirect)
    {
      for (int ci = Categories.CATEGORY_NAMES.indexOf (code); ci >= 0; ci = Categories.CATEGORY_NAMES.indexOf (code,
                                                                                                               ci + 1))
      {
        final int [] addRanges = Categories.CATEGORY_RANGES[ci / 2];
        for (int i = 0; i < addRanges.length; i += 2)
          classes.add (new CharRange (addRanges[i], addRanges[i + 1]));
      }
    }
    if (code == 'P')
      classes.add (makeCharClass (CATEGORY_Pi + CATEGORY_Pf));
    if (code == 'L')
    {
      classes.add (new SingleChar (UNICODE_3_1_ADD_Ll));
      classes.add (new SingleChar (UNICODE_3_1_ADD_Lu));
    }
    if (code == 'C')
      classes.add (computeSubCategoryCharClass ("Cn")); // JDK 1.4 leaves Cn out
                                                        // of C?
    if (classes.size () == 1)
      return classes.get (0);
    return new Union (classes);
  }

  static private CharClass computeSubCategoryCharClass (final String name)
  {
    if (name.equals ("Pi"))
      return makeCharClass (CATEGORY_Pi);
    if (name.equals ("Pf"))
      return makeCharClass (CATEGORY_Pf);
    CharClass base = new Property (name);
    if (name.equals ("Cn"))
    {
      // Unassigned
      final List <CharClass> assignedRanges = new Vector <CharClass> ();
      assignedRanges.add (new SingleChar (UNICODE_3_1_ADD_Lu));
      assignedRanges.add (new SingleChar (UNICODE_3_1_ADD_Ll));
      if (!surrogatesDirect)
      {
        for (final int [] element : Categories.CATEGORY_RANGES)
          for (int j = 0; j < element.length; j += 2)
            assignedRanges.add (new CharRange (element[j], element[j + 1]));
        base = new Union (new CharClass [] { base, new CharRange (NONBMP_MIN, NONBMP_MAX) });
      }
      return new Subtraction (base, new Union (assignedRanges));
    }
    final List <CharClass> classes = new Vector <CharClass> ();
    classes.add (base);
    if (!surrogatesDirect)
    {
      final int sci = Categories.CATEGORY_NAMES.indexOf (name);
      if (sci >= 0)
      {
        final int [] addRanges = Categories.CATEGORY_RANGES[sci / 2];
        for (int i = 0; i < addRanges.length; i += 2)
          classes.add (new CharRange (addRanges[i], addRanges[i + 1]));
      }
    }
    if (name.equals ("Lu"))
      classes.add (new SingleChar (UNICODE_3_1_ADD_Lu));
    else
      if (name.equals ("Ll"))
        classes.add (new SingleChar (UNICODE_3_1_ADD_Ll));
      else
        if (name.equals ("Nl"))
          classes.add (new CharRange (UNICODE_3_1_CHANGE_No_to_Nl_MIN, UNICODE_3_1_CHANGE_No_to_Nl_MAX));
        else
          if (name.equals ("No"))
            return new Subtraction (new Union (classes), new CharRange (UNICODE_3_1_CHANGE_No_to_Nl_MIN,
                                                                        UNICODE_3_1_CHANGE_No_to_Nl_MAX));
    if (classes.size () == 1)
      return base;
    return new Union (classes);
  }

  private static CharClass makeCharClass (final String members)
  {
    final List <CharClass> list = new Vector <CharClass> ();
    for (int i = 0, len = members.length (); i < len; i++)
      list.add (new SingleChar (members.charAt (i)));
    return new Union (list);
  }

  public static void main (final String [] args) throws RegexSyntaxException
  {
    final String s = translate (args[0]);
    for (int i = 0, len = s.length (); i < len; i++)
    {
      final char c = s.charAt (i);
      if (c >= 0x20 && c <= 0x7e)
        System.err.print (c);
      else
      {
        System.err.print ("\\u");
        for (int shift = 12; shift >= 0; shift -= 4)
          System.err.print ("0123456789ABCDEF".charAt ((c >> shift) & 0xF));
      }
    }
    System.err.println ();
  }
}
