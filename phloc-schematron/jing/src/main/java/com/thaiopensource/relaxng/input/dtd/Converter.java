package com.thaiopensource.relaxng.input.dtd;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.thaiopensource.relaxng.edit.Annotated;
import com.thaiopensource.relaxng.edit.AnyNameNameClass;
import com.thaiopensource.relaxng.edit.AttributeAnnotation;
import com.thaiopensource.relaxng.edit.AttributePattern;
import com.thaiopensource.relaxng.edit.ChoicePattern;
import com.thaiopensource.relaxng.edit.Combine;
import com.thaiopensource.relaxng.edit.Comment;
import com.thaiopensource.relaxng.edit.Component;
import com.thaiopensource.relaxng.edit.CompositePattern;
import com.thaiopensource.relaxng.edit.DataPattern;
import com.thaiopensource.relaxng.edit.DefineComponent;
import com.thaiopensource.relaxng.edit.ElementPattern;
import com.thaiopensource.relaxng.edit.EmptyPattern;
import com.thaiopensource.relaxng.edit.GrammarPattern;
import com.thaiopensource.relaxng.edit.GroupPattern;
import com.thaiopensource.relaxng.edit.IncludeComponent;
import com.thaiopensource.relaxng.edit.NameClass;
import com.thaiopensource.relaxng.edit.NameNameClass;
import com.thaiopensource.relaxng.edit.NotAllowedPattern;
import com.thaiopensource.relaxng.edit.OneOrMorePattern;
import com.thaiopensource.relaxng.edit.OptionalPattern;
import com.thaiopensource.relaxng.edit.Pattern;
import com.thaiopensource.relaxng.edit.RefPattern;
import com.thaiopensource.relaxng.edit.SchemaCollection;
import com.thaiopensource.relaxng.edit.SchemaDocument;
import com.thaiopensource.relaxng.edit.TextPattern;
import com.thaiopensource.relaxng.edit.ValuePattern;
import com.thaiopensource.relaxng.edit.ZeroOrMorePattern;
import com.thaiopensource.relaxng.input.CommentTrimmer;
import com.thaiopensource.relaxng.output.common.ErrorReporter;
import com.thaiopensource.xml.dtd.om.AttributeDefault;
import com.thaiopensource.xml.dtd.om.AttributeGroup;
import com.thaiopensource.xml.dtd.om.AttributeGroupMember;
import com.thaiopensource.xml.dtd.om.AttributeGroupVisitor;
import com.thaiopensource.xml.dtd.om.Datatype;
import com.thaiopensource.xml.dtd.om.DatatypeVisitor;
import com.thaiopensource.xml.dtd.om.Def;
import com.thaiopensource.xml.dtd.om.Dtd;
import com.thaiopensource.xml.dtd.om.EnumGroup;
import com.thaiopensource.xml.dtd.om.EnumGroupVisitor;
import com.thaiopensource.xml.dtd.om.Flag;
import com.thaiopensource.xml.dtd.om.ModelGroup;
import com.thaiopensource.xml.dtd.om.ModelGroupVisitor;
import com.thaiopensource.xml.dtd.om.NameSpec;
import com.thaiopensource.xml.dtd.om.TokenizedDatatype;
import com.thaiopensource.xml.dtd.om.TopLevel;
import com.thaiopensource.xml.dtd.om.TopLevelVisitor;
import com.thaiopensource.xml.em.ExternalId;
import com.thaiopensource.xml.util.WellKnownNamespaces;

public class Converter
{
  static class Options
  {
    boolean inlineAttlistDecls;
    boolean generateStart = true;
    boolean strictAny;
    String elementDeclPattern;
    String attlistDeclPattern;
    String colonReplacement;
    String anyName;
    String annotationPrefix;
    String defaultNamespace;
    final Map <String, String> prefixMap = new HashMap <String, String> ();
  }

  private final Dtd dtd;
  private final ErrorReporter er;
  private final SchemaCollection sc = new SchemaCollection ();
  private final Options options;
  /**
   * true if any uses of ANY have been encountered in the DTD
   */
  private boolean hadAny = false;
  /**
   * true if any default values have been encountered in the DTD
   */
  private boolean hadDefaultValue = false;
  /**
   * Maps each element name to an Integer containing a set of flags.
   */
  private final Map <String, Integer> elementNameTable = new HashMap <String, Integer> ();
  /**
   * Maps each element name to a List of attribute groups of each attlist
   * declaration.
   */
  private final Map <String, List <AttributeGroup>> attlistDeclTable = new HashMap <String, List <AttributeGroup>> ();
  /**
   * Set of strings representing names for which there are definitions in the
   * DTD.
   */
  private final Set <String> definedNames = new HashSet <String> ();
  /**
   * Maps prefixes to namespace URIs.
   */
  private final Map <String, String> prefixTable = new HashMap <String, String> ();

  /**
   * Maps a string representing an element name to the set of names of
   * attributes that have been declated for that element.
   */
  private final Map <String, Set <String>> attributeNamesTable = new HashMap <String, Set <String>> ();
  /**
   * Contains the set of attribute names that have already been output in the
   * current scope.
   */
  private Set <String> attributeNames = null;
  private String defaultNamespace = null;
  private String annotationPrefix = null;

  // These variables control the names use for definitions.
  private String colonReplacement = null;
  private String elementDeclPattern = null;
  private String attlistDeclPattern = null;
  private String anyName = null;

  /**
   * Flags for element names used in elementDeclTable.
   */
  private static final int ELEMENT_DECL = 01;
  private static final int ATTLIST_DECL = 02;
  private static final int ELEMENT_REF = 04;

  /**
   * Characters that will be considered for use as a replacement for colon in a
   * QName. Also used as separators in constructing names of definitions
   * corresponding to element declarations and attlist declarations,
   */
  private static final String SEPARATORS = ".-_";

  // # is the category; % is the name in the category

  private static final String DEFAULT_PATTERN = "#.%";

  private final String [] ELEMENT_KEYWORDS = { "element", "elem", "e" };

  private final String [] ATTLIST_KEYWORDS = { "attlist", "attributes", "attribs", "atts", "a" };

  private final String [] ANY_KEYWORDS = { "any", "ANY", "anyElement" };

  private static abstract class VisitorBase implements TopLevelVisitor
  {
    public void processingInstruction (final String target, final String value) throws Exception
    {}

    public void comment (final String value) throws Exception
    {}

    public void flagDef (final String name, final Flag flag) throws Exception
    {}

    public void includedSection (final Flag flag, final TopLevel [] contents) throws Exception
    {
      for (final TopLevel content : contents)
        content.accept (this);
    }

    public void ignoredSection (final Flag flag, final String contents) throws Exception
    {}

    public void internalEntityDecl (final String name, final String value) throws Exception
    {}

    public void externalEntityDecl (final String name, final ExternalId externalId) throws Exception
    {}

    public void notationDecl (final String name, final ExternalId externalId) throws Exception
    {}

    public void nameSpecDef (final String name, final NameSpec nameSpec) throws Exception
    {}

    public void overriddenDef (final Def def, final boolean isDuplicate) throws Exception
    {}

    public void externalIdDef (final String name, final ExternalId externalId) throws Exception
    {}

    public void externalIdRef (final String name,
                               final ExternalId externalId,
                               final String uri,
                               final String encoding,
                               final TopLevel [] contents) throws Exception
    {
      for (final TopLevel content : contents)
        content.accept (this);
    }

    public void paramDef (final String name, final String value) throws Exception
    {}

    public void attributeDefaultDef (final String name, final AttributeDefault ad) throws Exception
    {}
  }

  private class Analyzer extends VisitorBase implements ModelGroupVisitor, AttributeGroupVisitor
  {
    public void elementDecl (final NameSpec nameSpec, final ModelGroup modelGroup) throws Exception
    {
      noteElementName (nameSpec.getValue (), ELEMENT_DECL);
      modelGroup.accept (this);
    }

    public void attlistDecl (final NameSpec nameSpec, final AttributeGroup attributeGroup) throws Exception
    {
      noteElementName (nameSpec.getValue (), ATTLIST_DECL);
      noteAttlist (nameSpec.getValue (), attributeGroup);
      attributeGroup.accept (this);
    }

    public void modelGroupDef (final String name, final ModelGroup modelGroup) throws Exception
    {
      noteDef (name);
      modelGroup.accept (this);
    }

    public void attributeGroupDef (final String name, final AttributeGroup attributeGroup) throws Exception
    {
      noteDef (name);
      attributeGroup.accept (this);
    }

    public void enumGroupDef (final String name, final EnumGroup enumGroup)
    {
      noteDef (name);
    }

    public void datatypeDef (final String name, final Datatype datatype)
    {
      noteDef (name);
    }

    public void choice (final ModelGroup [] members) throws Exception
    {
      for (final ModelGroup member : members)
        member.accept (this);
    }

    public void sequence (final ModelGroup [] members) throws Exception
    {
      for (final ModelGroup member : members)
        member.accept (this);
    }

    public void oneOrMore (final ModelGroup member) throws Exception
    {
      member.accept (this);
    }

    public void zeroOrMore (final ModelGroup member) throws Exception
    {
      member.accept (this);
    }

    public void optional (final ModelGroup member) throws Exception
    {
      member.accept (this);
    }

    public void modelGroupRef (final String name, final ModelGroup modelGroup)
    {}

    public void elementRef (final NameSpec name)
    {
      noteElementName (name.getValue (), ELEMENT_REF);
    }

    public void pcdata ()
    {}

    public void any ()
    {
      hadAny = true;
    }

    public void attribute (final NameSpec nameSpec, final Datatype datatype, final AttributeDefault attributeDefault)
    {
      noteAttribute (nameSpec.getValue (), attributeDefault.getDefaultValue ());
    }

    public void attributeGroupRef (final String name, final AttributeGroup attributeGroup)
    {}

  }

  private class ComponentOutput extends VisitorBase
  {
    private final List <Component> components;
    private final Annotated grammar;
    private List <Comment> comments = null;

    ComponentOutput (final GrammarPattern grammar)
    {
      components = grammar.getComponents ();
      this.grammar = grammar;
    }

    void finish ()
    {
      if (comments != null)
        grammar.getFollowingElementAnnotations ().addAll (comments);
    }

    private void addComponent (final Component c)
    {
      if (comments != null)
      {
        if (components.isEmpty ())
          grammar.getLeadingComments ().addAll (comments);
        else
          c.getLeadingComments ().addAll (comments);
        comments = null;
      }
      components.add (c);
    }

    public void elementDecl (final NameSpec nameSpec, final ModelGroup modelGroup) throws Exception
    {
      final GroupPattern gp = new GroupPattern ();
      if (options.inlineAttlistDecls)
      {
        final List <AttributeGroup> groups = attlistDeclTable.get (nameSpec.getValue ());
        if (groups != null)
        {
          attributeNames = new HashSet <String> ();
          final AttributeGroupVisitor agv = new AttributeGroupOutput (gp);
          for (final AttributeGroup group : groups)
            group.accept (agv);
        }
      }
      else
        gp.getChildren ().add (ref (attlistDeclName (nameSpec.getValue ())));
      Pattern pattern = convert (modelGroup);
      if (gp.getChildren ().size () > 0)
      {
        if (pattern instanceof GroupPattern)
          gp.getChildren ().addAll (((GroupPattern) pattern).getChildren ());
        else
          gp.getChildren ().add (pattern);
        pattern = gp;
      }
      addComponent (new DefineComponent (elementDeclName (nameSpec.getValue ()),
                                         new ElementPattern (convertQName (nameSpec.getValue (), true), pattern)));
      if (!options.inlineAttlistDecls && (nameFlags (nameSpec.getValue ()) & ATTLIST_DECL) == 0)
      {
        final DefineComponent dc = new DefineComponent (attlistDeclName (nameSpec.getValue ()), new EmptyPattern ());
        dc.setCombine (Combine.INTERLEAVE);
        addComponent (dc);
      }
      if (anyName != null && options.strictAny)
      {
        final DefineComponent dc = new DefineComponent (anyName, ref (elementDeclName (nameSpec.getValue ())));
        dc.setCombine (Combine.CHOICE);
        addComponent (dc);
      }
    }

    public void attlistDecl (final NameSpec nameSpec, final AttributeGroup attributeGroup) throws Exception
    {
      if (options.inlineAttlistDecls)
        return;
      final String name = nameSpec.getValue ();
      attributeNames = attributeNamesTable.get (name);
      if (attributeNames == null)
      {
        attributeNames = new HashSet <String> ();
        attributeNamesTable.put (name, attributeNames);
      }
      final Pattern pattern = convert (attributeGroup);
      if (pattern instanceof EmptyPattern)
      {
        // Only keep an empty definition if this is the first attlist for this
        // element,
        // and all attlists are also empty. In this case, if we didn't keep the
        // definition, we would have no definition for the attlist.
        final List <AttributeGroup> decls = attlistDeclTable.get (name);
        if (decls.get (0) != attributeGroup)
          return;
        attributeNames = new HashSet <String> ();
        for (int i = 1, len = decls.size (); i < len; i++)
          if (!(convert (decls.get (i)) instanceof EmptyPattern))
            return;
      }
      final DefineComponent dc = new DefineComponent (attlistDeclName (name), pattern);
      dc.setCombine (Combine.INTERLEAVE);
      addComponent (dc);
    }

    public void modelGroupDef (final String name, final ModelGroup modelGroup) throws Exception
    {
      addComponent (new DefineComponent (name, convert (modelGroup)));
    }

    public void attributeGroupDef (final String name, final AttributeGroup attributeGroup) throws Exception
    {
      // This takes care of duplicates within the group
      attributeNames = new HashSet <String> ();
      Pattern pattern;
      final AttributeGroupMember [] members = attributeGroup.getMembers ();
      final GroupPattern group = new GroupPattern ();
      final AttributeGroupVisitor agv = new AttributeGroupOutput (group);
      for (final AttributeGroupMember member : members)
        member.accept (agv);
      switch (group.getChildren ().size ())
      {
        case 0:
          pattern = new EmptyPattern ();
          break;
        case 1:
          pattern = group.getChildren ().get (0);
          break;
        default:
          pattern = group;
          break;
      }
      addComponent (new DefineComponent (name, pattern));
    }

    public void enumGroupDef (final String name, final EnumGroup enumGroup) throws Exception
    {
      final ChoicePattern choice = new ChoicePattern ();
      enumGroup.accept (new EnumGroupOutput (choice));
      Pattern pattern;
      switch (choice.getChildren ().size ())
      {
        case 0:
          pattern = new NotAllowedPattern ();
          break;
        case 1:
          pattern = choice.getChildren ().get (0);
          break;
        default:
          pattern = choice;
          break;
      }
      addComponent (new DefineComponent (name, pattern));
    }

    public void datatypeDef (final String name, final Datatype datatype) throws Exception
    {
      addComponent (new DefineComponent (name, convert (datatype)));
    }

    @Override
    public void comment (final String value)
    {
      if (comments == null)
        comments = new Vector <Comment> ();
      comments.add (new Comment (CommentTrimmer.trimComment (value)));
    }

    @Override
    public void externalIdRef (final String name,
                               final ExternalId externalId,
                               final String uri,
                               final String encoding,
                               final TopLevel [] contents) throws Exception
    {
      if (uri == null)
      {
        // I don't think this can happen
        super.externalIdRef (name, externalId, uri, encoding, contents);
        return;
      }
      final SignificanceDetector sd = new SignificanceDetector ();
      try
      {
        sd.externalIdRef (name, externalId, uri, encoding, contents);
        if (!sd.significant)
          return;
      }
      catch (final Exception e)
      {
        throw (RuntimeException) e;
      }
      if (sc.getSchemaDocumentMap ().get (uri) != null)
      {
        // I don't think this can happen because the second and subsequent
        // inclusions
        // will never pass the SignificanceDetector, but just in case
        super.externalIdRef (name, externalId, uri, encoding, contents);
        return;
      }
      final IncludeComponent ic = new IncludeComponent (uri);
      ic.setNs (defaultNamespace);
      addComponent (ic);
      final GrammarPattern included = new GrammarPattern ();
      final ComponentOutput co = new ComponentOutput (included);
      for (final TopLevel content : contents)
        content.accept (co);
      co.finish ();
      sc.getSchemaDocumentMap ().put (uri, new SchemaDocument (included, encoding));
    }

  }

  private class AttributeGroupOutput implements AttributeGroupVisitor
  {
    final List <Pattern> group;

    AttributeGroupOutput (final GroupPattern gp)
    {
      group = gp.getChildren ();
    }

    public void attribute (final NameSpec nameSpec, final Datatype datatype, final AttributeDefault attributeDefault) throws Exception
    {
      final String name = nameSpec.getValue ();
      if (attributeNames.contains (name))
        return;
      attributeNames.add (name);
      if (name.equals ("xmlns") || name.startsWith ("xmlns:"))
        return;
      final String dv = attributeDefault.getDefaultValue ();
      final String fv = attributeDefault.getFixedValue ();
      Pattern dt;
      if (fv != null)
      {
        final String [] typeName = valueType (datatype);
        dt = new ValuePattern (typeName[0], typeName[1], fv);
      }
      else
        if (datatype.getType () != Datatype.CDATA)
          dt = convert (datatype);
        else
          dt = new TextPattern ();
      final AttributePattern pattern = new AttributePattern (convertQName (name, false), dt);
      if (dv != null)
      {
        final AttributeAnnotation anno = new AttributeAnnotation (WellKnownNamespaces.RELAX_NG_COMPATIBILITY_ANNOTATIONS,
                                                                  "defaultValue",
                                                                  dv);
        anno.setPrefix (annotationPrefix);
        pattern.getAttributeAnnotations ().add (anno);
      }
      if (!attributeDefault.isRequired ())
        group.add (new OptionalPattern (pattern));
      else
        group.add (pattern);
    }

    public void attributeGroupRef (final String name, final AttributeGroup attributeGroup) throws Exception
    {
      final DuplicateAttributeDetector detector = new DuplicateAttributeDetector ();
      attributeGroup.accept (detector);
      if (detector.containsDuplicate)
        attributeGroup.accept (this);
      else
      {
        group.add (ref (name));
        attributeNames.addAll (detector.names);
      }
    }

  }

  private class DatatypeOutput implements DatatypeVisitor
  {
    Pattern pattern;

    public void cdataDatatype ()
    {
      pattern = new DataPattern ("", "string");
    }

    public void tokenizedDatatype (final String typeName)
    {
      pattern = new DataPattern (WellKnownNamespaces.XML_SCHEMA_DATATYPES, typeName);
    }

    public void enumDatatype (final EnumGroup enumGroup) throws Exception
    {
      if (enumGroup.getMembers ().length == 0)
        pattern = new NotAllowedPattern ();
      else
      {
        final ChoicePattern tem = new ChoicePattern ();
        pattern = tem;
        enumGroup.accept (new EnumGroupOutput (tem));
      }
    }

    public void notationDatatype (final EnumGroup enumGroup) throws Exception
    {
      enumDatatype (enumGroup);
    }

    public void datatypeRef (final String name, final Datatype datatype)
    {
      pattern = ref (name);
    }
  }

  private class EnumGroupOutput implements EnumGroupVisitor
  {
    final private List <Pattern> list;

    EnumGroupOutput (final ChoicePattern choice)
    {
      list = choice.getChildren ();
    }

    public void enumValue (final String value)
    {
      list.add (new ValuePattern ("", "token", value));
    }

    public void enumGroupRef (final String name, final EnumGroup enumGroup)
    {
      list.add (ref (name));
    }
  }

  private class ModelGroupOutput implements ModelGroupVisitor
  {
    private Pattern pattern;

    public void choice (final ModelGroup [] members) throws Exception
    {
      if (members.length == 0)
        pattern = new NotAllowedPattern ();
      else
        if (members.length == 1)
          members[0].accept (this);
        else
        {
          final ChoicePattern tem = new ChoicePattern ();
          pattern = tem;
          final List <Pattern> children = tem.getChildren ();
          for (final ModelGroup member : members)
            children.add (convert (member));
        }
    }

    public void sequence (final ModelGroup [] members) throws Exception
    {
      if (members.length == 0)
        pattern = new EmptyPattern ();
      else
        if (members.length == 1)
          members[0].accept (this);
        else
        {
          final GroupPattern tem = new GroupPattern ();
          pattern = tem;
          final List <Pattern> children = tem.getChildren ();
          for (final ModelGroup member : members)
            children.add (convert (member));
        }
    }

    public void oneOrMore (final ModelGroup member) throws Exception
    {
      pattern = new OneOrMorePattern (convert (member));
    }

    public void zeroOrMore (final ModelGroup member) throws Exception
    {
      pattern = new ZeroOrMorePattern (convert (member));
    }

    public void optional (final ModelGroup member) throws Exception
    {
      pattern = new OptionalPattern (convert (member));
    }

    public void modelGroupRef (final String name, final ModelGroup modelGroup)
    {
      pattern = ref (name);
    }

    public void elementRef (final NameSpec name)
    {
      pattern = ref (elementDeclName (name.getValue ()));
    }

    public void pcdata ()
    {
      pattern = new TextPattern ();
    }

    public void any ()
    {
      pattern = ref (anyName);
      if (options.strictAny)
        pattern = new ZeroOrMorePattern (pattern);
    }

  }

  private class DuplicateAttributeDetector implements AttributeGroupVisitor
  {
    private boolean containsDuplicate = false;
    private final List <String> names = new Vector <String> ();

    public void attribute (final NameSpec nameSpec, final Datatype datatype, final AttributeDefault attributeDefault)
    {
      final String name = nameSpec.getValue ();
      if (attributeNames.contains (name))
        containsDuplicate = true;
      names.add (name);
    }

    public void attributeGroupRef (final String name, final AttributeGroup attributeGroup) throws Exception
    {
      attributeGroup.accept (this);
    }

  }

  private class SignificanceDetector extends VisitorBase
  {
    boolean significant = false;

    public void elementDecl (final NameSpec nameSpec, final ModelGroup modelGroup) throws Exception
    {
      significant = true;
    }

    public void attlistDecl (final NameSpec nameSpec, final AttributeGroup attributeGroup) throws Exception
    {
      significant = true;
    }

    public void modelGroupDef (final String name, final ModelGroup modelGroup) throws Exception
    {
      significant = true;
    }

    public void attributeGroupDef (final String name, final AttributeGroup attributeGroup) throws Exception
    {
      significant = true;
    }

    public void enumGroupDef (final String name, final EnumGroup enumGroup)
    {
      significant = true;
    }

    public void datatypeDef (final String name, final Datatype datatype)
    {
      significant = true;
    }
  }

  public Converter (final Dtd dtd, final ErrorReporter er, final Options options)
  {
    this.dtd = dtd;
    this.er = er;
    this.options = options;
  }

  public SchemaCollection convert ()
  {
    try
    {
      dtd.accept (new Analyzer ());
      chooseNames ();
      final GrammarPattern grammar = new GrammarPattern ();
      sc.setMainUri (dtd.getUri ());
      sc.getSchemaDocumentMap ().put (dtd.getUri (), new SchemaDocument (grammar, dtd.getEncoding ()));
      final ComponentOutput co = new ComponentOutput (grammar);
      dtd.accept (co);
      outputUndefinedElements (grammar.getComponents ());
      if (options.generateStart)
        outputStart (grammar.getComponents ());
      outputAny (grammar.getComponents ());
      co.finish ();
      return sc;
    }
    catch (final Exception e)
    {
      throw (RuntimeException) e;
    }
  }

  private void chooseNames ()
  {
    chooseAny ();
    chooseColonReplacement ();
    chooseDeclPatterns ();
    choosePrefixes ();
    chooseAnnotationPrefix ();
  }

  private void chooseAny ()
  {
    if (!hadAny)
      return;
    if (options.anyName != null)
    {
      if (!definedNames.contains (options.anyName))
      {
        anyName = options.anyName;
        definedNames.add (anyName);
        return;
      }
      warning ("cannot_use_any_name");
    }
    for (int n = 0;; n++)
    {
      for (final String element : ANY_KEYWORDS)
      {
        anyName = repeatChar ('_', n) + element;
        if (!definedNames.contains (anyName))
        {
          definedNames.add (anyName);
          return;
        }
      }
    }
  }

  private void choosePrefixes ()
  {
    if (options.defaultNamespace != null)
    {
      if (defaultNamespace != null && !defaultNamespace.equals (options.defaultNamespace))
        warning ("default_namespace_conflict");
      defaultNamespace = options.defaultNamespace;
    }
    else
      if (defaultNamespace == null)
        defaultNamespace = NameClass.INHERIT_NS;
    for (final Map.Entry <String, String> entry : options.prefixMap.entrySet ())
    {
      final String prefix = entry.getKey ();
      final String ns = entry.getValue ();
      final String s = prefixTable.get (prefix);
      if (s == null)
        warning ("irrelevant_prefix", prefix);
      else
      {
        if (!s.equals ("") && !s.equals (ns))
          warning ("prefix_conflict", prefix);
        prefixTable.put (prefix, ns);
      }
    }
  }

  private void chooseAnnotationPrefix ()
  {
    if (!hadDefaultValue)
      return;
    if (options.annotationPrefix != null)
    {
      if (prefixTable.get (options.annotationPrefix) == null)
      {
        annotationPrefix = options.annotationPrefix;
        return;
      }
      warning ("cannot_use_annotation_prefix");
    }
    for (int n = 0;; n++)
    {
      annotationPrefix = repeatChar ('_', n) + "a";
      if (prefixTable.get (annotationPrefix) == null)
        return;
    }
  }

  private void chooseColonReplacement ()
  {
    if (options.colonReplacement != null)
    {
      colonReplacement = options.colonReplacement;
      if (colonReplacementOk ())
        return;
      warning ("cannot_use_colon_replacement");
      colonReplacement = null;
    }
    if (colonReplacementOk ())
      return;
    for (int n = 1;; n++)
    {
      for (int i = 0; i < SEPARATORS.length (); i++)
      {
        colonReplacement = repeatChar (SEPARATORS.charAt (i), n);
        if (colonReplacementOk ())
          return;
      }
    }
  }

  private boolean colonReplacementOk ()
  {
    final Set <String> names = new HashSet <String> ();
    for (final String s : elementNameTable.keySet ())
    {
      final String name = mungeQName (s);
      if (names.contains (name))
        return false;
      names.add (name);
    }
    return true;
  }

  private void chooseDeclPatterns ()
  {
    if (options.elementDeclPattern != null)
    {
      if (patternOk (options.elementDeclPattern, null))
        elementDeclPattern = options.elementDeclPattern;
      else
        warning ("cannot_use_element_decl_pattern");
    }
    if (options.attlistDeclPattern != null)
    {
      if (patternOk (options.attlistDeclPattern, elementDeclPattern))
        attlistDeclPattern = options.attlistDeclPattern;
      else
        warning ("cannot_use_attlist_decl_pattern");
    }
    if (elementDeclPattern != null && attlistDeclPattern != null)
      return;
    // XXX Try to match length and case of best prefix
    final String pattern = namingPattern ();
    if (elementDeclPattern == null)
    {
      if (patternOk ("%", attlistDeclPattern))
        elementDeclPattern = "%";
      else
        elementDeclPattern = choosePattern (pattern, ELEMENT_KEYWORDS, attlistDeclPattern);
    }
    if (attlistDeclPattern == null)
      attlistDeclPattern = choosePattern (pattern, ATTLIST_KEYWORDS, elementDeclPattern);
  }

  private String choosePattern (String metaPattern, final String [] keywords, final String otherPattern)
  {
    for (;;)
    {
      for (final String keyword : keywords)
      {
        final String pattern = substitute (metaPattern, '#', keyword);
        if (patternOk (pattern, otherPattern))
          return pattern;
      }
      // add another separator
      metaPattern = (metaPattern.substring (0, 1) + metaPattern.substring (1, 2) + metaPattern.substring (1, 2) + metaPattern.substring (2));
    }
  }

  private String namingPattern ()
  {
    final Map <String, Integer> patternTable = new HashMap <String, Integer> ();
    for (final String name : definedNames)
    {
      for (int i = 0; i < SEPARATORS.length (); i++)
      {
        final char sep = SEPARATORS.charAt (i);
        int k = name.indexOf (sep);
        if (k > 0)
          inc (patternTable, name.substring (0, k + 1) + "%");
        k = name.lastIndexOf (sep);
        if (k >= 0 && k < name.length () - 1)
          inc (patternTable, "%" + name.substring (k));
      }
    }
    String bestPattern = null;
    int bestCount = 0;
    for (final Map.Entry <String, Integer> entry : patternTable.entrySet ())
    {
      final int count = entry.getValue ().intValue ();
      if (bestPattern == null || count > bestCount)
      {
        bestCount = count;
        bestPattern = entry.getKey ();
      }
    }
    if (bestPattern == null)
      return DEFAULT_PATTERN;
    if (bestPattern.charAt (0) == '%')
      return bestPattern.substring (0, 2) + "#";
    else
      return "#" + bestPattern.substring (bestPattern.length () - 2);
  }

  private static void inc (final Map <String, Integer> table, final String str)
  {
    final Integer n = table.get (str);
    if (n == null)
      table.put (str, Integer.valueOf (1));
    else
      table.put (str, Integer.valueOf (n.intValue () + 1));
  }

  private boolean patternOk (final String pattern, final String otherPattern)
  {
    final Set <String> usedNames = new HashSet <String> ();
    for (final String s : elementNameTable.keySet ())
    {
      final String name = mungeQName (s);
      final String declName = substitute (pattern, '%', name);
      if (definedNames.contains (declName))
        return false;
      if (otherPattern != null)
      {
        final String otherDeclName = substitute (otherPattern, '%', name);
        if (usedNames.contains (declName) || usedNames.contains (otherDeclName) || declName.equals (otherDeclName))
          return false;
        usedNames.add (declName);
        usedNames.add (otherDeclName);
      }
    }
    return true;
  }

  private void noteDef (final String name)
  {
    definedNames.add (name);
  }

  private void noteElementName (final String name, int flags)
  {
    final Integer n = elementNameTable.get (name);
    if (n != null)
    {
      flags |= n.intValue ();
      if (n.intValue () == flags)
        return;
    }
    else
      noteNamePrefix (name);
    elementNameTable.put (name, Integer.valueOf (flags));
  }

  private void noteAttlist (final String name, final AttributeGroup group)
  {
    List <AttributeGroup> groups = attlistDeclTable.get (name);
    if (groups == null)
    {
      groups = new Vector <AttributeGroup> ();
      attlistDeclTable.put (name, groups);
    }
    groups.add (group);
  }

  private void noteAttribute (final String name, final String defaultValue)
  {
    if (name.equals ("xmlns"))
    {
      if (defaultValue != null)
      {
        if (defaultNamespace != null && !defaultNamespace.equals (defaultValue))
          error ("INCONSISTENT_DEFAULT_NAMESPACE");
        else
          defaultNamespace = defaultValue;
      }
    }
    else
      if (name.startsWith ("xmlns:"))
      {
        if (defaultValue != null)
        {
          final String prefix = name.substring (6);
          final String ns = prefixTable.get (prefix);
          if (ns != null && !ns.equals ("") && !ns.equals (defaultValue))
            error ("INCONSISTENT_PREFIX", prefix);
          else
            if (!prefix.equals ("xml"))
              prefixTable.put (prefix, defaultValue);
        }
      }
      else
      {
        if (defaultValue != null)
          hadDefaultValue = true;
        noteNamePrefix (name);
      }
  }

  private void noteNamePrefix (final String name)
  {
    final int i = name.indexOf (':');
    if (i < 0)
      return;
    final String prefix = name.substring (0, i);
    if (prefixTable.get (prefix) == null && !prefix.equals ("xml"))
      prefixTable.put (prefix, "");
  }

  private int nameFlags (final String name)
  {
    final Integer n = elementNameTable.get (name);
    if (n == null)
      return 0;
    return n.intValue ();
  }

  private String elementDeclName (final String name)
  {
    return substitute (elementDeclPattern, '%', mungeQName (name));
  }

  private String attlistDeclName (final String name)
  {
    return substitute (attlistDeclPattern, '%', mungeQName (name));
  }

  private String mungeQName (final String name)
  {
    if (colonReplacement == null)
    {
      final int i = name.indexOf (':');
      if (i < 0)
        return name;
      return name.substring (i + 1);
    }
    return substitute (name, ':', colonReplacement);
  }

  private static String repeatChar (final char c, final int n)
  {
    final char [] buf = new char [n];
    for (int i = 0; i < n; i++)
      buf[i] = c;
    return new String (buf);
  }

  /* Replace the first occurrence of ch in pattern by value. */

  private static String substitute (final String pattern, final char ch, final String value)
  {
    final int i = pattern.indexOf (ch);
    if (i < 0)
      return pattern;
    final StringBuffer buf = new StringBuffer ();
    buf.append (pattern.substring (0, i));
    buf.append (value);
    buf.append (pattern.substring (i + 1));
    return buf.toString ();
  }

  private void outputStart (final List <Component> components)
  {
    final ChoicePattern choice = new ChoicePattern ();
    // Use the defined but unreferenced elements.
    // If there aren't any, use all defined elements.
    int mask = ELEMENT_REF | ELEMENT_DECL;
    for (;;)
    {
      boolean gotOne = false;
      for (final Map.Entry <String, Integer> entry : elementNameTable.entrySet ())
      {
        if ((entry.getValue ().intValue () & mask) == ELEMENT_DECL)
        {
          gotOne = true;
          choice.getChildren ().add (ref (elementDeclName (entry.getKey ())));
        }
      }
      if (gotOne)
        break;
      if (mask == ELEMENT_DECL)
        return;
      mask = ELEMENT_DECL;
    }
    components.add (new DefineComponent (DefineComponent.START, choice));
  }

  private void outputAny (final List <Component> components)
  {
    if (!hadAny)
      return;
    if (options.strictAny)
    {
      final DefineComponent dc = new DefineComponent (anyName, new TextPattern ());
      dc.setCombine (Combine.CHOICE);
      components.add (dc);
    }
    else
    {
      // any = (element * { attribute * { text }*, any } | text)*
      final CompositePattern group = new GroupPattern ();
      group.getChildren ().add (new ZeroOrMorePattern (new AttributePattern (new AnyNameNameClass (),
                                                                             new TextPattern ())));
      group.getChildren ().add (ref (anyName));
      final CompositePattern choice = new ChoicePattern ();
      choice.getChildren ().add (new ElementPattern (new AnyNameNameClass (), group));
      choice.getChildren ().add (new TextPattern ());
      components.add (new DefineComponent (anyName, new ZeroOrMorePattern (choice)));
    }
  }

  private void outputUndefinedElements (final List <Component> components)
  {
    final List <String> elementNames = new Vector <String> ();
    elementNames.addAll (elementNameTable.keySet ());
    Collections.sort (elementNames);
    for (final String elementName : elementNames)
    {
      if ((elementNameTable.get (elementName).intValue () & ELEMENT_DECL) == 0)
      {
        final DefineComponent dc = new DefineComponent (elementDeclName (elementName), new NotAllowedPattern ());
        dc.setCombine (Combine.CHOICE);
        components.add (dc);
      }
    }
  }

  static private Pattern ref (final String name)
  {
    return new RefPattern (name);
  }

  private void error (final String key)
  {
    er.error (key, null);
  }

  private void error (final String key, final String arg)
  {
    er.error (key, arg, null);
  }

  private void warning (final String key)
  {
    er.warning (key, null);
  }

  private void warning (final String key, final String arg)
  {
    er.warning (key, arg, null);
  }

  private static String [] valueType (Datatype datatype)
  {
    datatype = datatype.deref ();
    switch (datatype.getType ())
    {
      case Datatype.CDATA:
        return new String [] { "", "string" };
      case Datatype.TOKENIZED:
        return new String [] { WellKnownNamespaces.XML_SCHEMA_DATATYPES, ((TokenizedDatatype) datatype).getTypeName () };
    }
    return new String [] { "", "token" };
  }

  private Pattern convert (final ModelGroup mg) throws Exception
  {
    final ModelGroupOutput mgo = new ModelGroupOutput ();
    mg.accept (mgo);
    return mgo.pattern;
  }

  private Pattern convert (final Datatype dt) throws Exception
  {
    final DatatypeOutput dto = new DatatypeOutput ();
    dt.accept (dto);
    return dto.pattern;
  }

  private Pattern convert (final AttributeGroup ag) throws Exception
  {
    final GroupPattern group = new GroupPattern ();
    ag.accept (new AttributeGroupOutput (group));
    switch (group.getChildren ().size ())
    {
      case 0:
        return new EmptyPattern ();
      case 1:
        return group.getChildren ().get (0);
    }
    return group;
  }

  private NameClass convertQName (final String name, final boolean useDefault)
  {
    final int i = name.indexOf (':');
    if (i < 0)
      return new NameNameClass (useDefault ? defaultNamespace : "", name);
    final String prefix = name.substring (0, i);
    final String localName = name.substring (i + 1);
    String ns;
    if (prefix.equals ("xml"))
      ns = WellKnownNamespaces.XML;
    else
    {
      ns = prefixTable.get (prefix);
      if (ns.equals (""))
      {
        error ("UNDECLARED_PREFIX", prefix);
        ns = "##" + prefix;
        prefixTable.put (prefix, ns);
      }
    }
    final NameNameClass nnc = new NameNameClass (ns, localName);
    nnc.setPrefix (prefix);
    return nnc;
  }
}
