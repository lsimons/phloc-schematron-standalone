package com.thaiopensource.relaxng.output.xsd.basic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.thaiopensource.relaxng.edit.SourceLocation;

public class Schema extends Annotated
{
  private final String uri;
  private final String encoding;
  private Schema parent;
  private final List <TopLevel> topLevel = new Vector <TopLevel> ();
  private final Map <String, GroupDefinition> groupMap;
  private final Map <String, AttributeGroupDefinition> attributeGroupMap;
  private final Map <String, SimpleTypeDefinition> simpleTypeMap;
  private final List <Schema> subSchemas;
  private final List <Comment> leadingComments = new Vector <Comment> ();
  private final List <Comment> trailingComments = new Vector <Comment> ();

  public Schema (final SourceLocation location, final Annotation annotation, final String uri, final String encoding)
  {
    super (location, annotation);
    this.uri = uri;
    this.encoding = encoding;
    this.groupMap = new HashMap <String, GroupDefinition> ();
    this.attributeGroupMap = new HashMap <String, AttributeGroupDefinition> ();
    this.simpleTypeMap = new HashMap <String, SimpleTypeDefinition> ();
    this.subSchemas = new Vector <Schema> ();
    this.subSchemas.add (this);
  }

  private Schema (final SourceLocation location,
                  final Annotation annotation,
                  final String uri,
                  final String encoding,
                  final Schema parent)
  {
    super (location, annotation);
    this.parent = parent;
    this.uri = uri;
    this.encoding = encoding;
    this.groupMap = parent.groupMap;
    this.attributeGroupMap = parent.attributeGroupMap;
    this.simpleTypeMap = parent.simpleTypeMap;
    this.subSchemas = parent.subSchemas;
    this.subSchemas.add (this);
  }

  public String getUri ()
  {
    return uri;
  }

  public String getEncoding ()
  {
    return encoding;
  }

  public Schema getParent ()
  {
    return parent;
  }

  public void defineGroup (final String name,
                           final Particle particle,
                           final SourceLocation location,
                           final Annotation annotation)
  {
    final GroupDefinition def = new GroupDefinition (location, annotation, this, name, particle);
    topLevel.add (def);
    groupMap.put (name, def);
  }

  public void defineAttributeGroup (final String name,
                                    final AttributeUse attributeUses,
                                    final SourceLocation location,
                                    final Annotation annotation)
  {
    final AttributeGroupDefinition def = new AttributeGroupDefinition (location, annotation, this, name, attributeUses);
    topLevel.add (def);
    attributeGroupMap.put (name, def);
  }

  public void defineSimpleType (final String name,
                                final SimpleType simpleType,
                                final SourceLocation location,
                                final Annotation annotation)
  {
    final SimpleTypeDefinition def = new SimpleTypeDefinition (location, annotation, this, name, simpleType);
    topLevel.add (def);
    simpleTypeMap.put (name, def);
  }

  public void addRoot (final Particle particle, final SourceLocation location, final Annotation annotation)
  {
    topLevel.add (new RootDeclaration (location, annotation, particle));
  }

  public Schema addInclude (final String uri,
                            final String encoding,
                            final SourceLocation location,
                            final Annotation annotation)
  {
    final Schema included = new Schema (location, annotation, uri, encoding, this);
    topLevel.add (new Include (location, annotation, included));
    return included;
  }

  public void addComment (final String content, final SourceLocation location)
  {
    topLevel.add (new Comment (location, content));
  }

  public GroupDefinition getGroup (final String name)
  {
    return groupMap.get (name);
  }

  public SimpleTypeDefinition getSimpleType (final String name)
  {
    return simpleTypeMap.get (name);
  }

  public AttributeGroupDefinition getAttributeGroup (final String name)
  {
    return attributeGroupMap.get (name);
  }

  public void accept (final SchemaVisitor visitor)
  {
    for (final TopLevel t : this.topLevel)
      t.accept (visitor);
  }

  public List <Schema> getSubSchemas ()
  {
    return subSchemas;
  }

  public List <Comment> getLeadingComments ()
  {
    return leadingComments;
  }

  public List <Comment> getTrailingComments ()
  {
    return trailingComments;
  }

  @Override
  public boolean equals (final Object obj)
  {
    return obj == this;
  }

  @Override
  public int hashCode ()
  {
    return System.identityHashCode (this);
  }
}
