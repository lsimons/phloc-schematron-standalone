package com.thaiopensource.relaxng.output.xsd.basic;

public class AbstractSchemaVisitor implements SchemaVisitor
{
  public void visitGroup (final GroupDefinition def)
  {}

  public void visitAttributeGroup (final AttributeGroupDefinition def)
  {}

  public void visitSimpleType (final SimpleTypeDefinition def)
  {}

  public void visitInclude (final Include include)
  {}

  public void visitRoot (final RootDeclaration decl)
  {}

  public void visitComment (final Comment comment)
  {}
}
