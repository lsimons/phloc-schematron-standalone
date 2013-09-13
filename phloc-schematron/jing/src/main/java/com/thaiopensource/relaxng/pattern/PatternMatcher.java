package com.thaiopensource.relaxng.pattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.relaxng.datatype.Datatype;

import com.thaiopensource.datatype.Datatype2;
import com.thaiopensource.relaxng.match.MatchContext;
import com.thaiopensource.relaxng.match.Matcher;
import com.thaiopensource.util.Equal;
import com.thaiopensource.util.Localizer;
import com.thaiopensource.xml.util.Name;

public class PatternMatcher implements Cloneable, Matcher
{

  static private class Shared
  {
    private final Pattern start;
    private final ValidatorPatternBuilder builder;
    private Map <Name, Pattern> recoverPatternTable;

    Shared (final Pattern start, final ValidatorPatternBuilder builder)
    {
      this.start = start;
      this.builder = builder;
    }

    Pattern findElement (final Name name)
    {
      if (recoverPatternTable == null)
        recoverPatternTable = new HashMap <Name, Pattern> ();
      Pattern p = recoverPatternTable.get (name);
      if (p == null)
      {
        p = FindElementFunction.findElement (builder, name, start);
        recoverPatternTable.put (name, p);
      }
      return p;
    }
  }

  private PatternMemo memo;
  private boolean textTyped;
  private boolean hadError;
  private boolean ignoreNextEndTagOrAttributeValue;
  private String errorMessage;
  private final Shared shared;
  private List <DataDerivFailure> dataDerivFailureList = new ArrayList <DataDerivFailure> ();

  public PatternMatcher (final Pattern start, final ValidatorPatternBuilder builder)
  {
    shared = new Shared (start, builder);
    memo = builder.getPatternMemo (start);
  }

  private PatternMatcher (final PatternMemo memo, final Shared shared)
  {
    this.memo = memo;
    this.shared = shared;
  }

  public Matcher start ()
  {
    return new PatternMatcher (shared.builder.getPatternMemo (shared.start), shared);
  }

  @Override
  public boolean equals (final Object obj)
  {
    if (!(obj instanceof PatternMatcher))
      return false;
    final PatternMatcher other = (PatternMatcher) obj;
    // don't need to test equality of shared, because the memos can only be ==
    // if the shareds are ==.
    return (memo == other.memo &&
            hadError == other.hadError &&
            Equal.equal (errorMessage, other.errorMessage) &&
            ignoreNextEndTagOrAttributeValue == other.ignoreNextEndTagOrAttributeValue && textTyped == other.textTyped);
  }

  @Override
  public int hashCode ()
  {
    return memo.hashCode ();
  }

  @Override
  public final Object clone ()
  {
    try
    {
      final PatternMatcher cloned = (PatternMatcher) super.clone ();
      cloned.dataDerivFailureList = new ArrayList <DataDerivFailure> ();
      return cloned;
    }
    catch (final CloneNotSupportedException e)
    {
      throw new Error ("unexpected CloneNotSupportedException");
    }
  }

  public Matcher copy ()
  {
    return (Matcher) clone ();
  }

  public boolean matchStartDocument ()
  {
    if (memo.isNotAllowed ())
      return error ("schema_allows_nothing");
    return true;
  }

  public boolean matchEndDocument ()
  {
    // XXX maybe check that memo.isNullable if !hadError
    return true;
  }

  public boolean matchStartTagOpen (final Name name, final String qName, final MatchContext context)
  {
    if (setMemo (memo.startTagOpenDeriv (name)))
      return true;
    PatternMemo next = memo.startTagOpenRecoverDeriv (name);
    final boolean ok = ignoreError ();
    if (!next.isNotAllowed ())
    {
      if (!ok)
      {
        final Set <Name> missing = requiredElementNames ();
        if (!missing.isEmpty ())
          error (missing.size () == 1 ? "unexpected_element_required_element_missing"
                                     : "unexpected_element_required_elements_missing",
                 errorArgQName (qName, name, context, false),
                 formatNames (missing, FORMAT_NAMES_ELEMENT | FORMAT_NAMES_AND, context));
        else
          error ("element_not_allowed_yet", errorArgQName (qName, name, context, false), expectedContent (context));
      }
    }
    else
    {
      final ValidatorPatternBuilder builder = shared.builder;
      next = builder.getPatternMemo (builder.makeAfter (shared.findElement (name), memo.getPattern ()));
      if (!ok)
        error (next.isNotAllowed () ? "unknown_element" : "out_of_context_element",
               errorArgQName (qName, name, context, false),
               expectedContent (context));
    }
    memo = next;
    return ok;
  }

  public boolean matchAttributeName (final Name name, String qName, final MatchContext context)
  {
    if (setMemo (memo.startAttributeDeriv (name)))
      return true;
    ignoreNextEndTagOrAttributeValue = true;
    final boolean ok = ignoreError ();
    if (ok)
      return true;
    qName = errorArgQName (qName, name, context, true);
    final NormalizedNameClass nnc = memo.possibleAttributeNames ();
    if (nnc.isEmpty ())
      error ("no_attributes_allowed", qName);
    else
      error ("invalid_attribute_name", qName, expectedAttributes (context));
    return false;
  }

  public boolean matchAttributeValue (final String value,
                                      final Name name,
                                      final String qName,
                                      final MatchContext context)
  {
    if (ignoreNextEndTagOrAttributeValue)
    {
      ignoreNextEndTagOrAttributeValue = false;
      return true;
    }
    dataDerivFailureList.clear ();
    if (setMemo (memo.dataDeriv (value, context, dataDerivFailureList)))
      return true;
    final boolean ok = error ("invalid_attribute_value",
                              errorArgQName (qName, name, context, true),
                              formatDataDerivFailures (value, context));
    memo = memo.recoverAfter ();
    return ok;
  }

  public boolean matchStartTagClose (final Name name, final String qName, final MatchContext context)
  {
    boolean ok;
    if (setMemo (memo.endAttributes ()))
      ok = true;
    else
    {
      ok = ignoreError ();
      if (!ok)
      {
        final Set <Name> missing = requiredAttributeNames ();
        if (missing.isEmpty ())
          error ("required_attributes_missing_expected",
                 errorArgQName (qName, name, context, false),
                 expectedAttributes (context));
        else
          error (missing.size () == 1 ? "required_attribute_missing" : "required_attributes_missing",
                 errorArgQName (qName, name, context, false),
                 formatNames (missing, FORMAT_NAMES_ATTRIBUTE | FORMAT_NAMES_AND, context));
      }
      memo = memo.ignoreMissingAttributes ();
    }
    textTyped = memo.getPattern ().getContentType () == Pattern.DATA_CONTENT_TYPE;
    return ok;
  }

  public boolean matchTextBeforeEndTag (final String string,
                                        final Name name,
                                        final String qName,
                                        final MatchContext context)
  {
    if (textTyped)
    {
      ignoreNextEndTagOrAttributeValue = true;
      return setDataDeriv (string, name, qName, context);
    }
    else
      return matchUntypedText (string, context);
  }

  public boolean matchTextBeforeStartTag (final String string, final MatchContext context)
  {
    return matchUntypedText (string, context);
  }

  private boolean matchUntypedText (final String string, final MatchContext context)
  {
    if (DataDerivFunction.isBlank (string))
      return true;
    return matchUntypedText (context);
  }

  public boolean matchUntypedText (final MatchContext context)
  {
    if (setMemo (memo.mixedTextDeriv ()))
      return true;
    return error ("text_not_allowed", expectedContent (context));
  }

  public boolean isTextTyped ()
  {
    return textTyped;
  }

  private boolean setDataDeriv (final String string, final Name name, final String qName, final MatchContext context)
  {
    textTyped = false;
    final PatternMemo textOnlyMemo = memo.textOnly ();
    dataDerivFailureList.clear ();
    if (setMemo (textOnlyMemo.dataDeriv (string, context, dataDerivFailureList)))
      return true;
    final PatternMemo next = memo.recoverAfter ();
    final boolean ok = ignoreError ();
    if (!ok && (!next.isNotAllowed () || textOnlyMemo.emptyAfter ().dataDeriv (string, context).isNotAllowed ()))
    {
      final NormalizedNameClass nnc = memo.possibleStartTagNames ();
      if (!nnc.isEmpty () && DataDerivFunction.isBlank (string))
        error ("blank_not_allowed", errorArgQName (qName, name, context, false), expectedContent (context));
      else
        error ("invalid_element_value",
               errorArgQName (qName, name, context, false),
               formatDataDerivFailures (string, context));
    }
    memo = next;
    return ok;
  }

  public boolean matchEndTag (final Name name, final String qName, final MatchContext context)
  {
    if (ignoreNextEndTagOrAttributeValue)
    {
      ignoreNextEndTagOrAttributeValue = false;
      return true;
    }
    if (textTyped)
      return setDataDeriv ("", name, qName, context);
    if (setMemo (memo.endTagDeriv ()))
      return true;
    final boolean ok = ignoreError ();
    final PatternMemo next = memo.recoverAfter ();
    // The tricky thing here is that the derivative that we compute may be
    // notAllowed simply because the parent
    // is notAllowed; we don't want to give an error in this case.
    if (!ok && (!next.isNotAllowed ()
    // Retry computing the deriv on a pattern where the after is OK (not
    // notAllowed)
        || memo.emptyAfter ().endTagDeriv ().isNotAllowed ()))
    {
      final Set <Name> missing = requiredElementNames ();
      if (!missing.isEmpty ())
        error (missing.size () == 1 ? "incomplete_element_required_element_missing"
                                   : "incomplete_element_required_elements_missing",
               errorArgQName (qName, name, context, false),
               formatNames (missing, FORMAT_NAMES_ELEMENT | FORMAT_NAMES_AND, context));
      else
        // XXX Could do better here and describe what is required instead of
        // what is possible
        error ("incomplete_element_required_elements_missing_expected",
               errorArgQName (qName, name, context, false),
               expectedContent (context));
    }
    memo = next;
    return ok;
  }

  public String getErrorMessage ()
  {
    return errorMessage;
  }

  public boolean isValidSoFar ()
  {
    return !hadError;
  }

  public com.thaiopensource.relaxng.match.NameClass possibleStartTagNames ()
  {
    return memo.possibleStartTagNames ();
  }

  public com.thaiopensource.relaxng.match.NameClass possibleAttributeNames ()
  {
    return memo.possibleAttributeNames ();
  }

  public Set <Name> requiredElementNames ()
  {
    return memo.getPattern ().apply (shared.builder.getRequiredElementsFunction ());
  }

  public Set <Name> requiredAttributeNames ()
  {
    return memo.getPattern ().apply (shared.builder.getRequiredAttributesFunction ());
  }

  private boolean setMemo (final PatternMemo m)
  {
    if (m.isNotAllowed ())
      return false;
    else
    {
      memo = m;
      return true;
    }
  }

  private boolean ignoreError ()
  {
    return hadError && memo.isNotAllowed ();
  }

  /*
   * Return true if the error was ignored, false otherwise.
   */
  private boolean error (final String key)
  {
    return error (key, new String [] {});
  }

  private boolean error (final String key, final String arg)
  {
    return error (key, new String [] { arg });
  }

  private boolean error (final String key, final String arg1, final String arg2)
  {
    return error (key, new String [] { arg1, arg2 });
  }

  private boolean error (final String key, final String [] args)
  {
    if (ignoreError ())
      return true;
    hadError = true;
    errorMessage = localizer ().message (key, args);
    return false;
  }

  private String errorArgQName (String qName, final Name name, final MatchContext context, final boolean isAttribute)
  {
    if (ignoreError ())
      return null;
    if (qName == null || qName.length () == 0)
    {
      final String ns = name.getNamespaceUri ();
      final String localName = name.getLocalName ();
      if (ns.length () == 0 || (!isAttribute && ns.equals (context.resolveNamespacePrefix (""))))
        qName = localName;
      else
      {
        final String prefix = context.getPrefix (ns);
        if (prefix != null)
          qName = prefix + ":" + localName;
        // this shouldn't happen unless the parser isn't supplying prefixes
        // properly
        else
          qName = "{" + ns + "}" + localName;
      }
    }
    return quoteQName (qName);
  }

  static private final int UNDEFINED_TOKEN_INDEX = -3;
  static private final int INCONSISTENT_TOKEN_INDEX = -2;

  private String formatDataDerivFailures (final String str, final MatchContext context)
  {
    if (ignoreError ())
      return null;
    if (dataDerivFailureList.size () == 0)
      return "";
    if (dataDerivFailureList.size () > 1)
    {
      // remove duplicates
      final Set <DataDerivFailure> failures = new HashSet <DataDerivFailure> ();
      failures.addAll (dataDerivFailureList);
      dataDerivFailureList.clear ();
      dataDerivFailureList.addAll (failures);
    }
    final List <String> stringValues = new ArrayList <String> ();
    final Set <Name> names = new HashSet <Name> ();
    final List <String> messages = new ArrayList <String> ();
    int tokenIndex = UNDEFINED_TOKEN_INDEX;
    int tokenStart = -1;
    int tokenEnd = -1;
    for (final DataDerivFailure fail : dataDerivFailureList)
    {
      final Datatype dt = fail.getDatatype ();
      final String s = fail.getStringValue ();
      if (s != null)
      {
        final Object value = fail.getValue ();
        // we imply some special semantics for Datatype2
        if (value instanceof Name && dt instanceof Datatype2)
          names.add ((Name) value);
        else
          if (value instanceof String && dt instanceof Datatype2)
            stringValues.add ((String) value);
          else
            stringValues.add (s);
      }
      else
      {
        final String message = fail.getMessage ();
        // XXX this might produce strangely worded messages for 3rd party
        // datatype libraries
        if (message != null)
          messages.add (message);
        else
          if (fail.getExcept () != null)
            return ""; // XXX do better for except
          else
            messages.add (localizer ().message ("require_datatype", fail.getDatatypeName ().getLocalName ()));
      }
      switch (tokenIndex)
      {
        case INCONSISTENT_TOKEN_INDEX:
          break;
        case UNDEFINED_TOKEN_INDEX:
          tokenIndex = fail.getTokenIndex ();
          tokenStart = fail.getTokenStart ();
          tokenEnd = fail.getTokenEnd ();
          break;
        default:
          if (tokenIndex != fail.getTokenIndex ())
            tokenIndex = INCONSISTENT_TOKEN_INDEX;
          break;
      }
    }
    if (stringValues.size () > 0)
    {
      Collections.sort (stringValues);
      for (int i = 0; i < stringValues.size (); i++)
        stringValues.set (i, quoteValue (stringValues.get (i)));
      messages.add (localizer ().message ("require_values", formatList (stringValues, "or")));
    }
    if (names.size () > 0)
      // XXX provide the strings as well so that a sensible prefix can be chosen
      // if none is declared
      messages.add (localizer ().message ("require_qnames",
                                          formatNames (names, FORMAT_NAMES_OR | FORMAT_NAMES_ELEMENT, context)));
    if (messages.size () == 0)
      return "";
    final String arg = formatList (messages, "or");
    // XXX should do something with inconsistent token index (e.g. list {
    // integer+ } | "foo" )
    if (tokenIndex >= 0 && tokenStart >= 0 && tokenEnd <= str.length ())
    {
      if (tokenStart == str.length ())
        return localizer ().message ("missing_token", arg);
      return localizer ().message ("token_failures", quoteValue (str.substring (tokenStart, tokenEnd)), arg);
    }
    return localizer ().message ("data_failures", arg);
  }

  private String quoteValue (final String str)
  {
    final StringBuilder buf = new StringBuilder ();
    appendAttributeValue (buf, str);
    return buf.toString ();
  }

  private String expectedAttributes (final MatchContext context)
  {
    if (ignoreError ())
      return null;
    final NormalizedNameClass nnc = memo.possibleAttributeNames ();
    if (nnc.isEmpty ())
      return "";
    final Set <Name> expectedNames = nnc.getIncludedNames ();
    if (!expectedNames.isEmpty ())
      return localizer ().message (nnc.isAnyNameIncluded () || !nnc.getIncludedNamespaces ().isEmpty ()
                                                                                                       ? "expected_attribute_or_other_ns"
                                                                                                       : "expected_attribute",
                                   formatNames (expectedNames, FORMAT_NAMES_ATTRIBUTE | FORMAT_NAMES_OR, context));
    return "";
  }

  private String expectedContent (final MatchContext context)
  {
    if (ignoreError ())
      return null;
    final List <String> expected = new ArrayList <String> ();
    if (!memo.endTagDeriv ().isNotAllowed ())
      expected.add (localizer ().message ("element_end_tag"));
    // getContentType isn't so well-defined on after patterns
    switch (memo.emptyAfter ().getPattern ().getContentType ())
    {
      case Pattern.MIXED_CONTENT_TYPE:
        // A pattern such as (element foo { empty }, text) has a
        // MIXED_CONTENT_TYPE
        // but text is not allowed everywhere.
        if (!memo.mixedTextDeriv ().isNotAllowed ())
          expected.add (localizer ().message ("text"));
        break;
      case Pattern.DATA_CONTENT_TYPE:
        expected.add (localizer ().message ("data"));
        break;
    }
    final NormalizedNameClass nnc = memo.possibleStartTagNames ();
    final Set <Name> expectedNames = nnc.getIncludedNames ();
    // XXX say something about wildcards
    if (!expectedNames.isEmpty ())
    {
      expected.add (localizer ().message ("element_list",
                                          formatNames (expectedNames, FORMAT_NAMES_ELEMENT | FORMAT_NAMES_OR, context)));
      if (nnc.isAnyNameIncluded () || !nnc.getIncludedNamespaces ().isEmpty ())
        expected.add (localizer ().message ("element_other_ns"));
    }
    if (expected.isEmpty ())
      return "";
    return localizer ().message ("expected", formatList (expected, "or"));
  }

  static final String GENERATED_PREFIXES[] = { "ns", "ns-", "ns_", "NS", "NS-", "NS_" };

  // Values for flags parameter of formatNames
  static private final int FORMAT_NAMES_ELEMENT = 0x0;
  static private final int FORMAT_NAMES_ATTRIBUTE = 0x1;
  static private final int FORMAT_NAMES_AND = 0x0;
  static private final int FORMAT_NAMES_OR = 0x2;

  private static String formatNames (final Set <Name> names, final int flags, final MatchContext context)
  {
    if (names.isEmpty ())
      return "";
    final Map <String, String> nsDecls = new HashMap <String, String> ();
    final List <String> qNames = generateQNames (names, flags, context, nsDecls);
    Collections.sort (qNames);
    final int len = qNames.size ();
    for (int i = 0; i < len; i++)
      qNames.set (i, quoteQName (qNames.get (i)));
    String result = formatList (qNames, (flags & FORMAT_NAMES_OR) != 0 ? "or" : "and");
    if (nsDecls.size () != 0)
      result = localizer ().message ("qnames_nsdecls", result, formatNamespaceDecls (nsDecls));
    return result;
  }

  private static List <String> generateQNames (final Set <Name> names,
                                               final int flags,
                                               final MatchContext context,
                                               final Map <String, String> nsDecls)
  {
    String defaultNamespace;
    if ((flags & FORMAT_NAMES_ATTRIBUTE) != 0)
      defaultNamespace = "";
    else
    {
      defaultNamespace = context.resolveNamespacePrefix ("");
      for (final Name name : names)
      {
        if (name.getNamespaceUri ().length () == 0)
        {
          if (defaultNamespace != null)
            nsDecls.put ("", "");
          defaultNamespace = "";
          break;
        }
      }
    }
    final List <String> qNames = new ArrayList <String> ();
    final Set <String> undeclaredNamespaces = new HashSet <String> ();
    final List <Name> namesWithUndeclaredNamespaces = new ArrayList <Name> ();
    for (final Name name : names)
    {
      final String ns = name.getNamespaceUri ();
      String prefix;
      if (ns.equals (defaultNamespace))
        prefix = "";
      else
      {
        prefix = context.getPrefix (ns);
        // If we have no prefix for the namespace and we have an attribute, set
        // the prefix to null
        // to mark that the namespace is undeclared.
        if ((flags & FORMAT_NAMES_ATTRIBUTE) != 0 && "".equals (prefix) && !"".equals (ns))
          prefix = null;
      }
      if (prefix == null)
      {
        undeclaredNamespaces.add (ns);
        namesWithUndeclaredNamespaces.add (name);
      }
      else
        qNames.add (makeQName (prefix, name.getLocalName ()));
    }
    if (namesWithUndeclaredNamespaces.isEmpty ())
      return qNames;
    if (undeclaredNamespaces.size () == 1 && defaultNamespace == null)
      nsDecls.put (undeclaredNamespaces.iterator ().next (), "");
    else
      choosePrefixes (undeclaredNamespaces, context, nsDecls);
    // now nsDecls has a prefix for each namespace
    for (final Name name : namesWithUndeclaredNamespaces)
      qNames.add (makeQName (nsDecls.get (name.getNamespaceUri ()), name.getLocalName ()));
    return qNames;
  }

  private static void choosePrefixes (final Set <String> nsSet,
                                      final MatchContext context,
                                      final Map <String, String> nsDecls)
  {
    final List <String> nsList = new ArrayList <String> (nsSet);
    Collections.sort (nsList);
    final int len = nsList.size ();
    String prefix;
    int tryIndex = 0;
    do
    {
      if (tryIndex < GENERATED_PREFIXES.length)
        prefix = GENERATED_PREFIXES[tryIndex];
      else
      {
        // default is just to stick as many underscores as necessary at the
        // beginning
        prefix = "_" + GENERATED_PREFIXES[0];
        for (int i = GENERATED_PREFIXES.length; i < tryIndex; i++)
          prefix += "_" + prefix;
      }
      for (int i = 0; i < len; i++)
      {
        if (context.resolveNamespacePrefix (len == 1 ? prefix : prefix + (i + 1)) != null)
        {
          prefix = null;
          break;
        }
      }
      ++tryIndex;
    } while (prefix == null);
    for (int i = 0; i < len; i++)
    {
      final String ns = nsList.get (i);
      nsDecls.put (ns, len == 1 ? prefix : prefix + (i + 1));
    }
  }

  private static String formatList (final List <String> list, final String conjunction)
  {
    final int len = list.size ();
    switch (len)
    {
      case 0:
        return "";
      case 1:
        return list.get (0);
      case 2:
        return localizer ().message (conjunction + "_list_pair", list.get (0), list.get (1));
    }
    String s = localizer ().message (conjunction + "_list_many_first", list.get (0));
    for (int i = 1; i < len - 1; i++)
      s = localizer ().message (conjunction + "_list_many_middle", s, list.get (i));
    return localizer ().message (conjunction + "_list_many_last", s, list.get (len - 1));
  }

  // nsDecls maps namespaces to prefixes
  private static String formatNamespaceDecls (final Map <String, String> nsDecls)
  {
    final List <String> list = new ArrayList <String> ();
    for (final Map.Entry <String, String> entry : nsDecls.entrySet ())
    {
      final StringBuilder buf = new StringBuilder ();
      final String prefix = entry.getValue ();
      if (prefix.length () == 0)
        buf.append ("xmlns");
      else
        buf.append ("xmlns:").append (prefix);
      buf.append ('=');
      appendAttributeValue (buf, entry.getKey ());
      list.add (buf.toString ());
    }
    Collections.sort (list);
    final StringBuilder buf = new StringBuilder ();
    for (final String aList : list)
    {
      if (buf.length () != 0)
        buf.append (" ");
      buf.append (aList);
    }
    return buf.toString ();
  }

  private static String quoteForAttributeValue (final char c)
  {
    switch (c)
    {
      case '<':
        return "&lt;";
      case '"':
        return "&quot;";
      case '&':
        return "&amp;";
      case 0xA:
        return "&#xA;";
      case 0xD:
        return "&#xD;";
      case 0x9:
        return "&#x9;";
    }
    return null;
  }

  private static StringBuilder appendAttributeValue (final StringBuilder buf, final String value)
  {
    buf.append ('"');
    for (int i = 0; i < value.length (); i++)
    {
      final char c = value.charAt (i);
      final String quoted = quoteForAttributeValue (c);
      if (quoted != null)
        buf.append (quoted);
      else
        buf.append (c);
    }
    buf.append ('"');
    return buf;
  }

  private static String makeQName (final String prefix, final String localName)
  {
    if (prefix.length () == 0)
      return localName;
    return prefix + ":" + localName;
  }

  static private String quoteQName (final String qName)
  {
    return localizer ().message ("qname", qName);
  }

  static private Localizer localizer ()
  {
    return SchemaBuilderImpl.localizer;
  }
}
