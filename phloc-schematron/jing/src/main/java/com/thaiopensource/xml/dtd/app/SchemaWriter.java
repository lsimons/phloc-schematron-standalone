package com.thaiopensource.xml.dtd.app;

import java.io.IOException;

import com.thaiopensource.xml.dtd.om.AttributeDefault;
import com.thaiopensource.xml.dtd.om.AttributeDefaultVisitor;
import com.thaiopensource.xml.dtd.om.AttributeGroup;
import com.thaiopensource.xml.dtd.om.AttributeGroupVisitor;
import com.thaiopensource.xml.dtd.om.Datatype;
import com.thaiopensource.xml.dtd.om.DatatypeVisitor;
import com.thaiopensource.xml.dtd.om.Def;
import com.thaiopensource.xml.dtd.om.Dtd;
import com.thaiopensource.xml.dtd.om.EnumGroup;
import com.thaiopensource.xml.dtd.om.EnumGroupVisitor;
import com.thaiopensource.xml.dtd.om.Flag;
import com.thaiopensource.xml.dtd.om.FlagRef;
import com.thaiopensource.xml.dtd.om.FlagVisitor;
import com.thaiopensource.xml.dtd.om.ModelGroup;
import com.thaiopensource.xml.dtd.om.ModelGroupVisitor;
import com.thaiopensource.xml.dtd.om.NameSpec;
import com.thaiopensource.xml.dtd.om.NameSpecVisitor;
import com.thaiopensource.xml.dtd.om.TopLevel;
import com.thaiopensource.xml.dtd.om.TopLevelVisitor;
import com.thaiopensource.xml.em.ExternalId;
import com.thaiopensource.xml.out.XmlWriter;

public class SchemaWriter implements
                         TopLevelVisitor,
                         ModelGroupVisitor,
                         AttributeGroupVisitor,
                         DatatypeVisitor,
                         EnumGroupVisitor,
                         FlagVisitor,
                         NameSpecVisitor,
                         AttributeDefaultVisitor
{
  private final XmlWriter w;

  public SchemaWriter (final XmlWriter writer)
  {
    this.w = writer;
  }

  public void writeDtd (final Dtd dtd) throws IOException
  {
    final String enc = dtd.getEncoding ();
    if (enc != null)
      w.writeXmlDecl (enc);
    w.startElement ("doctype");
    try
    {
      dtd.accept (this);
    }
    catch (final RuntimeException e)
    {
      throw e;
    }
    catch (final Exception e)
    {
      throw (IOException) e;
    }
    w.endElement ();
  }

  public void elementDecl (final NameSpec nameSpec, final ModelGroup modelGroup) throws Exception
  {
    w.startElement ("element");
    nameSpec.accept (this);
    modelGroup.accept (this);
    w.endElement ();
  }

  public void attlistDecl (final NameSpec nameSpec, final AttributeGroup attributeGroup) throws Exception
  {
    w.startElement ("attlist");
    nameSpec.accept (this);
    attributeGroup.accept (this);
    w.endElement ();
  }

  public void processingInstruction (final String target, final String value) throws Exception
  {
    w.startElement ("processingInstruction");
    w.attribute ("target", target);
    w.characters (value);
    w.endElement ();
  }

  public void comment (final String value) throws Exception
  {
    w.startElement ("comment");
    w.characters (value);
    w.endElement ();
  }

  public void modelGroupDef (final String name, final ModelGroup modelGroup) throws Exception
  {
    w.startElement ("modelGroup");
    w.attribute ("name", name);
    modelGroup.accept (this);
    w.endElement ();
  }

  public void attributeGroupDef (final String name, final AttributeGroup attributeGroup) throws Exception
  {
    w.startElement ("attributeGroup");
    w.attribute ("name", name);
    attributeGroup.accept (this);
    w.endElement ();
  }

  public void enumGroupDef (final String name, final EnumGroup enumGroup) throws Exception
  {
    w.startElement ("enumGroup");
    w.attribute ("name", name);
    enumGroup.accept (this);
    w.endElement ();
  }

  public void datatypeDef (final String name, final Datatype datatype) throws Exception
  {
    w.startElement ("datatype");
    w.attribute ("name", name);
    datatype.accept (this);
    w.endElement ();
  }

  public void flagDef (final String name, final Flag flag) throws Exception
  {
    w.startElement ("flag");
    w.attribute ("name", name);
    flag.accept (this);
    w.endElement ();
  }

  public void attributeDefaultDef (final String name, final AttributeDefault attributeDefault) throws Exception
  {
    w.startElement ("attributeDefault");
    w.attribute ("name", name);
    attributeDefault.accept (this);
    w.endElement ();
  }

  public void choice (final ModelGroup [] members) throws Exception
  {
    w.startElement ("choice");
    for (final ModelGroup member : members)
      member.accept (this);
    w.endElement ();
  }

  public void sequence (final ModelGroup [] members) throws Exception
  {
    w.startElement ("sequence");
    for (final ModelGroup member : members)
      member.accept (this);
    w.endElement ();
  }

  public void oneOrMore (final ModelGroup member) throws Exception
  {
    w.startElement ("oneOrMore");
    member.accept (this);
    w.endElement ();
  }

  public void zeroOrMore (final ModelGroup member) throws Exception
  {
    w.startElement ("zeroOrMore");
    member.accept (this);
    w.endElement ();
  }

  public void optional (final ModelGroup member) throws Exception
  {
    w.startElement ("optional");
    member.accept (this);
    w.endElement ();
  }

  public void modelGroupRef (final String name, final ModelGroup modelGroup) throws Exception
  {
    w.startElement ("modelGroupRef");
    w.attribute ("name", name);
    w.endElement ();
  }

  public void elementRef (final NameSpec nameSpec) throws Exception
  {
    w.startElement ("elementRef");
    nameSpec.accept (this);
    w.endElement ();
  }

  public void pcdata () throws Exception
  {
    w.startElement ("pcdata");
    w.endElement ();
  }

  public void any () throws Exception
  {
    w.startElement ("any");
    w.endElement ();
  }

  public void attribute (final NameSpec nameSpec, final Datatype datatype, final AttributeDefault attributeDefault) throws Exception
  {
    w.startElement ("attribute");
    nameSpec.accept (this);
    datatype.accept (this);
    attributeDefault.accept (this);
    w.endElement ();
  }

  public void attributeGroupRef (final String name, final AttributeGroup attributeGroup) throws Exception
  {
    w.startElement ("attributeGroupRef");
    w.attribute ("name", name);
    w.endElement ();
  }

  public void enumValue (final String value) throws Exception
  {
    w.startElement ("enum");
    w.characters (value);
    w.endElement ();
  }

  public void enumGroupRef (final String name, final EnumGroup enumGroup) throws Exception
  {
    w.startElement ("enumGroupRef");
    w.attribute ("name", name);
    w.endElement ();
  }

  public void cdataDatatype () throws IOException
  {
    w.startElement ("cdata");
    w.endElement ();
  }

  public void tokenizedDatatype (final String typeName) throws IOException
  {
    w.startElement ("tokenized");
    w.attribute ("name", typeName);
    w.endElement ();
  }

  public void enumDatatype (final EnumGroup enumGroup) throws Exception
  {
    w.startElement ("tokenized");
    enumGroup.accept (this);
    w.endElement ();
  }

  public void notationDatatype (final EnumGroup enumGroup) throws Exception
  {
    w.startElement ("tokenized");
    w.attribute ("name", "NOTATION");
    enumGroup.accept (this);
    w.endElement ();
  }

  public void datatypeRef (final String name, final Datatype datatype) throws IOException
  {
    w.startElement ("datatypeRef");
    w.attribute ("name", name);
    w.endElement ();
  }

  public void flagRef (final String name, final Flag flag) throws IOException
  {
    w.startElement ("flagRef");
    w.attribute ("name", name);
    w.endElement ();
  }

  public void include () throws IOException
  {
    w.startElement ("include");
    w.endElement ();
  }

  public void ignore () throws IOException
  {
    w.startElement ("ignore");
    w.endElement ();
  }

  public void includedSection (final Flag flag, final TopLevel [] contents) throws Exception
  {
    w.startElement ("includedSection");
    if (flag instanceof FlagRef)
      w.attribute ("flag", ((FlagRef) flag).getName ());
    for (final TopLevel content : contents)
      content.accept (this);
    w.endElement ();
  }

  public void ignoredSection (final Flag flag, final String contents) throws Exception
  {
    w.startElement ("ignoredSection");
    if (flag instanceof FlagRef)
      w.attribute ("flag", ((FlagRef) flag).getName ());
    w.characters (contents);
    w.endElement ();
  }

  public void externalIdDef (final String name, final ExternalId xid) throws IOException
  {
    w.startElement ("externalId");
    w.attribute ("name", name);
    externalId (xid);
    w.endElement ();
  }

  public void externalIdRef (final String name,
                             final ExternalId xid,
                             final String uri,
                             final String encoding,
                             final TopLevel [] contents) throws Exception
  {
    w.startElement ("externalIdRef");
    w.attribute ("name", name);
    for (final TopLevel content : contents)
      content.accept (this);
    w.endElement ();
  }

  public void internalEntityDecl (final String name, final String value) throws Exception
  {
    w.startElement ("internalEntity");
    w.attribute ("name", name);
    final boolean useCharRef = value.length () == 1 && value.charAt (0) >= 0x80;
    w.characters (value, useCharRef);
    w.endElement ();
  }

  public void externalEntityDecl (final String name, final ExternalId xid) throws IOException
  {
    w.startElement ("externalEntity");
    w.attribute ("name", name);
    externalId (xid);
    w.endElement ();
  }

  public void notationDecl (final String name, final ExternalId xid) throws IOException
  {
    w.startElement ("notation");
    w.attribute ("name", name);
    externalId (xid);
    w.endElement ();
  }

  private void externalId (final ExternalId xid) throws IOException
  {
    attributeIfNotNull ("system", xid.getSystemId ());
    attributeIfNotNull ("public", xid.getPublicId ());
    // this messes up testing
    // attributeIfNotNull("xml:base", xid.getBaseUri());
  }

  private void attributeIfNotNull (final String name, final String value) throws IOException
  {
    if (value != null)
      w.attribute (name, value);
  }

  public void nameSpecDef (final String name, final NameSpec nameSpec) throws Exception
  {
    w.startElement ("nameSpec");
    w.attribute ("name", name);
    nameSpec.accept (this);
    w.endElement ();
  }

  public void name (final String value) throws IOException
  {
    w.startElement ("name");
    w.characters (value);
    w.endElement ();
  }

  public void nameSpecRef (final String name, final NameSpec nameSpec) throws Exception
  {
    w.startElement ("nameSpecRef");
    w.attribute ("name", name);
    w.endElement ();
  }

  public void overriddenDef (final Def def, final boolean duplicate) throws Exception
  {
    w.startElement ("overridden");
    if (duplicate)
    {
      w.startElement ("duplicate");
      w.attribute ("name", def.getName ());
      w.endElement ();
    }
    else
      def.accept (this);
    w.endElement ();
  }

  public void paramDef (final String name, final String value) throws IOException
  {
    w.startElement ("param");
    w.attribute ("name", name);
    w.characters (value);
    w.endElement ();
  }

  public void defaultValue (final String value) throws Exception
  {
    w.startElement ("default");
    w.characters (value);
    w.endElement ();
  }

  public void fixedValue (final String value) throws Exception
  {
    w.startElement ("fixed");
    w.characters (value);
    w.endElement ();
  }

  public void impliedValue () throws Exception
  {
    w.startElement ("implied");
    w.endElement ();
  }

  public void requiredValue () throws Exception
  {
    w.startElement ("required");
    w.endElement ();
  }

  public void attributeDefaultRef (final String name, final AttributeDefault attributeDefault) throws Exception
  {
    w.startElement ("attributeDefaultRef");
    w.attribute ("name", name);
    w.endElement ();
  }
}
