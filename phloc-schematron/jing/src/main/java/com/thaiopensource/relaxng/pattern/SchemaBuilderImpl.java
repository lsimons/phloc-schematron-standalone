package com.thaiopensource.relaxng.pattern;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeBuilder;
import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.DatatypeLibrary;
import org.relaxng.datatype.DatatypeLibraryFactory;
import org.relaxng.datatype.ValidationContext;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import com.thaiopensource.relaxng.parse.BuildException;
import com.thaiopensource.relaxng.parse.Context;
import com.thaiopensource.relaxng.parse.DataPatternBuilder;
import com.thaiopensource.relaxng.parse.Div;
import com.thaiopensource.relaxng.parse.ElementAnnotationBuilder;
import com.thaiopensource.relaxng.parse.Grammar;
import com.thaiopensource.relaxng.parse.GrammarSection;
import com.thaiopensource.relaxng.parse.IllegalSchemaException;
import com.thaiopensource.relaxng.parse.Include;
import com.thaiopensource.relaxng.parse.IncludedGrammar;
import com.thaiopensource.relaxng.parse.ParseReceiver;
import com.thaiopensource.relaxng.parse.Parseable;
import com.thaiopensource.relaxng.parse.ParsedPatternFuture;
import com.thaiopensource.relaxng.parse.SchemaBuilder;
import com.thaiopensource.relaxng.parse.Scope;
import com.thaiopensource.relaxng.parse.SubParseable;
import com.thaiopensource.relaxng.parse.SubParser;
import com.thaiopensource.util.Localizer;
import com.thaiopensource.util.VoidValue;
import com.thaiopensource.xml.util.Name;

public class SchemaBuilderImpl extends AnnotationsImpl implements
                                                      ElementAnnotationBuilder <Locator, VoidValue, CommentListImpl>,
                                                      SchemaBuilder <Pattern, NameClass, Locator, VoidValue, CommentListImpl, AnnotationsImpl>
{
  private final SchemaBuilderImpl parent;
  private boolean hadError = false;
  private final SubParser <Pattern, NameClass, Locator, VoidValue, CommentListImpl, AnnotationsImpl> subParser;
  private final SchemaPatternBuilder pb;
  private final DatatypeLibraryFactory datatypeLibraryFactory;
  private final String inheritNs;
  private final ErrorHandler eh;
  private final OpenIncludes openIncludes;
  private final AttributeNameClassChecker attributeNameClassChecker = new AttributeNameClassChecker ();
  static final Localizer localizer = new Localizer (SchemaBuilderImpl.class);

  static class OpenIncludes
  {
    final String uri;
    final OpenIncludes parent;

    OpenIncludes (final String uri, final OpenIncludes parent)
    {
      this.uri = uri;
      this.parent = parent;
    }
  }

  static public Pattern parse (final Parseable <Pattern, NameClass, Locator, VoidValue, CommentListImpl, AnnotationsImpl> parseable,
                               final ErrorHandler eh,
                               final DatatypeLibraryFactory datatypeLibraryFactory,
                               final SchemaPatternBuilder pb,
                               final boolean isAttributesPattern) throws IllegalSchemaException,
                                                                 IOException,
                                                                 SAXException
  {
    try
    {
      final SchemaBuilderImpl sb = new SchemaBuilderImpl (parseable,
                                                          eh,
                                                          new BuiltinDatatypeLibraryFactory (datatypeLibraryFactory),
                                                          pb);
      Pattern pattern = parseable.parse (sb, new RootScope (sb));
      if (isAttributesPattern)
        pattern = sb.wrapAttributesPattern (pattern);
      return sb.expandPattern (pattern);
    }
    catch (final BuildException e)
    {
      throw unwrapBuildException (e);
    }
  }

  static public PatternFuture installHandlers (final ParseReceiver <Pattern, NameClass, Locator, VoidValue, CommentListImpl, AnnotationsImpl> parser,
                                               final XMLReader xr,
                                               final ErrorHandler eh,
                                               final DatatypeLibraryFactory dlf,
                                               final SchemaPatternBuilder pb) throws SAXException
  {
    final SchemaBuilderImpl sb = new SchemaBuilderImpl (parser, eh, new BuiltinDatatypeLibraryFactory (dlf), pb);
    final ParsedPatternFuture <Pattern> pf = parser.installHandlers (xr, sb, new RootScope (sb));
    return new PatternFuture ()
    {
      public Pattern getPattern (final boolean isAttributesPattern) throws IllegalSchemaException,
                                                                   SAXException,
                                                                   IOException
      {
        try
        {
          Pattern pattern = pf.getParsedPattern ();
          if (isAttributesPattern)
            pattern = sb.wrapAttributesPattern (pattern);
          return sb.expandPattern (pattern);
        }
        catch (final BuildException e)
        {
          throw unwrapBuildException (e);
        }
      }
    };
  }

  static public RuntimeException unwrapBuildException (final BuildException e) throws SAXException,
                                                                              IllegalSchemaException,
                                                                              IOException
  {
    final Throwable t = e.getCause ();
    if (t instanceof IOException)
      throw (IOException) t;
    if (t instanceof RuntimeException)
      return (RuntimeException) t;
    if (t instanceof IllegalSchemaException)
      throw new IllegalSchemaException ();
    if (t instanceof SAXException)
      throw (SAXException) t;
    if (t instanceof Exception)
      throw new SAXException ((Exception) t);
    throw new SAXException (t.getClass ().getName () + " thrown");
  }

  private Pattern wrapAttributesPattern (final Pattern pattern)
  {
    // XXX where can we get a locator from?
    return makeElement (makeAnyName (null, null), pattern, null, null);
  }

  private Pattern expandPattern (Pattern pattern) throws IllegalSchemaException, BuildException
  {
    if (!hadError)
    {
      try
      {
        pattern.checkRecursion (0);
        pattern = pattern.expand (pb);
        pattern.checkRestrictions (Pattern.START_CONTEXT, null, null);
        if (!hadError)
          return pattern;
      }
      catch (final SAXParseException e)
      {
        error (e);
      }
      catch (final SAXException e)
      {
        throw new BuildException (e);
      }
      catch (final RestrictionViolationException e)
      {
        if (e.getName () != null)
          error (e.getMessageId (), NameFormatter.format (e.getName ()), e.getLocator ());
        else
          if (e.getNamespaceUri () != null)
            error (e.getMessageId (), e.getNamespaceUri (), e.getLocator ());
          else
            error (e.getMessageId (), e.getLocator ());
      }
    }
    throw new IllegalSchemaException ();
  }

  private SchemaBuilderImpl (final SubParser <Pattern, NameClass, Locator, VoidValue, CommentListImpl, AnnotationsImpl> subParser,
                             final ErrorHandler eh,
                             final DatatypeLibraryFactory datatypeLibraryFactory,
                             final SchemaPatternBuilder pb)
  {
    this.parent = null;
    this.subParser = subParser;
    this.eh = eh;
    this.datatypeLibraryFactory = datatypeLibraryFactory;
    this.pb = pb;
    this.inheritNs = "";
    this.openIncludes = null;
  }

  private SchemaBuilderImpl (final String inheritNs, final String uri, final SchemaBuilderImpl parent)
  {
    this.parent = parent;
    this.subParser = parent.subParser;
    this.eh = parent.eh;
    this.datatypeLibraryFactory = parent.datatypeLibraryFactory;
    this.pb = parent.pb;
    this.inheritNs = parent.resolveInherit (inheritNs);
    this.openIncludes = new OpenIncludes (uri, parent.openIncludes);
  }

  public Pattern makeChoice (final List <Pattern> patterns, final Locator loc, final AnnotationsImpl anno) throws BuildException
  {
    final int nPatterns = patterns.size ();
    if (nPatterns <= 0)
      throw new IllegalArgumentException ();
    Pattern result = patterns.get (0);
    for (int i = 1; i < nPatterns; i++)
      result = pb.makeChoice (result, patterns.get (i));
    return result;
  }

  public Pattern makeInterleave (final List <Pattern> patterns, final Locator loc, final AnnotationsImpl anno) throws BuildException
  {
    final int nPatterns = patterns.size ();
    if (nPatterns <= 0)
      throw new IllegalArgumentException ();
    Pattern result = patterns.get (0);
    for (int i = 1; i < nPatterns; i++)
      result = pb.makeInterleave (result, patterns.get (i));
    return result;
  }

  public Pattern makeGroup (final List <Pattern> patterns, final Locator loc, final AnnotationsImpl anno) throws BuildException
  {
    final int nPatterns = patterns.size ();
    if (nPatterns <= 0)
      throw new IllegalArgumentException ();
    Pattern result = patterns.get (0);
    for (int i = 1; i < nPatterns; i++)
      result = pb.makeGroup (result, patterns.get (i));
    return result;
  }

  public Pattern makeOneOrMore (final Pattern p, final Locator loc, final AnnotationsImpl anno) throws BuildException
  {
    return pb.makeOneOrMore (p);
  }

  public Pattern makeZeroOrMore (final Pattern p, final Locator loc, final AnnotationsImpl anno) throws BuildException
  {
    return pb.makeZeroOrMore (p);
  }

  public Pattern makeOptional (final Pattern p, final Locator loc, final AnnotationsImpl anno) throws BuildException
  {
    return pb.makeOptional (p);
  }

  public Pattern makeList (final Pattern p, final Locator loc, final AnnotationsImpl anno) throws BuildException
  {
    return pb.makeList (p, loc);
  }

  public Pattern makeMixed (final Pattern p, final Locator loc, final AnnotationsImpl anno) throws BuildException
  {
    return pb.makeMixed (p);
  }

  public Pattern makeEmpty (final Locator loc, final AnnotationsImpl anno)
  {
    return pb.makeEmpty ();
  }

  public Pattern makeNotAllowed (final Locator loc, final AnnotationsImpl anno)
  {
    return pb.makeUnexpandedNotAllowed ();
  }

  public Pattern makeText (final Locator loc, final AnnotationsImpl anno)
  {
    return pb.makeText ();
  }

  public Pattern makeErrorPattern ()
  {
    return pb.makeError ();
  }

  public NameClass makeErrorNameClass ()
  {
    return new ErrorNameClass ();
  }

  public Pattern makeAttribute (final NameClass nc, final Pattern p, final Locator loc, final AnnotationsImpl anno) throws BuildException
  {
    final String messageId = attributeNameClassChecker.checkNameClass (nc);
    if (messageId != null)
      error (messageId, loc);
    return pb.makeAttribute (nc, p, loc);
  }

  public Pattern makeElement (final NameClass nc, final Pattern p, final Locator loc, final AnnotationsImpl anno) throws BuildException
  {
    return pb.makeElement (nc, p, loc);
  }

  private class DummyDataPatternBuilder implements
                                       DataPatternBuilder <Pattern, Locator, VoidValue, CommentListImpl, AnnotationsImpl>
  {
    public void addParam (final String name,
                          final String value,
                          final Context context,
                          final String ns,
                          final Locator loc,
                          final AnnotationsImpl anno) throws BuildException
    {}

    public void annotation (final VoidValue ea) throws BuildException
    {}

    public Pattern makePattern (final Locator loc, final AnnotationsImpl anno) throws BuildException
    {
      return pb.makeError ();
    }

    public Pattern makePattern (final Pattern except, final Locator loc, final AnnotationsImpl anno) throws BuildException
    {
      return pb.makeError ();
    }
  }

  private class ValidationContextImpl implements ValidationContext
  {
    private final ValidationContext vc;
    private final String ns;

    ValidationContextImpl (final ValidationContext vc, final String ns)
    {
      this.vc = vc;
      this.ns = ns.length () == 0 ? null : ns;
    }

    public String resolveNamespacePrefix (final String prefix)
    {
      final String result = prefix.length () == 0 ? ns : vc.resolveNamespacePrefix (prefix);
      if (result == INHERIT_NS)
      {
        if (inheritNs.length () == 0)
          return null;
        return inheritNs;
      }
      return result;
    }

    public String getBaseUri ()
    {
      return vc.getBaseUri ();
    }

    public boolean isUnparsedEntity (final String entityName)
    {
      return vc.isUnparsedEntity (entityName);
    }

    public boolean isNotation (final String notationName)
    {
      return vc.isNotation (notationName);
    }
  }

  private class DataPatternBuilderImpl implements
                                      DataPatternBuilder <Pattern, Locator, VoidValue, CommentListImpl, AnnotationsImpl>
  {
    private final DatatypeBuilder dtb;
    private final Name dtName;
    private final List <String> params = new ArrayList <String> ();

    DataPatternBuilderImpl (final DatatypeBuilder dtb, final Name dtName)
    {
      this.dtb = dtb;
      this.dtName = dtName;
    }

    public void addParam (final String name,
                          final String value,
                          final Context context,
                          final String ns,
                          final Locator loc,
                          final AnnotationsImpl anno) throws BuildException
    {
      try
      {
        dtb.addParameter (name, value, new ValidationContextImpl (context, ns));
        params.add (name);
        params.add (value);
      }
      catch (final DatatypeException e)
      {
        final String detail = e.getMessage ();
        final int pos = e.getIndex ();
        String displayedParam;
        if (pos == DatatypeException.UNKNOWN)
          displayedParam = null;
        else
          displayedParam = displayParam (value, pos);
        if (displayedParam != null)
        {
          if (detail != null)
            error ("invalid_param_detail_display", detail, displayedParam, loc);
          else
            error ("invalid_param_display", displayedParam, loc);
        }
        else
          if (detail != null)
            error ("invalid_param_detail", detail, loc);
          else
            error ("invalid_param", loc);
      }
    }

    public void annotation (final VoidValue ea) throws BuildException
    {}

    String displayParam (final String value, int pos)
    {
      if (pos < 0)
        pos = 0;
      else
        if (pos > value.length ())
          pos = value.length ();
      return localizer.message ("display_param", value.substring (0, pos), value.substring (pos));
    }

    public Pattern makePattern (final Locator loc, final AnnotationsImpl anno) throws BuildException
    {
      try
      {
        return pb.makeData (dtb.createDatatype (), dtName, params);
      }
      catch (final DatatypeException e)
      {
        final String detail = e.getMessage ();
        if (detail != null)
          error ("invalid_params_detail", detail, loc);
        else
          error ("invalid_params", loc);
        return pb.makeError ();
      }
    }

    public Pattern makePattern (final Pattern except, final Locator loc, final AnnotationsImpl anno) throws BuildException
    {
      try
      {
        return pb.makeDataExcept (dtb.createDatatype (), dtName, params, except, loc);
      }
      catch (final DatatypeException e)
      {
        final String detail = e.getMessage ();
        if (detail != null)
          error ("invalid_params_detail", detail, loc);
        else
          error ("invalid_params", loc);
        return pb.makeError ();
      }
    }
  }

  public DataPatternBuilder <Pattern, Locator, VoidValue, CommentListImpl, AnnotationsImpl> makeDataPatternBuilder (final String datatypeLibrary,
                                                                                                                    final String type,
                                                                                                                    final Locator loc) throws BuildException
  {
    final DatatypeLibrary dl = datatypeLibraryFactory.createDatatypeLibrary (datatypeLibrary);
    if (dl == null)
      error ("unrecognized_datatype_library", datatypeLibrary, loc);
    else
    {
      try
      {
        return new DataPatternBuilderImpl (dl.createDatatypeBuilder (type), new Name (datatypeLibrary, type));
      }
      catch (final DatatypeException e)
      {
        final String detail = e.getMessage ();
        if (detail != null)
          error ("unsupported_datatype_detail", datatypeLibrary, type, detail, loc);
        else
          error ("unrecognized_datatype", datatypeLibrary, type, loc);
      }
    }
    return new DummyDataPatternBuilder ();
  }

  public Pattern makeValue (final String datatypeLibrary,
                            final String type,
                            final String value,
                            final Context context,
                            final String ns,
                            final Locator loc,
                            final AnnotationsImpl anno) throws BuildException
  {
    final DatatypeLibrary dl = datatypeLibraryFactory.createDatatypeLibrary (datatypeLibrary);
    if (dl == null)
      error ("unrecognized_datatype_library", datatypeLibrary, loc);
    else
    {
      try
      {
        final DatatypeBuilder dtb = dl.createDatatypeBuilder (type);
        try
        {
          final Datatype dt = dtb.createDatatype ();
          final Object obj = dt.createValue (value, new ValidationContextImpl (context, ns));
          if (obj != null)
            return pb.makeValue (dt, new Name (datatypeLibrary, type), obj, value);
          error ("invalid_value", value, loc);
        }
        catch (final DatatypeException e)
        {
          final String detail = e.getMessage ();
          if (detail != null)
            error ("datatype_requires_param_detail", detail, loc);
          else
            error ("datatype_requires_param", loc);
        }
      }
      catch (final DatatypeException e)
      {
        error ("unrecognized_datatype", datatypeLibrary, type, loc);
      }
    }
    return pb.makeError ();
  }

  static class GrammarImpl implements
                          Grammar <Pattern, Locator, VoidValue, CommentListImpl, AnnotationsImpl>,
                          Div <Pattern, Locator, VoidValue, CommentListImpl, AnnotationsImpl>,
                          IncludedGrammar <Pattern, Locator, VoidValue, CommentListImpl, AnnotationsImpl>
  {
    private final SchemaBuilderImpl sb;
    private final Map <String, RefPattern> defines;
    private final RefPattern startRef;
    private final Scope <Pattern, Locator, VoidValue, CommentListImpl, AnnotationsImpl> parent;

    private GrammarImpl (final SchemaBuilderImpl sb,
                         final Scope <Pattern, Locator, VoidValue, CommentListImpl, AnnotationsImpl> parent)
    {
      this.sb = sb;
      this.parent = parent;
      this.defines = new HashMap <String, RefPattern> ();
      this.startRef = new RefPattern (null);
    }

    protected GrammarImpl (final SchemaBuilderImpl sb, final GrammarImpl g)
    {
      this.sb = sb;
      parent = g.parent;
      startRef = g.startRef;
      defines = g.defines;
    }

    public Pattern endGrammar (final Locator loc, final AnnotationsImpl anno) throws BuildException
    {
      for (final String name : defines.keySet ())
      {
        final RefPattern rp = defines.get (name);
        if (rp.getPattern () == null)
        {
          sb.error ("reference_to_undefined", name, rp.getRefLocator ());
          rp.setPattern (sb.pb.makeError ());
        }
      }
      Pattern start = startRef.getPattern ();
      if (start == null)
      {
        sb.error ("missing_start_element", loc);
        start = sb.pb.makeError ();
      }
      return start;
    }

    public void endDiv (final Locator loc, final AnnotationsImpl anno) throws BuildException
    {
      // nothing to do
    }

    public Pattern endIncludedGrammar (final Locator loc, final AnnotationsImpl anno) throws BuildException
    {
      return null;
    }

    public void define (final String name,
                        final GrammarSection.Combine combine,
                        final Pattern pattern,
                        final Locator loc,
                        final AnnotationsImpl anno) throws BuildException
    {
      define (lookup (name), combine, pattern, loc);
    }

    private void define (final RefPattern rp,
                         final GrammarSection.Combine combine,
                         final Pattern pattern,
                         final Locator loc) throws BuildException
    {
      switch (rp.getReplacementStatus ())
      {
        case RefPattern.REPLACEMENT_KEEP:
          if (combine == null)
          {
            if (rp.isCombineImplicit ())
            {
              if (rp.getName () == null)
                sb.error ("duplicate_start", loc);
              else
                sb.error ("duplicate_define", rp.getName (), loc);
            }
            else
              rp.setCombineImplicit ();
          }
          else
          {
            final byte combineType = (combine == COMBINE_CHOICE ? RefPattern.COMBINE_CHOICE
                                                               : RefPattern.COMBINE_INTERLEAVE);
            if (rp.getCombineType () != RefPattern.COMBINE_NONE && rp.getCombineType () != combineType)
            {
              if (rp.getName () == null)
                sb.error ("conflict_combine_start", loc);
              else
                sb.error ("conflict_combine_define", rp.getName (), loc);
            }
            rp.setCombineType (combineType);
          }
          if (rp.getPattern () == null)
            rp.setPattern (pattern);
          else
            if (rp.getCombineType () == RefPattern.COMBINE_INTERLEAVE)
              rp.setPattern (sb.pb.makeInterleave (rp.getPattern (), pattern));
            else
              rp.setPattern (sb.pb.makeChoice (rp.getPattern (), pattern));
          break;
        case RefPattern.REPLACEMENT_REQUIRE:
          rp.setReplacementStatus (RefPattern.REPLACEMENT_IGNORE);
          break;
        case RefPattern.REPLACEMENT_IGNORE:
          break;
      }
    }

    public void topLevelAnnotation (final VoidValue ea) throws BuildException
    {}

    public void topLevelComment (final CommentListImpl comments) throws BuildException
    {}

    private RefPattern lookup (final String name)
    {
      if (name == START)
        return startRef;
      return lookup1 (name);
    }

    private RefPattern lookup1 (final String name)
    {
      RefPattern p = defines.get (name);
      if (p == null)
      {
        p = new RefPattern (name);
        defines.put (name, p);
      }
      return p;
    }

    public Pattern makeRef (final String name, final Locator loc, final AnnotationsImpl anno) throws BuildException
    {
      final RefPattern p = lookup1 (name);
      if (p.getRefLocator () == null && loc != null)
        p.setRefLocator (loc);
      return p;
    }

    public Pattern makeParentRef (final String name, final Locator loc, final AnnotationsImpl anno) throws BuildException
    {
      if (parent == null)
      {
        sb.error ("parent_ref_outside_grammar", loc);
        return sb.makeErrorPattern ();
      }
      return parent.makeRef (name, loc, anno);
    }

    public Div <Pattern, Locator, VoidValue, CommentListImpl, AnnotationsImpl> makeDiv ()
    {
      return this;
    }

    public Include <Pattern, Locator, VoidValue, CommentListImpl, AnnotationsImpl> makeInclude ()
    {
      return new IncludeImpl (sb, this);
    }

  }

  static class RootScope implements Scope <Pattern, Locator, VoidValue, CommentListImpl, AnnotationsImpl>
  {
    private final SchemaBuilderImpl sb;

    RootScope (final SchemaBuilderImpl sb)
    {
      this.sb = sb;
    }

    public Pattern makeParentRef (final String name, final Locator loc, final AnnotationsImpl anno) throws BuildException
    {
      sb.error ("parent_ref_outside_grammar", loc);
      return sb.makeErrorPattern ();
    }

    public Pattern makeRef (final String name, final Locator loc, final AnnotationsImpl anno) throws BuildException
    {
      sb.error ("ref_outside_grammar", loc);
      return sb.makeErrorPattern ();
    }

  }

  static class Override
  {
    Override (final RefPattern prp, final Override next)
    {
      this.prp = prp;
      this.next = next;
    }

    final RefPattern prp;
    final Override next;
    byte replacementStatus;
  }

  private static class IncludeImpl implements
                                  Include <Pattern, Locator, VoidValue, CommentListImpl, AnnotationsImpl>,
                                  Div <Pattern, Locator, VoidValue, CommentListImpl, AnnotationsImpl>
  {
    private final SchemaBuilderImpl sb;
    private Override overrides;
    private final GrammarImpl grammar;

    private IncludeImpl (final SchemaBuilderImpl sb, final GrammarImpl grammar)
    {
      this.sb = sb;
      this.grammar = grammar;
    }

    public void define (final String name,
                        final GrammarSection.Combine combine,
                        final Pattern pattern,
                        final Locator loc,
                        final AnnotationsImpl anno) throws BuildException
    {
      final RefPattern rp = grammar.lookup (name);
      overrides = new Override (rp, overrides);
      grammar.define (rp, combine, pattern, loc);
    }

    public void endDiv (final Locator loc, final AnnotationsImpl anno) throws BuildException
    {
      // nothing to do
    }

    public void topLevelAnnotation (final VoidValue ea) throws BuildException
    {
      // nothing to do
    }

    public void topLevelComment (final CommentListImpl comments) throws BuildException
    {}

    public Div <Pattern, Locator, VoidValue, CommentListImpl, AnnotationsImpl> makeDiv ()
    {
      return this;
    }

    public void endInclude (final String href,
                            final String base,
                            final String ns,
                            final Locator loc,
                            final AnnotationsImpl anno) throws BuildException
    {
      final SubParseable <Pattern, NameClass, Locator, VoidValue, CommentListImpl, AnnotationsImpl> subParseable = sb.subParser.createSubParseable (href,
                                                                                                                                                    base);
      final String uri = subParseable.getUri ();
      for (OpenIncludes inc = sb.openIncludes; inc != null; inc = inc.parent)
      {
        if (inc.uri.equals (uri))
        {
          sb.error ("recursive_include", uri, loc);
          return;
        }
      }

      for (Override o = overrides; o != null; o = o.next)
      {
        o.replacementStatus = o.prp.getReplacementStatus ();
        o.prp.setReplacementStatus (RefPattern.REPLACEMENT_REQUIRE);
      }
      try
      {
        final SchemaBuilderImpl isb = new SchemaBuilderImpl (ns, uri, sb);
        subParseable.parseAsInclude (isb, new GrammarImpl (isb, grammar));
        for (Override o = overrides; o != null; o = o.next)
        {
          if (o.prp.getReplacementStatus () == RefPattern.REPLACEMENT_REQUIRE)
          {
            if (o.prp.getName () == null)
              sb.error ("missing_start_replacement", loc);
            else
              sb.error ("missing_define_replacement", o.prp.getName (), loc);
          }
        }
      }
      catch (final IllegalSchemaException e)
      {
        sb.noteError ();
      }
      finally
      {
        for (Override o = overrides; o != null; o = o.next)
          o.prp.setReplacementStatus (o.replacementStatus);
      }
    }

    public Include <Pattern, Locator, VoidValue, CommentListImpl, AnnotationsImpl> makeInclude ()
    {
      return null;
    }
  }

  public Grammar <Pattern, Locator, VoidValue, CommentListImpl, AnnotationsImpl> makeGrammar (final Scope <Pattern, Locator, VoidValue, CommentListImpl, AnnotationsImpl> parent)
  {
    return new GrammarImpl (this, parent);
  }

  public Pattern makeExternalRef (final String href,
                                  final String base,
                                  final String ns,
                                  final Scope <Pattern, Locator, VoidValue, CommentListImpl, AnnotationsImpl> scope,
                                  final Locator loc,
                                  final AnnotationsImpl anno) throws BuildException
  {
    final SubParseable <Pattern, NameClass, Locator, VoidValue, CommentListImpl, AnnotationsImpl> subParseable = subParser.createSubParseable (href,
                                                                                                                                               base);
    final String uri = subParseable.getUri ();
    for (OpenIncludes inc = openIncludes; inc != null; inc = inc.parent)
    {
      if (inc.uri.equals (uri))
      {
        error ("recursive_include", uri, loc);
        return pb.makeError ();
      }
    }
    try
    {
      return subParseable.parse (new SchemaBuilderImpl (ns, uri, this), scope);
    }
    catch (final IllegalSchemaException e)
    {
      noteError ();
      return pb.makeError ();
    }
  }

  public NameClass makeNameClassChoice (final List <NameClass> nameClasses,
                                        final Locator loc,
                                        final AnnotationsImpl anno)
  {
    final int nNameClasses = nameClasses.size ();
    if (nNameClasses <= 0)
      throw new IllegalArgumentException ();
    NameClass result = nameClasses.get (0);
    for (int i = 1; i < nNameClasses; i++)
      result = new ChoiceNameClass (result, nameClasses.get (i));
    return result;
  }

  public NameClass makeName (final String ns,
                             final String localName,
                             final String prefix,
                             final Locator loc,
                             final AnnotationsImpl anno)
  {
    return new SimpleNameClass (new Name (resolveInherit (ns), localName));
  }

  public NameClass makeNsName (final String ns, final Locator loc, final AnnotationsImpl anno)
  {
    return new NsNameClass (resolveInherit (ns));
  }

  public NameClass makeNsName (final String ns, final NameClass except, final Locator loc, final AnnotationsImpl anno)
  {
    return new NsNameExceptNameClass (resolveInherit (ns), except);
  }

  public NameClass makeAnyName (final Locator loc, final AnnotationsImpl anno)
  {
    return new AnyNameClass ();
  }

  public NameClass makeAnyName (final NameClass except, final Locator loc, final AnnotationsImpl anno)
  {
    return new AnyNameExceptNameClass (except);
  }

  public AnnotationsImpl makeAnnotations (final CommentListImpl comments, final Context context)
  {
    return this;
  }

  public VoidValue makeElementAnnotation () throws BuildException
  {
    return VoidValue.VOID;
  }

  public void addText (final String value, final Locator loc, final CommentListImpl comments) throws BuildException
  {}

  public ElementAnnotationBuilder <Locator, VoidValue, CommentListImpl> makeElementAnnotationBuilder (final String ns,
                                                                                                      final String localName,
                                                                                                      final String prefix,
                                                                                                      final Locator loc,
                                                                                                      final CommentListImpl comments,
                                                                                                      final Context context)
  {
    return this;
  }

  public CommentListImpl makeCommentList ()
  {
    return this;
  }

  public boolean usesComments ()
  {
    return false;
  }

  public Pattern annotatePattern (final Pattern p, final AnnotationsImpl anno) throws BuildException
  {
    return p;
  }

  public NameClass annotateNameClass (final NameClass nc, final AnnotationsImpl anno) throws BuildException
  {
    return nc;
  }

  public Pattern annotateAfterPattern (final Pattern p, final VoidValue e) throws BuildException
  {
    return p;
  }

  public NameClass annotateAfterNameClass (final NameClass nc, final VoidValue e) throws BuildException
  {
    return nc;
  }

  public Pattern commentAfterPattern (final Pattern p, final CommentListImpl comments) throws BuildException
  {
    return p;
  }

  public NameClass commentAfterNameClass (final NameClass nc, final CommentListImpl comments) throws BuildException
  {
    return nc;
  }

  private String resolveInherit (final String ns)
  {
    if (ns == INHERIT_NS)
      return inheritNs;
    return ns;
  }

  private class LocatorImpl implements Locator
  {
    private final String systemId;
    private final int lineNumber;
    private final int columnNumber;

    private LocatorImpl (final String systemId, final int lineNumber, final int columnNumber)
    {
      this.systemId = systemId;
      this.lineNumber = lineNumber;
      this.columnNumber = columnNumber;
    }

    public String getPublicId ()
    {
      return null;
    }

    public String getSystemId ()
    {
      return systemId;
    }

    public int getLineNumber ()
    {
      return lineNumber;
    }

    public int getColumnNumber ()
    {
      return columnNumber;
    }
  }

  public Locator makeLocation (final String systemId, final int lineNumber, final int columnNumber)
  {
    return new LocatorImpl (systemId, lineNumber, columnNumber);
  }

  private void error (final SAXParseException message) throws BuildException
  {
    noteError ();
    try
    {
      if (eh != null)
        eh.error (message);
    }
    catch (final SAXException e)
    {
      throw new BuildException (e);
    }
  }

  /*
   * private void warning(SAXParseException message) throws BuildException { try
   * { if (eh != null) eh.warning(message); } catch (SAXException e) { throw new
   * BuildException(e); } }
   */

  private void error (final String key, final Locator loc) throws BuildException
  {
    error (new SAXParseException (localizer.message (key), loc));
  }

  private void error (final String key, final String arg, final Locator loc) throws BuildException
  {
    error (new SAXParseException (localizer.message (key, arg), loc));
  }

  private void error (final String key, final String arg1, final String arg2, final Locator loc) throws BuildException
  {
    error (new SAXParseException (localizer.message (key, arg1, arg2), loc));
  }

  private void error (final String key, final String arg1, final String arg2, final String arg3, final Locator loc) throws BuildException
  {
    error (new SAXParseException (localizer.message (key, new Object [] { arg1, arg2, arg3 }), loc));
  }

  private void noteError ()
  {
    if (!hadError && parent != null)
      parent.noteError ();
    hadError = true;
  }
}
