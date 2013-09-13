package com.thaiopensource.relaxng.input.parse;

import java.io.IOException;
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

import com.thaiopensource.relaxng.edit.Annotated;
import com.thaiopensource.relaxng.edit.AnyNameNameClass;
import com.thaiopensource.relaxng.edit.AttributePattern;
import com.thaiopensource.relaxng.edit.ChoiceNameClass;
import com.thaiopensource.relaxng.edit.ChoicePattern;
import com.thaiopensource.relaxng.edit.Combine;
import com.thaiopensource.relaxng.edit.Component;
import com.thaiopensource.relaxng.edit.CompositePattern;
import com.thaiopensource.relaxng.edit.Container;
import com.thaiopensource.relaxng.edit.DataPattern;
import com.thaiopensource.relaxng.edit.DefineComponent;
import com.thaiopensource.relaxng.edit.DivComponent;
import com.thaiopensource.relaxng.edit.ElementAnnotation;
import com.thaiopensource.relaxng.edit.ElementPattern;
import com.thaiopensource.relaxng.edit.EmptyPattern;
import com.thaiopensource.relaxng.edit.ExternalRefPattern;
import com.thaiopensource.relaxng.edit.GrammarPattern;
import com.thaiopensource.relaxng.edit.GroupPattern;
import com.thaiopensource.relaxng.edit.IncludeComponent;
import com.thaiopensource.relaxng.edit.InterleavePattern;
import com.thaiopensource.relaxng.edit.ListPattern;
import com.thaiopensource.relaxng.edit.MixedPattern;
import com.thaiopensource.relaxng.edit.NameClass;
import com.thaiopensource.relaxng.edit.NameNameClass;
import com.thaiopensource.relaxng.edit.NotAllowedPattern;
import com.thaiopensource.relaxng.edit.NsNameNameClass;
import com.thaiopensource.relaxng.edit.OneOrMorePattern;
import com.thaiopensource.relaxng.edit.OptionalPattern;
import com.thaiopensource.relaxng.edit.Param;
import com.thaiopensource.relaxng.edit.ParentRefPattern;
import com.thaiopensource.relaxng.edit.Pattern;
import com.thaiopensource.relaxng.edit.RefPattern;
import com.thaiopensource.relaxng.edit.SchemaCollection;
import com.thaiopensource.relaxng.edit.SchemaDocument;
import com.thaiopensource.relaxng.edit.SourceLocation;
import com.thaiopensource.relaxng.edit.TextPattern;
import com.thaiopensource.relaxng.edit.ValuePattern;
import com.thaiopensource.relaxng.edit.ZeroOrMorePattern;
import com.thaiopensource.relaxng.input.CommentTrimmer;
import com.thaiopensource.relaxng.parse.BuildException;
import com.thaiopensource.relaxng.parse.CommentList;
import com.thaiopensource.relaxng.parse.Context;
import com.thaiopensource.relaxng.parse.DataPatternBuilder;
import com.thaiopensource.relaxng.parse.Div;
import com.thaiopensource.relaxng.parse.ElementAnnotationBuilder;
import com.thaiopensource.relaxng.parse.Grammar;
import com.thaiopensource.relaxng.parse.GrammarSection;
import com.thaiopensource.relaxng.parse.IllegalSchemaException;
import com.thaiopensource.relaxng.parse.Include;
import com.thaiopensource.relaxng.parse.IncludedGrammar;
import com.thaiopensource.relaxng.parse.Parseable;
import com.thaiopensource.relaxng.parse.SchemaBuilder;
import com.thaiopensource.relaxng.parse.Scope;
import com.thaiopensource.relaxng.parse.SubParseable;
import com.thaiopensource.relaxng.parse.SubParser;
import com.thaiopensource.util.Localizer;

class SchemaBuilderImpl implements
                       SchemaBuilder <Pattern, NameClass, SourceLocation, ElementAnnotationBuilderImpl, CommentListImpl, AnnotationsImpl>
{
  private final SubParser <Pattern, NameClass, SourceLocation, ElementAnnotationBuilderImpl, CommentListImpl, AnnotationsImpl> subParser;
  private final ErrorHandler eh;
  private final Map <String, SchemaDocument> schemas;
  private final DatatypeLibraryFactory dlf;
  private final boolean commentsNeedTrimming;
  private boolean hadError = false;
  static private final Localizer localizer = new Localizer (SchemaBuilderImpl.class);

  private SchemaBuilderImpl (final SubParser <Pattern, NameClass, SourceLocation, ElementAnnotationBuilderImpl, CommentListImpl, AnnotationsImpl> subParser,
                             final ErrorHandler eh,
                             final Map <String, SchemaDocument> schemas,
                             final DatatypeLibraryFactory dlf,
                             final boolean commentsNeedTrimming)
  {
    this.subParser = subParser;
    this.eh = eh;
    this.schemas = schemas;
    this.dlf = dlf;
    this.commentsNeedTrimming = commentsNeedTrimming;
  }

  public Pattern makeChoice (final List <Pattern> patterns, final SourceLocation loc, final AnnotationsImpl anno) throws BuildException
  {
    return makeComposite (new ChoicePattern (), patterns, loc, anno);
  }

  private static Pattern makeComposite (final CompositePattern p,
                                        final List <Pattern> patterns,
                                        final SourceLocation loc,
                                        final AnnotationsImpl anno) throws BuildException
  {
    p.getChildren ().addAll (patterns);
    return finishPattern (p, loc, anno);
  }

  public Pattern makeGroup (final List <Pattern> patterns, final SourceLocation loc, final AnnotationsImpl anno) throws BuildException
  {
    return makeComposite (new GroupPattern (), patterns, loc, anno);
  }

  public Pattern makeInterleave (final List <Pattern> patterns, final SourceLocation loc, final AnnotationsImpl anno) throws BuildException
  {
    return makeComposite (new InterleavePattern (), patterns, loc, anno);
  }

  public Pattern makeOneOrMore (final Pattern p, final SourceLocation loc, final AnnotationsImpl anno) throws BuildException
  {
    return finishPattern (new OneOrMorePattern (p), loc, anno);
  }

  public Pattern makeZeroOrMore (final Pattern p, final SourceLocation loc, final AnnotationsImpl anno) throws BuildException
  {
    return finishPattern (new ZeroOrMorePattern (p), loc, anno);
  }

  public Pattern makeOptional (final Pattern p, final SourceLocation loc, final AnnotationsImpl anno) throws BuildException
  {
    return finishPattern (new OptionalPattern (p), loc, anno);
  }

  public Pattern makeList (final Pattern p, final SourceLocation loc, final AnnotationsImpl anno) throws BuildException
  {
    return finishPattern (new ListPattern (p), loc, anno);
  }

  public Pattern makeMixed (final Pattern p, final SourceLocation loc, final AnnotationsImpl anno) throws BuildException
  {
    return finishPattern (new MixedPattern (p), loc, anno);
  }

  public Pattern makeEmpty (final SourceLocation loc, final AnnotationsImpl anno)
  {
    return finishPattern (new EmptyPattern (), loc, anno);
  }

  public Pattern makeNotAllowed (final SourceLocation loc, final AnnotationsImpl anno)
  {
    return finishPattern (new NotAllowedPattern (), loc, anno);
  }

  public Pattern makeText (final SourceLocation loc, final AnnotationsImpl anno)
  {
    return finishPattern (new TextPattern (), loc, anno);
  }

  public Pattern makeAttribute (final NameClass nc,
                                final Pattern p,
                                final SourceLocation loc,
                                final AnnotationsImpl anno) throws BuildException
  {
    return finishPattern (new AttributePattern (nc, p), loc, anno);
  }

  public Pattern makeElement (final NameClass nc, final Pattern p, final SourceLocation loc, final AnnotationsImpl anno) throws BuildException
  {
    return finishPattern (new ElementPattern (nc, p), loc, anno);
  }

  private static class TraceValidationContext implements ValidationContext
  {
    private final Map <String, String> map;
    private final ValidationContext vc;
    private final String ns;

    TraceValidationContext (final Map <String, String> map, final ValidationContext vc, final String ns)
    {
      this.map = map;
      this.vc = vc;
      this.ns = ns.length () == 0 ? null : ns;
    }

    public String resolveNamespacePrefix (final String prefix)
    {
      String result;
      if (prefix.length () == 0)
        result = ns;
      else
      {
        result = vc.resolveNamespacePrefix (prefix);
        if (result == SchemaBuilder.INHERIT_NS)
          return null;
      }
      if (result != null)
        map.put (prefix, result);
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

  public Pattern makeValue (final String datatypeLibrary,
                            final String type,
                            final String value,
                            final Context context,
                            final String ns,
                            final SourceLocation loc,
                            final AnnotationsImpl anno) throws BuildException
  {
    final ValuePattern p = new ValuePattern (datatypeLibrary, type, value);
    final DatatypeLibrary dl = dlf.createDatatypeLibrary (datatypeLibrary);
    if (dl != null)
    {
      try
      {
        final DatatypeBuilder dtb = dl.createDatatypeBuilder (type);
        try
        {
          final Datatype dt = dtb.createDatatype ();
          try
          {
            final ValidationContext vc = dt.isContextDependent () ? new TraceValidationContext (p.getPrefixMap (),
                                                                                                context,
                                                                                                ns) : null;
            // use createValue rather than isValid so that default namespace
            // gets used with QName
            if (dt.createValue (value, vc) == null)
              dt.checkValid (value, vc);
          }
          catch (final DatatypeException e)
          {
            diagnoseDatatypeException ("invalid_value_detail", "invalid_value", e, loc);
          }
        }
        catch (final DatatypeException e)
        {
          diagnoseDatatypeException ("invalid_params_detail", "invalid_params", e, loc);
        }
      }
      catch (final DatatypeException e)
      {
        diagnoseDatatypeException ("unsupported_datatype_detail", "unknown_datatype", e, loc);
      }
    }
    return finishPattern (p, loc, anno);
  }

  public Pattern makeExternalRef (final String href,
                                  final String base,
                                  final String ns,
                                  final Scope <Pattern, SourceLocation, ElementAnnotationBuilderImpl, CommentListImpl, AnnotationsImpl> scope,
                                  final SourceLocation loc,
                                  final AnnotationsImpl anno) throws BuildException, IllegalSchemaException
  {
    final SubParseable <Pattern, NameClass, SourceLocation, ElementAnnotationBuilderImpl, CommentListImpl, AnnotationsImpl> subParseable = subParser.createSubParseable (href,
                                                                                                                                                                         base);
    final String uri = subParseable.getUri ();
    final ExternalRefPattern erp = new ExternalRefPattern (uri);
    erp.setNs (mapInheritNs (ns));
    erp.setHref (href);
    erp.setBaseUri (base);
    finishPattern (erp, loc, anno);
    if (schemas.get (uri) == null)
    {
      schemas.put (uri, new SchemaDocument (null)); // avoid possibility of
                                                    // infinite loop
      schemas.put (uri, new SchemaDocument (subParseable.parse (this, scope)));
    }
    return erp;
  }

  static private Pattern finishPattern (final Pattern p, final SourceLocation loc, final AnnotationsImpl anno)
  {
    finishAnnotated (p, loc, anno);
    return p;
  }

  public NameClass makeNameClassChoice (final List <NameClass> nameClasses,
                                        final SourceLocation loc,
                                        final AnnotationsImpl anno)
  {
    final ChoiceNameClass nc = new ChoiceNameClass ();
    nc.getChildren ().addAll (nameClasses);
    return finishNameClass (nc, loc, anno);
  }

  public NameClass makeName (final String ns,
                             final String localName,
                             final String prefix,
                             final SourceLocation loc,
                             final AnnotationsImpl anno)
  {
    final NameNameClass nc = new NameNameClass (mapInheritNs (ns), localName);
    nc.setPrefix (prefix);
    return finishNameClass (nc, loc, anno);
  }

  public NameClass makeNsName (final String ns, final SourceLocation loc, final AnnotationsImpl anno)
  {
    return finishNameClass (new NsNameNameClass (mapInheritNs (ns)), loc, anno);
  }

  public NameClass makeNsName (final String ns,
                               final NameClass except,
                               final SourceLocation loc,
                               final AnnotationsImpl anno)
  {
    return finishNameClass (new NsNameNameClass (mapInheritNs (ns), except), loc, anno);
  }

  public NameClass makeAnyName (final SourceLocation loc, final AnnotationsImpl anno)
  {
    return finishNameClass (new AnyNameNameClass (), loc, anno);
  }

  public NameClass makeAnyName (final NameClass except, final SourceLocation loc, final AnnotationsImpl anno)
  {
    return finishNameClass (new AnyNameNameClass (except), loc, anno);
  }

  private static class ScopeImpl implements
                                Scope <Pattern, SourceLocation, ElementAnnotationBuilderImpl, CommentListImpl, AnnotationsImpl>
  {
    public Pattern makeRef (final String name, final SourceLocation loc, final AnnotationsImpl anno) throws BuildException
    {
      return finishPattern (new RefPattern (name), loc, anno);
    }

    public Pattern makeParentRef (final String name, final SourceLocation loc, final AnnotationsImpl anno) throws BuildException
    {
      return finishPattern (new ParentRefPattern (name), loc, anno);
    }
  }

  private class GrammarSectionImpl extends ScopeImpl implements
                                                    Grammar <Pattern, SourceLocation, ElementAnnotationBuilderImpl, CommentListImpl, AnnotationsImpl>,
                                                    Div <Pattern, SourceLocation, ElementAnnotationBuilderImpl, CommentListImpl, AnnotationsImpl>,
                                                    Include <Pattern, SourceLocation, ElementAnnotationBuilderImpl, CommentListImpl, AnnotationsImpl>,
                                                    IncludedGrammar <Pattern, SourceLocation, ElementAnnotationBuilderImpl, CommentListImpl, AnnotationsImpl>
  {
    private final Annotated subject;
    private final List <Component> components;
    Component lastComponent;

    private GrammarSectionImpl (final Annotated subject, final Container container)
    {
      this.subject = subject;
      this.components = container.getComponents ();
    }

    public void define (String name,
                        final GrammarSection.Combine combine,
                        final Pattern pattern,
                        final SourceLocation loc,
                        final AnnotationsImpl anno) throws BuildException
    {
      if (name == GrammarSection.START)
        name = DefineComponent.START;
      final DefineComponent dc = new DefineComponent (name, pattern);
      if (combine != null)
        dc.setCombine (mapCombine (combine));
      finishAnnotated (dc, loc, anno);
      add (dc);
    }

    public Div <Pattern, SourceLocation, ElementAnnotationBuilderImpl, CommentListImpl, AnnotationsImpl> makeDiv ()
    {
      final DivComponent dc = new DivComponent ();
      add (dc);
      return new GrammarSectionImpl (dc, dc);
    }

    public Include <Pattern, SourceLocation, ElementAnnotationBuilderImpl, CommentListImpl, AnnotationsImpl> makeInclude ()
    {
      final IncludeComponent ic = new IncludeComponent ();
      add (ic);
      return new GrammarSectionImpl (ic, ic);
    }

    public void topLevelAnnotation (final ElementAnnotationBuilderImpl ea) throws BuildException
    {
      if (lastComponent == null)
        ea.addTo (subject.getChildElementAnnotations ());
      else
        addAfterAnnotation (lastComponent, ea);
    }

    public void topLevelComment (final CommentListImpl comments) throws BuildException
    {
      if (comments != null)
      {
        if (lastComponent == null)
          subject.getChildElementAnnotations ().addAll (comments.list);
        else
          addAfterComment (lastComponent, comments);
      }
    }

    private void add (final Component c)
    {
      components.add (c);
      lastComponent = c;
    }

    public void endDiv (final SourceLocation loc, final AnnotationsImpl anno) throws BuildException
    {
      finishAnnotated (subject, loc, anno);
    }

    public void endInclude (final String href,
                            final String base,
                            final String ns,
                            final SourceLocation loc,
                            final AnnotationsImpl anno) throws BuildException, IllegalSchemaException
    {
      final IncludeComponent ic = (IncludeComponent) subject;
      final SubParseable <Pattern, NameClass, SourceLocation, ElementAnnotationBuilderImpl, CommentListImpl, AnnotationsImpl> subParseable = subParser.createSubParseable (href,
                                                                                                                                                                           base);
      final String uri = subParseable.getUri ();
      ic.setUri (uri);
      ic.setBaseUri (base);
      ic.setHref (href);
      ic.setNs (mapInheritNs (ns));
      finishAnnotated (ic, loc, anno);
      if (schemas.get (uri) == null)
      {
        schemas.put (uri, new SchemaDocument (null)); // avoid possibility of
                                                      // infinite loop
        final GrammarPattern g = new GrammarPattern ();
        try
        {
          final Pattern pattern = subParseable.parseAsInclude (SchemaBuilderImpl.this, new GrammarSectionImpl (g, g));
          schemas.put (uri, new SchemaDocument (pattern));
        }
        catch (final IllegalSchemaException e)
        {
          schemas.remove (uri);
          hadError = true;
          throw e;
        }
      }
    }

    public Pattern endGrammar (final SourceLocation loc, final AnnotationsImpl anno) throws BuildException
    {
      finishAnnotated (subject, loc, anno);
      return (Pattern) subject;
    }

    public Pattern endIncludedGrammar (final SourceLocation loc, final AnnotationsImpl anno) throws BuildException
    {
      finishAnnotated (subject, loc, anno);
      return (Pattern) subject;
    }
  }

  public Grammar <Pattern, SourceLocation, ElementAnnotationBuilderImpl, CommentListImpl, AnnotationsImpl> makeGrammar (final Scope parent)
  {
    final GrammarPattern g = new GrammarPattern ();
    return new GrammarSectionImpl (g, g);
  }

  private static NameClass finishNameClass (final NameClass nc, final SourceLocation loc, final AnnotationsImpl anno)
  {
    finishAnnotated (nc, loc, anno);
    return nc;
  }

  private static void finishAnnotated (final Annotated a, final SourceLocation loc, final AnnotationsImpl anno)
  {
    a.setSourceLocation (loc);
    if (anno != null)
      anno.apply (a);
  }

  public NameClass annotateNameClass (final NameClass nc, final AnnotationsImpl anno) throws BuildException
  {
    if (anno != null)
      anno.apply (nc);
    return nc;
  }

  public Pattern annotatePattern (final Pattern p, final AnnotationsImpl anno) throws BuildException
  {
    if (anno != null)
      anno.apply (p);
    return p;
  }

  public Pattern annotateAfterPattern (final Pattern p, final ElementAnnotationBuilderImpl e) throws BuildException
  {
    addAfterAnnotation (p, e);
    return p;
  }

  public NameClass annotateAfterNameClass (final NameClass nc, final ElementAnnotationBuilderImpl e) throws BuildException
  {
    addAfterAnnotation (nc, e);
    return nc;
  }

  static private void addAfterAnnotation (final Annotated a, final ElementAnnotationBuilderImpl e)
  {
    e.addTo (a.getFollowingElementAnnotations ());
  }

  public Pattern commentAfterPattern (final Pattern p, final CommentListImpl comments) throws BuildException
  {
    addAfterComment (p, comments);
    return p;
  }

  public NameClass commentAfterNameClass (final NameClass nc, final CommentListImpl comments) throws BuildException
  {
    addAfterComment (nc, comments);
    return nc;
  }

  static private void addAfterComment (final Annotated a, final CommentList comments)
  {
    if (comments != null)
      a.getFollowingElementAnnotations ().addAll (((CommentListImpl) comments).list);
  }

  public SourceLocation makeLocation (final String systemId, final int lineNumber, final int columnNumber)
  {
    return new SourceLocation (systemId, lineNumber, columnNumber);
  }

  static class TrimmingCommentListImpl extends CommentListImpl
  {
    @Override
    public void addComment (final String value, final SourceLocation loc) throws BuildException
    {
      super.addComment (CommentTrimmer.trimComment (value), loc);
    }
  }

  public CommentListImpl makeCommentList ()
  {
    if (commentsNeedTrimming)
      return new TrimmingCommentListImpl ();
    else
      return new CommentListImpl ();
  }

  private class DataPatternBuilderImpl implements
                                      DataPatternBuilder <Pattern, SourceLocation, ElementAnnotationBuilderImpl, CommentListImpl, AnnotationsImpl>
  {
    private final DataPattern p;
    private DatatypeBuilder dtb = null;

    DataPatternBuilderImpl (final DataPattern p) throws BuildException
    {
      this.p = p;
      final DatatypeLibrary dl = dlf.createDatatypeLibrary (p.getDatatypeLibrary ());
      if (dl != null)
      {
        try
        {
          dtb = dl.createDatatypeBuilder (p.getType ());
        }
        catch (final DatatypeException e)
        {
          final String datatypeLibrary = p.getDatatypeLibrary ();
          final String type = p.getType ();
          final SourceLocation loc = p.getSourceLocation ();
          final String detail = e.getMessage ();
          if (detail != null)
            error ("unsupported_datatype_detail", datatypeLibrary, type, detail, loc);
          else
            error ("unknown_datatype", datatypeLibrary, type, loc);
        }
      }
    }

    public void addParam (final String name,
                          final String value,
                          final Context context,
                          final String ns,
                          final SourceLocation loc,
                          final AnnotationsImpl anno) throws BuildException
    {
      final Param param = new Param (name, value);
      param.setContext (new NamespaceContextImpl (context));
      finishAnnotated (param, loc, anno);
      p.getParams ().add (param);
      if (dtb != null)
      {
        try
        {
          dtb.addParameter (name, value, context);
        }
        catch (final DatatypeException e)
        {
          diagnoseDatatypeException ("invalid_param_detail", "invalid_param", e, loc);
        }
      }
    }

    public void annotation (final ElementAnnotationBuilderImpl ea)
    {
      final List <Param> params = p.getParams ();
      ea.addTo (params.isEmpty () ? p.getChildElementAnnotations ()
                                 : (params.get (params.size () - 1)).getFollowingElementAnnotations ());
    }

    public Pattern makePattern (final SourceLocation loc, final AnnotationsImpl anno) throws BuildException
    {
      if (dtb != null)
      {
        try
        {
          dtb.createDatatype ();
        }
        catch (final DatatypeException e)
        {
          diagnoseDatatypeException ("invalid_params_detail", "invalid_params", e, loc);
        }
      }
      return finishPattern (p, loc, anno);
    }

    public Pattern makePattern (final Pattern except, final SourceLocation loc, final AnnotationsImpl anno) throws BuildException
    {
      p.setExcept (except);
      return finishPattern (p, loc, anno);
    }
  }

  public DataPatternBuilder <Pattern, SourceLocation, ElementAnnotationBuilderImpl, CommentListImpl, AnnotationsImpl> makeDataPatternBuilder (final String datatypeLibrary,
                                                                                                                                              final String type,
                                                                                                                                              final SourceLocation loc) throws BuildException
  {
    final DataPattern pattern = new DataPattern (datatypeLibrary, type);
    pattern.setSourceLocation (loc);
    return new DataPatternBuilderImpl (pattern);
  }

  public Pattern makeErrorPattern ()
  {
    return null;
  }

  public NameClass makeErrorNameClass ()
  {
    return null;
  }

  public AnnotationsImpl makeAnnotations (final CommentListImpl comments, final Context context)
  {
    return new AnnotationsImpl (comments, context);
  }

  public ElementAnnotationBuilder <SourceLocation, ElementAnnotationBuilderImpl, CommentListImpl> makeElementAnnotationBuilder (final String ns,
                                                                                                                                final String localName,
                                                                                                                                final String prefix,
                                                                                                                                final SourceLocation loc,
                                                                                                                                final CommentListImpl comments,
                                                                                                                                final Context context)
  {
    final ElementAnnotation element = new ElementAnnotation (ns, localName);
    element.setPrefix (prefix);
    element.setSourceLocation (loc);
    element.setContext (new NamespaceContextImpl (context));
    return new ElementAnnotationBuilderImpl (comments, element);
  }

  public boolean usesComments ()
  {
    return true;
  }

  private static Combine mapCombine (final GrammarSection.Combine combine)
  {
    if (combine == null)
      return null;
    return combine == GrammarSection.COMBINE_CHOICE ? Combine.CHOICE : Combine.INTERLEAVE;
  }

  private static String mapInheritNs (final String ns)
  {
    // noop since we represent INHERIT_NS by the same object
    return ns;
  }

  private void parse (final Parseable <Pattern, NameClass, SourceLocation, ElementAnnotationBuilderImpl, CommentListImpl, AnnotationsImpl> parseable,
                      final String uri) throws IllegalSchemaException
  {
    schemas.put (uri, new SchemaDocument (parseable.parse (this, new ScopeImpl ())));
  }

  static SchemaCollection parse (final Parseable <Pattern, NameClass, SourceLocation, ElementAnnotationBuilderImpl, CommentListImpl, AnnotationsImpl> parseable,
                                 final String uri,
                                 final ErrorHandler eh,
                                 final DatatypeLibraryFactory dlf,
                                 final boolean commentsNeedTrimming) throws IllegalSchemaException,
                                                                    IOException,
                                                                    SAXException
  {
    try
    {
      final SchemaCollection sc = new SchemaCollection ();
      final SchemaBuilderImpl sb = new SchemaBuilderImpl (parseable,
                                                          eh,
                                                          sc.getSchemaDocumentMap (),
                                                          dlf,
                                                          commentsNeedTrimming);
      sc.setMainUri (uri);
      sb.parse (parseable, uri);
      if (sb.hadError)
        throw new IllegalSchemaException ();
      return sc;
    }
    catch (final BuildException e)
    {
      final Throwable t = e.getCause ();
      if (t instanceof IOException)
        throw (IOException) t;
      if (t instanceof RuntimeException)
        throw (RuntimeException) t;
      if (t instanceof SAXException)
        throw (SAXException) t;
      if (t instanceof Exception)
        throw new SAXException ((Exception) t);
      throw new SAXException (t.getClass ().getName () + " thrown");
    }
  }

  private void error (final SAXParseException message) throws BuildException
  {
    hadError = true;
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

  private void diagnoseDatatypeException (final String detailKey,
                                          final String noDetailKey,
                                          final DatatypeException e,
                                          final SourceLocation loc) throws BuildException
  {
    final String detail = e.getMessage ();
    if (detail != null)
      error (detailKey, detail, loc);
    else
      error (noDetailKey, loc);
  }

  static private Locator makeLocator (final SourceLocation loc)
  {
    return new Locator ()
    {
      public String getPublicId ()
      {
        return null;
      }

      public int getColumnNumber ()
      {
        if (loc == null)
          return -1;
        return loc.getColumnNumber ();
      }

      public String getSystemId ()
      {
        if (loc == null)
          return null;
        return loc.getUri ();
      }

      public int getLineNumber ()
      {
        if (loc == null)
          return -1;
        return loc.getLineNumber ();
      }
    };
  }

  private void error (final String key, final SourceLocation loc) throws BuildException
  {
    error (new SAXParseException (localizer.message (key), makeLocator (loc)));
  }

  private void error (final String key, final String arg, final SourceLocation loc) throws BuildException
  {
    error (new SAXParseException (localizer.message (key, arg), makeLocator (loc)));
  }

  private void error (final String key, final String arg1, final String arg2, final SourceLocation loc) throws BuildException
  {
    error (new SAXParseException (localizer.message (key, arg1, arg2), makeLocator (loc)));
  }

  private void error (final String key,
                      final String arg1,
                      final String arg2,
                      final String arg3,
                      final SourceLocation loc) throws BuildException
  {
    error (new SAXParseException (localizer.message (key, new Object [] { arg1, arg2, arg3 }), makeLocator (loc)));
  }
}
